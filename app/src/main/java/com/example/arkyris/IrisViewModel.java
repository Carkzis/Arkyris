package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class IrisViewModel extends AndroidViewModel {

    private IrisEntryRepository mRepository;
    private LiveData<List<IrisEntryItem>> mAllEntries;
    private MutableLiveData<String> mAccountName;

    public IrisViewModel (Application application) {
        super(application);
        mRepository = new IrisEntryRepository(application);
        mAllEntries = mRepository.getAllEntries();
        mAccountName = mRepository.getAccountName();
    }

    // getter method to retrieve account name from repository shared preferences
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<String>();
        }
        return mAccountName;
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<IrisEntryItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(IrisEntryItem entry) { mRepository.insert(entry); }

    // This is only for testing purposes
    public void deleteAll() { mRepository.deleteAll(); }



}
