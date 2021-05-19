package com.example.arkyris.entries;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArkeEntryRepository {

    private static final String LOG_TAG = ArkeEntryRepository.class.getSimpleName();

    // add member variables for DAO and list of words
    private ArkeEntryDao mArkeEntryDao;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;
    EntryService entryService = APIUtils.getEntryService();
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<String> mLoadingOutcome;
    private MutableLiveData<Boolean> mEntryAdded;

    SharedPreferences preferences;
    private MutableLiveData<String> mAccountName;
    List<ArkeEntryItem> entriesList = new ArrayList<ArkeEntryItem>();

    // constructor to get handle to db and initialise member variables
    ArkeEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mArkeEntryDao = db.arkeEntryDao();
        mPublicEntries = mArkeEntryDao.getAllPublicEntries();

        // Initialise variables for getting account name currently logged in
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<String>();
        mConnectionError = new MutableLiveData<Boolean>();
        mLoadingOutcome = new MutableLiveData<String>();
        mEntryAdded = new MutableLiveData<Boolean>();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<ArkeEntryItem>> getAllPublicEntries() {
        return mPublicEntries;
    }

    // wrapper for insert() using threads
    public void insert(ArkeEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.insert(entry);
        });
    }

    public void deleteAll() {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.deleteAll();
        });
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
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<String> getLoadingOutcome() {
        return mLoadingOutcome;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        return mEntryAdded;
    }

    public void refreshArkeCache() {
        // This will load the items from the database
        Call<List<ArkeEntryItem>> call = entryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @Override
            public void onResponse(Call<List<ArkeEntryItem>> call, Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    entriesList = response.body();
                    // This could be replaced by a DiffUtil.
                    //deleteAll();
                    Log.e(LOG_TAG, String.valueOf(response.body()));
                    insertAll(entriesList);
                }
            }

            @Override
            public void onFailure(Call<List<ArkeEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mConnectionError.postValue(true);
                mLoadingOutcome.postValue("error");
            }
        });
    }

    /**
     * Add colour to the backend postgreSQL database
     */
    public void addRemoteEntry(int colour) {
        ArkeEntryItem entry = new ArkeEntryItem(mAccountName.getValue(), colour, 1);
        Call<ArkeEntryItem> call = entryService.addEntry(entry);
        call.enqueue(new Callback<ArkeEntryItem>() {
            @Override
            public void onResponse(Call<ArkeEntryItem> call, Response<ArkeEntryItem> response) {
                if (response.isSuccessful()) {
                    mEntryAdded.postValue(true);
                    refreshArkeCache();
                }
            }

            @Override
            public void onFailure(Call<ArkeEntryItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
                mLoadingOutcome.postValue("error");
            }

        });
    }

}
