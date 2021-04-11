package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ArkeViewModel extends AndroidViewModel {

    private EntryRepository mRepository;
    private LiveData<List<ArkeItem>> mAllEntries;

    public ArkeViewModel (Application application) {
        super(application);
        mRepository = new EntryRepository(application);
        mAllEntries = mRepository.getAllEntries();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<ArkeItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repositor's insert() method,
    // hides implementation of insert() from UI
    public void insert(ArkeItem entry) { mRepository.insert(entry); }

    public void deleteAll() { mRepository.deleteAll(); }

    public void deleteWord(ArkeItem entry) { mRepository.deleteEntry(entry); }

}
