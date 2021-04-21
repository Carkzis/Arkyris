package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class IrisViewModel extends AndroidViewModel {

    private IrisEntryRepository mRepository;
    private LiveData<List<IrisEntryItem>> mAllEntries;

    public IrisViewModel (Application application) {
        super(application);
        mRepository = new IrisEntryRepository(application);
        mAllEntries = mRepository.getAllEntries();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<IrisEntryItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(IrisEntryItem entry) { mRepository.insert(entry); }

    // This is only for testing purposes
    public void deleteAll() { mRepository.deleteAll(); }

    // Delete a single entry
    public void deleteEntry(IrisEntryItem entry) { mRepository.deleteEntry(entry); }

    // Toggle the public option for an individual entry on or off
    public void updatePublic(IrisEntryItem entry) { mRepository.updatePublic(entry); }
}
