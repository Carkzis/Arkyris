package com.carkzis.arkyris.entries;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Random;

/**
 * ViewModel for the IrisFragment.
 */
public class IrisViewModel extends AndroidViewModel {

    private final IrisEntryRepository mRepository;
    private final LiveData<List<IrisEntryItem>> mAllEntries;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Boolean> mLoadingComplete;
    private MutableLiveData<Boolean> mEntryAdded;
    private MutableLiveData<Boolean> mEntryDeleted;
    private MutableLiveData<String> mPublicOrPrivate;

    /**
     * These set colours are for the random colour selected when the colour picker is opened up.
     */
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

    /**
     * Getter method for the account name from the repository, to be observed by the
     * IrisFragment.
     */
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<>();
        }
        return mAccountName;
    }

    /**
     * Getter method for getting checking any connection error, hides
     * implementation from the UI.
     */
    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<>();
        }
        return mConnectionError;
    }

    /**
     * Getter method for getting checking whether loading has completed, hides
     * implementation from the UI.
     */
    public MutableLiveData<Boolean> getLoadingComplete() {
        if (mLoadingComplete == null) {
            mLoadingComplete = new MutableLiveData<>();
        }
        return mLoadingComplete;
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
     * Getter method for checking if an entry has been deleted.
     */
    public MutableLiveData<Boolean> getEntryDeleted() {
        if (mEntryDeleted == null) {
            mEntryDeleted = new MutableLiveData<>();
        }
        return mEntryDeleted;
    }

    /**
     * Getter method for checking if an entry has had its publicity setting changed.
     */
    public MutableLiveData<String> getIsPublic() {
        if (mPublicOrPrivate == null) {
            mPublicOrPrivate = new MutableLiveData<>();
        }
        return mPublicOrPrivate;
    }

    /**
     * These methods reset the values held in the LiveData after any events (such as
     * generating a toast in response to an error) so that they do not happen more than once.
     */
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

    /**
     * Getter method for retrieving the public entries, hides
     * implementation from the UI.
     */
    LiveData<List<IrisEntryItem>> getAllEntries() { return mAllEntries; }

    /**
     * Wrapper for calling the IrisEntryRepository's
     * insert() method, which inserts the entry into the local database.
     */
    public void insert(IrisEntryItem entry) {mRepository.insert(entry); }

    /**
     * Deletes all items in repository in iris_entry_table.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Wrapper for refreshing the iris_entry_table.
     */
    public void refreshIrisCache(boolean fromArke) {
        mRepository.refreshIrisCache(fromArke);
    }

    /**
     * Wrapper for calling the IrisEntryRepository's
     * addRemoteEntry(), which adds an entry into the remote database.
     */
    public void addRemoteEntry(int colour, int isPublic) {
        mRepository.addRemoteEntry(colour, isPublic);
    }

    /**
     * Wrapper for calling the IrisEntryRepository's
     * deleteRemoteEntry(), which deletes an entry in the remote database (via a flag).
     */
    public void deleteRemoteEntry(IrisEntryItem entryItem) {
        mRepository.deleteRemoteEntry(entryItem);
    }

    /**
     * Wrapper for calling the IrisEntryRepository's
     * updateRemoteEntryPublicity, which updates the publicity of an entry in the remote database.
     */
    public void updateRemoteEntryPublicity(IrisEntryItem entryItem, int isPublic) {
        mRepository.updateRemoteEntryPublicity(entryItem, isPublic);
    }

    /**
     * Method for generating a random color.
     */
    public String randomColour() {
        Random random = new Random();
        // Pick a random colour (using an index).
        return mColourArray[random.nextInt(20)];
    }

}
