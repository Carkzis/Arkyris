package com.carkzis.arkyris.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * ViewModel for the SettingsViewModel
 */
public class SettingsViewModel extends AndroidViewModel {

    private final LogoutRepository mRepository;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<String> mLogoutSuccess;
    private MutableLiveData<Boolean> mAutoLoggedOut;

    public SettingsViewModel (Application application) {
        super(application);
        mRepository = new LogoutRepository(application);
        mAccountName = mRepository.getAccountName();
        mLogoutSuccess = mRepository.getLogoutSuccess();
        mAutoLoggedOut = mRepository.getAutoLoggedOut();
    }

    /**
     * Getter method for the account name from the repository, to be observed by the
     * SettingsActivity.
     */
    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<>();
        }
        return mAccountName;
    }

    /**
     * Getter method for checking the logout success message from the repository,
     * and hides the implementation from the UI.
     */
    public MutableLiveData<String> getLogoutSuccess() {
        if (mLogoutSuccess == null) {
            mLogoutSuccess = new MutableLiveData<>();
        }
        return mLogoutSuccess;
    }

    /**
     * This method resets the value held in the LiveData after any events (such as
     * generating a toast in response to an error) so that they do not happen more than once.
     */
    public void logoutSuccessHandled() {
        mLogoutSuccess.postValue("handled");
    }

    /**
     * This is for logging the user out from a device if they have requested to
     * be logged out from all devices on another device.
     */
    public MutableLiveData<Boolean> getAutoLoggedOut() {
        if (mAutoLoggedOut == null) {
            mAutoLoggedOut = new MutableLiveData<>();
        }
        return mAutoLoggedOut;
    }

    /**
     * Wrapper for logging out the member from the current device, calling
     * LogoutRepository's logout() method.
     */
    public void logout() {
        mRepository.logout();
    }

    /**
     * Wrapper for logging out the member from all devices, calling
     * LogoutRepository's logout() method.
     */
    public void logoutAll() {
        mRepository.logoutAll();
    }

    /**
     * Wrapper for checking if the member is logged in with any device, calling
     * LogoutRepository's loggedIn() method.
     */
    public void loggedIn() {
        mRepository.loggedIn();
    }

}
