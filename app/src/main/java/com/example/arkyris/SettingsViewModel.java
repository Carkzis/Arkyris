package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = SettingsViewModel.class.getSimpleName();
    private LogoutRepository mRepository;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<String> mLogoutSuccess;

    public SettingsViewModel (Application application) {
        super(application);
        mRepository = new LogoutRepository(application);
        mAccountName = mRepository.getAccountName();
        mLogoutSuccess = mRepository.getLogoutSuccess();
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

    public void logout() {
        mRepository.logout();
    }

    public void logoutAll() {
        mRepository.logoutAll();
    }

}
