package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntryRepository {

    // add member variables for DAO and list of words
    private EntryDao mEntryDao;
    private LiveData<List<EntryItem>> mAllEntries;
    private LiveData<List<EntryItem>> mPublicEntries;

    // constructor to get handle to db and initialise member variables
    EntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mEntryDao = db.entryDao();
        mAllEntries = mEntryDao.getAllEntries();
        mPublicEntries = mEntryDao.getAllPublicEntries();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<EntryItem>> getAllEntries() {
        return mAllEntries;
    }

    LiveData<List<EntryItem>> getAllPublicEntries() {
        return mPublicEntries;
    }

    // wrapper for insert() using threads
    public void insert(EntryItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.insert(entry);
        });
    }

    public void deleteAll() {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.deleteAll();
        });
    }

    public void deleteEntry(EntryItem entry) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.deleteEntry(entry);
        });
    }


}
