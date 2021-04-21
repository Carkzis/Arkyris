package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class IrisEntryRepository {

    // add member variables for DAO and list of words
    private IrisEntryDao mIrisEntryDao;
    private LiveData<List<IrisEntryItem>> mAllEntries;
    private LiveData<List<IrisEntryItem>> mPublicEntries;

    // constructor to get handle to db and initialise member variables
    IrisEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mIrisEntryDao = db.entryDao();
        mAllEntries = mIrisEntryDao.getAllEntries();
        mPublicEntries = mIrisEntryDao.getAllPublicEntries();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<IrisEntryItem>> getAllEntries() {
        return mAllEntries;
    }

    LiveData<List<IrisEntryItem>> getAllPublicEntries() {
        return mPublicEntries;
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


}
