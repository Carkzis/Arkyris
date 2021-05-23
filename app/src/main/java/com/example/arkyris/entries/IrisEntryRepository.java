package com.example.arkyris.entries;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IrisEntryRepository {

    private static final String LOG_TAG = IrisEntryRepository.class.getSimpleName();

    // add member variables for DAO and list of words
    private IrisEntryDao mIrisEntryDao;
    private LiveData<List<IrisEntryItem>> mAllEntries;
    EntryService entryService = APIUtils.getEntryService();
    List<IrisEntryItem> entriesList = new ArrayList<IrisEntryItem>();
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Boolean> mLoadingComplete;
    private MutableLiveData<Boolean> mEntryAdded;
    private MutableLiveData<Boolean> mEntryDeleted;
    private MutableLiveData<String> mPublicOrPrivate;
    String username;

    SharedPreferences preferences;
    private MutableLiveData<String> mAccountName;

    // constructor to get handle to db and initialise member variables
    IrisEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mIrisEntryDao = db.irisEntryDao();
        mAllEntries = mIrisEntryDao.getAllEntries();

        // Initialise variables for getting account name currently logged in
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<String>();
        username = preferences.getString("username", null);
        mConnectionError = new MutableLiveData<Boolean>();
        mLoadingComplete = new MutableLiveData<Boolean>();
        mEntryAdded = new MutableLiveData<Boolean>();
        mEntryDeleted = new MutableLiveData<Boolean>();
        mPublicOrPrivate = new MutableLiveData<String>();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<IrisEntryItem>> getAllEntries() {
        return mAllEntries;
    }

    // wrapper for insert() using threads
    public void insert(IrisEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mIrisEntryDao.insert(entry);
        });
    }

    public void deleteAll() {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mIrisEntryDao.deleteAll();
        });
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
        mAccountName.postValue(username);
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
        Call<List<IrisEntryItem>> call = entryService.getPrivateEntries(username);
        call.enqueue(new Callback<List<IrisEntryItem>>() {
            @Override
            public void onResponse(Call<List<IrisEntryItem>> call, Response<List<IrisEntryItem>> response) {
                if (response.isSuccessful()) {
                    entriesList = response.body();
                    // This could be replaced by a DiffUtil.
                    //deleteAll();
                    Log.e(LOG_TAG, String.valueOf(response.body()));
                    insertAll(entriesList);
                }
            }
            @Override
            public void onFailure(Call<List<IrisEntryItem>> call, Throwable t) {
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
        Call<IrisEntryItem> call = entryService.addEntry(entry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(Call<IrisEntryItem> call, Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryAdded.postValue(true);
                    refreshIrisCache(false);
                }
            }

            @Override
            public void onFailure(Call<IrisEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }

    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        // this will pass change the flag of an item to deleted, so it will stop
        // being requested from REST
        HashMap<String, String> updateEntry = new HashMap<String, String>();
        updateEntry.put("deleted", "1");
        Call<IrisEntryItem> call = entryService.updatePublic(entryItem.getRemoteId(), updateEntry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(Call<IrisEntryItem> call, Response<IrisEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryDeleted.postValue(true);
                    // refresh after everything has been done
                    refreshIrisCache(false);
                }
            }

            @Override
            public void onFailure(Call<IrisEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }

    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        // this will pass change the flag of an item to deleted, so it will stop
        // being requested from REST
        HashMap<String, String> updateEntry = new HashMap<String, String>();
        updateEntry.put("public", String.valueOf(isPublic));
        Call<IrisEntryItem> call = entryService.updatePublic(entryItem.getRemoteId(), updateEntry);
        call.enqueue(new Callback<IrisEntryItem>() {
            @Override
            public void onResponse(Call<IrisEntryItem> call, Response<IrisEntryItem> response) {
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
            public void onFailure(Call<IrisEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }

        });
    }
}
