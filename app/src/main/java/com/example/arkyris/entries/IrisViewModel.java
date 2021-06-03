package com.example.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

public class IrisViewModel extends AndroidViewModel {

    private final IrisEntryRepository mRepository;
    private final LiveData<List<IrisEntryItem>> mAllEntries;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Boolean> mLoadingComplete;
    private MutableLiveData<Boolean> mEntryAdded;
    private MutableLiveData<Boolean> mEntryDeleted;
    private MutableLiveData<String> mPublicOrPrivate;

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
        mEntryDeleted = mRepository.getEntryDeleted();
        mPublicOrPrivate = mRepository.getIsPublic();
    }

    // getter method to retrieve account name from repository shared preferences
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<>();
        }
        return mAccountName;
    }

    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<>();
        }
        return mConnectionError;
    }

    public MutableLiveData<Boolean> getLoadingComplete() {
        if (mLoadingComplete == null) {
            mLoadingComplete = new MutableLiveData<>();
        }
        return mLoadingComplete;
    }

    public MutableLiveData<Boolean> getEntryAdded() {
        if (mEntryAdded == null) {
            mEntryAdded = new MutableLiveData<>();
        }
        return mEntryAdded;
    }

    public MutableLiveData<Boolean> getEntryDeleted() {
        if (mEntryDeleted == null) {
            mEntryDeleted = new MutableLiveData<>();
        }
        return mEntryDeleted;
    }

    public MutableLiveData<String> getIsPublic() {
        if (mPublicOrPrivate == null) {
            mPublicOrPrivate = new MutableLiveData<>();
        }
        return mPublicOrPrivate;
    }

    public void entryAddedComplete() {
        mEntryAdded.postValue(false);
    }

    public void entryDeletedComplete() {
        mEntryDeleted.postValue(false);
    }

    public void connectionErrorNotified() {
        mConnectionError.postValue(false);
    }

    public void changedEntryPublicity() {
        mPublicOrPrivate.postValue("complete");
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<IrisEntryItem>> getAllEntries() { return mAllEntries; }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insert(IrisEntryItem entry) {mRepository.insert(entry); }

    // This is only for testing purposes
    public void deleteAll() { mRepository.deleteAll(); }

    // refresh cache
    public void refreshIrisCache(boolean fromArke) {
        mRepository.refreshIrisCache(fromArke);
    }

    public void addRemoteEntry(int colour, int isPublic) {
        mRepository.addRemoteEntry(colour, isPublic);
    }

    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        mRepository.deleteRemoteEntry(entryItem);
    }

    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        mRepository.updateRemoteEntryPublicity(entryItem, isPublic);
    }

    /**
     * Method for generating a random color
     */
    public String randomColour() {
        Random random = new Random();
        // pick a random colour (using an index)
        return mColourArray[random.nextInt(20)];
    }

}
