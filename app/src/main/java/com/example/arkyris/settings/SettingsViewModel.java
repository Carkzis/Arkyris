package com.example.arkyris.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<>();
        }
        return mAccountName;
    }

    public MutableLiveData<String> getLogoutSuccess() {
        if (mLogoutSuccess == null) {
            mLogoutSuccess = new MutableLiveData<>();
        }
        return mLogoutSuccess;
    }

    public void logoutSuccessHandled() {
        mLogoutSuccess.postValue("handled");
    }

    public MutableLiveData<Boolean> getAutoLoggedOut() {
        if (mAutoLoggedOut == null) {
            mAutoLoggedOut = new MutableLiveData<>();
        }
        return mAutoLoggedOut;
    }

    public void logout() {
        mRepository.logout();
    }

    public void logoutAll() {
        mRepository.logoutAll();
    }

    public void loggedIn() {
        mRepository.loggedIn();
    }

}
