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
            mArkeEntryDao.insertAll(entries);
        });
    }

    // wrapper for retrieving SharedPreference username
    public MutableLiveData<String> getAccountName() {
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    public void refreshArkeCache() {
        // This will load the items from the database
        Call<List<ArkeEntryItem>> call = entryService.getPublicEntries();
        call.enqueue(new Callback<List<ArkeEntryItem>>() {
            @Override
            public void onResponse(Call<List<ArkeEntryItem>> call, Response<List<ArkeEntryItem>> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entries called.");
                    entriesList = response.body();
                    deleteAll();
                    insertAll(entriesList);
                    // refresh the local cache for Arke
                }
            }

            @Override
            public void onFailure(Call<List<ArkeEntryItem>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                mConnectionError.postValue(true);
            }

        });
    }

}
