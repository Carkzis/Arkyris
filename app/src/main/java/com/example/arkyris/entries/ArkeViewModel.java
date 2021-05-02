package com.example.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ArkeViewModel extends AndroidViewModel {

    private ArkeEntryRepository mRepository;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;
    private MutableLiveData<String> mAccountName;

    public ArkeViewModel (Application application) {
        super(application);
        mRepository = new ArkeEntryRepository(application);
        mPublicEntries = mRepository.getAllPublicEntries();
        mAccountName = mRepository.getAccountName();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<ArkeEntryItem>> getPublicEntries() { return mPublicEntries; }

    // getter method to retrieve account name from repository shared preferences
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<String>();
        }
        return mAccountName;
    }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(ArkeEntryItem entry) { mRepository.insert(entry); }

    public void deleteAll() { mRepository.deleteAll(); }

}
