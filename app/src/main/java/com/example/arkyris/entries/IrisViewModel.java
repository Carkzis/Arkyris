package com.example.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

public class IrisViewModel extends AndroidViewModel {

    private IrisEntryRepository mRepository;
    private LiveData<List<IrisEntryItem>> mAllEntries;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Boolean> mLoadingComplete;
    private MutableLiveData<Boolean> mEntryAdded;

    // Random colours
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public IrisViewModel (Application application) {
        super(application);
        mRepository = new IrisEntryRepository(application);
        mAllEntries = mRepository.getAllEntries();
        mAccountName = mRepository.getAccountName();
        mConnectionError = mRepository.getConnectionError();
        mLoadingComplete = mRepository.getLoadingComplete();
        mEntryAdded = mRepository.getEntryAdded();
    }

    // getter method to retrieve account name from repository shared preferences
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<String>();
        }
        return mAccountName;
    }

    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<Boolean>();
        }
        return mConnectionError;
    }

    public MutableLiveData<Boolean> getLoadingComplete() {
        if (mLoadingComplete == null) {
            mLoadingComplete = new MutableLiveData<Boolean>();
        }
        return mLoadingComplete;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        if (mEntryAdded == null) {
            mEntryAdded = new MutableLiveData<Boolean>();
        }
        return mEntryAdded;
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<IrisEntryItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(IrisEntryItem entry) { mRepository.insert(entry); }

    // This is only for testing purposes
    public void deleteAll() { mRepository.deleteAll(); }

    // refresh cache
    public void refreshIrisCache() { mRepository.refreshIrisCache(); }

    public void addRemoteEntry(int colour, int isPublic) {
        mRepository.addRemoteEntry(colour, isPublic);
    }

    /**
     * Method for generating a random color
     * @return
     */
    public String randomColour() {
        Random random = new Random();
        // pick a random colour (using an index)
        String colourName = mColourArray[random.nextInt(20)];
        return colourName;
    }

}
