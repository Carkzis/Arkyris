package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class IrisViewModel extends AndroidViewModel {

    private EntryRepository mRepository;
    private LiveData<List<EntryItem>> mAllEntries;

    public IrisViewModel (Application application) {
        super(application);
        mRepository = new EntryRepository(application);
        mAllEntries = mRepository.getAllEntries();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<EntryItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(EntryItem entry) { mRepository.insert(entry); }

    public void deleteAll() { mRepository.deleteAll(); }

    public void deleteEntry(EntryItem entry) { mRepository.deleteEntry(entry); }
}
