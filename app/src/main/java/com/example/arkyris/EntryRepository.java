package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntryRepository {

    // add member variables for DAO and list of words
    private EntryDao mEntryDao;
    private LiveData<List<ArkeItem>> mAllEntries;

    // constructor to get handle to db and initialise member variables
    EntryRepository(Application application) {
        ArkyrisRoomDatabase db = ArkyrisRoomDatabase.getDatabase(application);
        mEntryDao = db.entryDao();
        mAllEntries = mEntryDao.getAllEntries();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<ArkeItem>> getAllEntries() {
        return mAllEntries;
    }

    // wrapper for insert() using threads
    public void insert(ArkeItem entry) {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.insert(entry);
        });
    }

    public void deleteAll() {
        ArkyrisRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.deleteAll();
        });
    }

    public void deleteEntry(ArkeItem entry) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEntryDao.deleteEntry(entry);
        });
    }


}
