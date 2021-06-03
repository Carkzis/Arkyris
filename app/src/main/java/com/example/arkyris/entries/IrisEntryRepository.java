package com.example.arkyris.entries;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IrisEntryRepository {

    private static final String LOG_TAG = IrisEntryRepository.class.getSimpleName();

    // add member variables for DAO and list of words
    private final IrisEntryDao mIrisEntryDao;
    private final EntryService mEntryService = APIUtils.getEntryService();

    private final MutableLiveData<Boolean> mConnectionError;
    private final MutableLiveData<Boolean> mLoadingComplete;
    private final MutableLiveData<Boolean> mEntryAdded;
    private final MutableLiveData<Boolean> mEntryDeleted;
    private final MutableLiveData<String> mPublicOrPrivate;
    private final MutableLiveData<String> mAccountName;

    private List<IrisEntryItem> mEntriesList = new ArrayList<>();
    private final LiveData<List<IrisEntryItem>> mAllEntries;

    private final String mUsername;

    // constructor to get handle to db and initialise member variables
    IrisEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mIrisEntryDao = db.irisEntryDao();
        mAllEntries = mIrisEntryDao.getAllEntries();

        // Initialise variables for getting account name currently logged in
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mUsername = mPreferences.getString("username", null);
        mAccountName = new MutableLiveData<>();
        mConnectionError = new MutableLiveData<>();
        mLoadingComplete = new MutableLiveData<>();
        mEntryAdded = new MutableLiveData<>();
        mEntryDeleted = new MutableLiveData<>();
        mPublicOrPrivate = new MutableLiveData<>();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<IrisEntryItem>> getAllEntries() {
        return mAllEntries;
    }

    // wrapper for insert() using threads
    public void insert(IrisEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> mIrisEntryDao.insert(entry));
    }

    public void deleteAll() {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(mIrisEntryDao::deleteAll);
    }

    public void insertAll(List<IrisEntryItem> entries) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mIrisEntryDao.deleteAll();
            mIrisEntryDao.insertAll(entries);

            // Delay method to prevent loading complete value reaching the
            // fragment until all the items/recycler view has been updated
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mLoadingComplete.postValue(true);
        });
    }

    // wrapper for retrieving SharedPreference username
    public MutableLiveData<String> getAccountName() {
        mAccountName.postValue(mUsername);
        return mAccountName;
    }

    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<Boolean> getLoadingComplete() {
        return mLoadingComplete;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        return mEntryAdded;
    }

    public MutableLiveData<Boolean> getEntryDeleted() {
        return mEntryDeleted;
    }

    public MutableLiveData<String> getIsPublic() {
        return mPublicOrPrivate;
    }

    /**
     * Refresh the Iris cache when called via the Iris fragment, or within the
     * IrisEntryRepository class.
     */
    public void refreshIrisCache(boolean fromArke) {
        // This will load the items from the database
        Call<List<IrisEntryItem>> call = mEntryService.getPrivateEntries(mUsername);
        call.enqueue(new Callback<List<IrisEntryItem>>() {
            @Override
            public void onResponse(@NotNull Call<List<IrisEntryItem>> call,
                                   @NotNull Response<List<IrisEntryItem>> response) {
                if (response.isSuccessful()) {
                    mEntriesList = response.body();
                    // This could be replaced by a DiffUtil.
                    //deleteAll();
                    Log.e(LOG_TAG, String.valueOf(response.body()));
                    insertAll(mEntriesList);
                }
            }
            @Override
            public void onFailure(@NotNull Call<List<IrisEntryItem>> call,
                                  @NotNull Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                // If this method is called from the Arke fragment, a connection error will
                // have already been generated.
                if (!fromArke) {
                    mConnectionError.postValue(true);
                }
                mLoadingComplete.postValue(true);
            }
        });
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int colour, int isPublic) {
        IrisEntryItem entry = new IrisEntryItem(mAccountName.getValue(), colour, isPublic);
        Call<IrisEntryItem> call = mEntryService.addEntry(entry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(@NotNull Call<IrisEntryItem> call,
                                   @NotNull Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryAdded.postValue(true);
                    refreshIrisCache(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<IrisEntryItem> call,
                                  @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }

    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        // this will pass change the flag of an item to deleted, so it will stop
        // being requested from REST
        HashMap<String, String> updateEntry = new HashMap<>();
        updateEntry.put("deleted", "1");
        Call<IrisEntryItem> call = mEntryService.updatePublic(entryItem.getRemoteId(), updateEntry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(@NotNull Call<IrisEntryItem> call,
                                   @NotNull Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryDeleted.postValue(true);
                    // refresh after everything has been done
                    refreshIrisCache(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<IrisEntryItem> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }

    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        // this will pass change the flag of an item to deleted, so it will stop
        // being requested from REST
        HashMap<String, String> updateEntry = new HashMap<>();
        updateEntry.put("public", String.valueOf(isPublic));
        Call<IrisEntryItem> call = mEntryService.updatePublic(entryItem.getRemoteId(), updateEntry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(@NotNull Call<IrisEntryItem> call,
                                   @NotNull Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    if (isPublic == 1) {
                        mPublicOrPrivate.postValue("public");
                    } else {
                        mPublicOrPrivate.postValue("private");
                    }
                    // refresh after everything has been done
                    refreshIrisCache(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<IrisEntryItem> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }
}
