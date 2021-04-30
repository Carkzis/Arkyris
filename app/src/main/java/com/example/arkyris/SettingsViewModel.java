package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = SettingsViewModel.class.getSimpleName();
    private LogoutRepository mRepository;
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
            mAccountName = new MutableLiveData<String>();
        }
        return mAccountName;
    }

    public MutableLiveData<String> getLogoutSuccess() {
        if (mLogoutSuccess == null) {
            mLogoutSuccess = new MutableLiveData<String>();
        }
        return mLogoutSuccess;
    }

    public MutableLiveData<Boolean> getAutoLoggedOut() {
        if (mAutoLoggedOut == null) {
            mAutoLoggedOut = new MutableLiveData<Boolean>();
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
