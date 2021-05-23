package com.example.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

public class ArkeViewModel extends AndroidViewModel {

    private ArkeEntryRepository mRepository;
    private LiveData<List<ArkeEntryItem>> mPublicEntries;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<String> mLoadingOutcome;
    private MutableLiveData<Boolean> mEntryAdded;

    // These set colours are for the random colour selected when the colour picker is
    // opened up
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public ArkeViewModel (Application application) {
        super(application);
        mRepository = new ArkeEntryRepository(application);
        mPublicEntries = mRepository.getAllPublicEntries();
        mAccountName = mRepository.getAccountName();
        mConnectionError = mRepository.getConnectionError();
        mLoadingOutcome = mRepository.getLoadingOutcome();
        mEntryAdded = mRepository.getEntryAdded();
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

    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<Boolean>();
        }
        return mConnectionError;
    }

    public MutableLiveData<String> getLoadingOutcome() {
        if (mLoadingOutcome == null) {
            mLoadingOutcome = new MutableLiveData<String>();
        }
        return mLoadingOutcome;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        if (mEntryAdded == null) {
            mEntryAdded = new MutableLiveData<Boolean>();
        }
        return mEntryAdded;
    }

    public void entryAddedComplete() {
        mEntryAdded.postValue(false);
    }

    public void connectionErrorNotified() {
        mConnectionError.postValue(false);
    }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(ArkeEntryItem entry) { mRepository.insert(entry); }

    public void addRemoteEntry(int colour) {
        mRepository.addRemoteEntry(colour);
    }

    // refresh cache
    public void refreshArkeCache() { mRepository.refreshArkeCache(); }

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
