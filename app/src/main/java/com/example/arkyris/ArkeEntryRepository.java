package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ArkeEntryRepository {

    // add member variables for DAO and list of words
    private ArkeEntryDao mArkeEntryDao;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;

    // constructor to get handle to db and initialise member variables
    ArkeEntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mArkeEntryDao = db.arkeEntryDao();
        mPublicEntries = mArkeEntryDao.getAllPublicEntries();
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

    public void deleteEntry(ArkeEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.deleteEntry(entry);
        });
    }

    public void updatePublic(ArkeEntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mArkeEntryDao.updatePublic(entry);
        });
    }

}
