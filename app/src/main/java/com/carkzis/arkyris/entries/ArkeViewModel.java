package com.carkzis.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

/**
 * ViewModel for the ArkeFragment.
 */
public class ArkeViewModel extends AndroidViewModel {

    private final ArkeEntryRepository mRepository;
    private final LiveData<List<ArkeEntryItem>> mPublicEntries;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<String> mLoadingOutcome;
    private MutableLiveData<Boolean> mEntryAdded;

    /**
     * These set colours are for the random colour selected when the colour picker is opened up.
     */
    private static final String[] mColourArray = {"red", "pink", "purple", "deep_purple",
            "indigo", "blue", "light_blue", "cyan", "teal", "green",
            "light_green", "lime", "yellow", "amber", "orange", "deep_orange",
            "brown", "grey", "blue_grey", "pink_dark"};

    public ArkeViewModel (Application application) {
        super(application);
        mRepository = new ArkeEntryRepository(application);
        mPublicEntries = mRepository.getAllPublicEntries();
        mAccountName = mRepository.getAccountName();
        mLoadingOutcome = mRepository.getLoadingOutcome();
        mEntryAdded = mRepository.getEntryAdded();
    }

    /**
     * Getter method for retrieving the public entries, hides
     * implementation from the UI.
     */
    public LiveData<List<ArkeEntryItem>> getPublicEntries() { return mPublicEntries; }

    /**
     * Getter method for the account name from the repository, to be observed by the
     * ArkeFragment.
     */
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<>();
        }
        return mAccountName;
    }

    /**
     * Getter method for getting checking the loading outcome, hides
     * implementation from the UI.
     */
    public MutableLiveData<String> getLoadingOutcome() {
        if (mLoadingOutcome == null) {
            mLoadingOutcome = new MutableLiveData<>();
        }
        return mLoadingOutcome;
    }

    /**
     * Getter method for checking if an entry has been added.
     */
    public MutableLiveData<Boolean> getEntryAdded() {
        if (mEntryAdded == null) {
            mEntryAdded = new MutableLiveData<>();
        }
        return mEntryAdded;
    }

    /**
     * These methods reset the values held in the LiveData after any events (such as
     * generating a toast in response to an error) so that they do not happen more than once.
     */
    public void entryAddedComplete() {
        mEntryAdded.postValue(false);
    }
    public void loadingOutcomeComplete() { mLoadingOutcome.postValue("standby"); }

    /**
     * Wrapper for calling the ArkeEntryRepository's
     * insert() method, which inserts the entry into the local database.
     */
    public void insert(ArkeEntryItem entry) { mRepository.insert(entry); }

    /**
     * Wrapper for calling the ArkeEntryRepository's
     * addRemoteEntry(), which adds an entry into the remote database.
     */
    public void addRemoteEntry(int colour) {
        mRepository.addRemoteEntry(colour);
    }

    /**
     * Wrapper for refreshing the arke_entry_table.
     */
    public void refreshArkeCache() { mRepository.refreshArkeCache(); }

    /**
     * Method for generating a random color.
     */
    public String randomColour() {
        Random random = new Random();
        // Pick a random colour (using an index).
        return mColourArray[random.nextInt(20)];
    }

}
