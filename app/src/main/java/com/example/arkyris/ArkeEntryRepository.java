package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ArkeEntryRepository {

    // add member variables for DAO and list of words
    private ArkeEntryDao mArkeEntryDao;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;

    SharedPreferences preferences;
    private MutableLiveData<String> mAccountName;

    // constructor to get handle to db and initialise member variables
    ArkeEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mArkeEntryDao = db.arkeEntryDao();
        mPublicEntries = mArkeEntryDao.getAllPublicEntries();

        // Initialise variables for getting account name currently logged in
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<String>();
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

    // wrapper for retrieving SharedPreference username
    public MutableLiveData<String> getAccountName() {
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

}
