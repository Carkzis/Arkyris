package com.example.arkyris;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    private LoginRepository mRepository;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Integer> mLoginResponseCode;

    public LoginViewModel (Application application) {
        super(application);
        mRepository = new LoginRepository(application);
        mConnectionError = mRepository.getConnectionError();
        mLoginResponseCode = mRepository.getLoginResponseCode();
    }

    // getter method for getting checking any correction error
    // hides implementation from the UI
    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<Boolean>();
        }
        return mConnectionError;
    }

    public MutableLiveData<Integer> getLoginResponseCode() {
        if (mLoginResponseCode == null) {
            mLoginResponseCode = new MutableLiveData<Integer>();
        }
        return mLoginResponseCode;
    }

    public String getToken() {
        Log.e(LOG_TAG, mRepository.getToken());
        return mRepository.getToken();
    }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void authenticateUser(String username, String password) {
        mRepository.authenticateUser(username, password);
    }

}