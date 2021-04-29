package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

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

    public MutableLiveData<Integer> getRegisterResponseCode() {
        if (mLoginResponseCode == null) {
            mLoginResponseCode = new MutableLiveData<Integer>();
        }
        return mLoginResponseCode;
    }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void authenticateUser(String username, String password) {
        LoginItem login = new LoginItem(username, password);
        mRepository.authenticateUser(login);
    }

}