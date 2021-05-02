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
        mConnectionError = new MutableLiveData<Boolean>();
        mLoadingComplete = new MutableLiveData<Boolean>();
        mEntryAdded = new MutableLiveData<Boolean>();
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
            mIrisEntryDao.insertAll(entries);
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

    public MutableLiveData<Boolean> getLoadingComplete() {
        return mLoadingComplete;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        return mEntryAdded;
    }

    public void refreshIrisCache() {
        // This will load the items from the database
        Call<List<IrisEntryItem>> call = entryService.getPrivateEntries(mAccountName.getValue());
        call.enqueue(new Callback<List<IrisEntryItem>>() {
            @Override
            public void onResponse(Call<List<IrisEntryItem>> call, Response<List<IrisEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    insertAll(entriesList);
                    mLoadingComplete.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<List<IrisEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mConnectionError.postValue(true);
                mLoadingComplete.postValue(true);
            }
        });
    }

}
