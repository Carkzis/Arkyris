package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class IrisEntryRepository {

    // add member variables for DAO and list of words
    private IrisEntryDao mIrisEntryDao;
    private LiveData<List<IrisEntryItem>> mAllEntries;

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

    public void deleteEntry(IrisEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mIrisEntryDao.deleteEntry(entry);
        });
    }

    public void updatePublic(IrisEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mIrisEntryDao.updatePublic(entry);
        });
    }

    // wrapper for retrieving SharedPreference username
    public MutableLiveData<String> getAccountName() {
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

}
