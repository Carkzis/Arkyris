package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ArkeViewModel extends AndroidViewModel {

    private ArkeEntryRepository mRepository;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;

    public ArkeViewModel (Application application) {
        super(application);
        mRepository = new ArkeEntryRepository(application);
        mPublicEntries = mRepository.getAllPublicEntries();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<ArkeEntryItem>> getPublicEntries() { return mPublicEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(ArkeEntryItem entry) { mRepository.insert(entry); }

    // This is only for testing purposes
    public void deleteAll() { mRepository.deleteAll(); }

    // Delete a single entry
    public void deleteEntry(ArkeEntryItem entry) { mRepository.deleteEntry(entry); }

    // Toggle the public option for an individual entry on or off
    public void updatePublic(ArkeEntryItem entry) { mRepository.updatePublic(entry); }

}
