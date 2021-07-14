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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArkeEntryRepository {

    private static final String LOG_TAG = ArkeEntryRepository.class.getSimpleName();

    // add member variables for DAO and list of words
    private final ArkeEntryDao mArkeEntryDao;
    private final EntryService mEntryService = APIUtils.getEntryService();
    private final SharedPreferences mPreferences;

    private final MutableLiveData<String> mLoadingOutcome;
    private final MutableLiveData<Boolean> mEntryAdded;
    private final MutableLiveData<String> mAccountName;

    private List<ArkeEntryItem> mEntriesList = new ArrayList<>();
    private final LiveData<List<ArkeEntryItem>> mPublicEntries;

    // constructor to get handle to db and initialise member variables
    ArkeEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mArkeEntryDao = db.arkeEntryDao();
        mPublicEntries = mArkeEntryDao.getAllPublicEntries();

        // Initialise variables for getting account name currently logged in
        mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<>();
        mLoadingOutcome = new MutableLiveData<>();
        mEntryAdded = new MutableLiveData<>();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<ArkeEntryItem>> getAllPublicEntries() {
        return mPublicEntries;
    }

    // wrapper for insert() using threads
    public void insert(ArkeEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> mArkeEntryDao.insert(entry));
    }

    public void insertAll(List<ArkeEntryItem> entries) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.deleteAll();
            mArkeEntryDao.insertAll(entries);
            // Delay method to prevent loading complete value reaching the
            // fragment until all the items/recycler view has been updated
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mLoadingOutcome.postValue("complete");
        });
    }

    // wrapper for retrieving SharedPreference username
    public MutableLiveData<String> getAccountName() {
        String username = mPreferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    public MutableLiveData<String> getLoadingOutcome() {
        return mLoadingOutcome;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        return mEntryAdded;
    }

    public void refreshArkeCache() {
        // This will load the items from the database
        Call<List<ArkeEntryItem>> call = mEntryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @Override
            public void onResponse(@NotNull Call<List<ArkeEntryItem>> call,
                                   @NotNull Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    mEntriesList = response.body();
                    // This could be replaced by a DiffUtil.
                    //deleteAll();
                    Log.e(LOG_TAG, String.valueOf(response.body()));
                    insertAll(mEntriesList);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ArkeEntryItem>> call, @NotNull Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mLoadingOutcome.postValue("error");
            }
        });
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int colour) {
        ArkeEntryItem entry = new ArkeEntryItem(mAccountName.getValue(), colour, 1);
        Call<ArkeEntryItem> call = mEntryService.addEntry(entry);
        call.enqueue(new Callback<ArkeEntryItem>() {
            @Override
            public void onResponse(@NotNull Call<ArkeEntryItem> call,
                                   @NotNull Response<ArkeEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryAdded.postValue(true);
                    refreshArkeCache();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArkeEntryItem> call,
                                  @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mLoadingOutcome.postValue("error");
            }

        });
    }

}
