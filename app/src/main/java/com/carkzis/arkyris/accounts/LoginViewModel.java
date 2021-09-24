package com.carkzis.arkyris.accounts;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * ViewModel for the LoginActivity.
 */
public class LoginViewModel extends AndroidViewModel {

    private static final String LOG_TAG = LoginViewModel.class.getSimpleName();

    private final LoginRepository mRepository;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Integer> mLoginResponseCode;

    public LoginViewModel (Application application) {
        super(application);
        mRepository = new LoginRepository(application);
        mConnectionError = mRepository.getConnectionError();
        mLoginResponseCode = mRepository.getLoginResponseCode();
    }

    /**
     * Getter method for getting checking any connection error, hides
     * implementation from the UI.
     */
    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<Boolean>();
        }
        return mConnectionError;
    }

    /**
     * Getter method for getting checking the login response code, hides
     * implementation from the UI.
     */
    public MutableLiveData<Integer> getLoginResponseCode() {
        if (mLoginResponseCode == null) {
            mLoginResponseCode = new MutableLiveData<Integer>();
        }
        return mLoginResponseCode;
    }

    /**
     * These methods reset the values held in the LiveData after any events (such as
     * generating a toast in response to an error) so that they do not happen more than once.
     */
    public void connectionErrorHandled() {
        mConnectionError.postValue(false);
    }
    public void loginResponseHandled() {
        mLoginResponseCode.postValue(-1);
    }

    /**
     * Gives the LoginActivity access to the stored authentication token.
     */
    public String getToken() {
        return mRepository.getToken();
    }

    /**
     * Wrapper for authenticating the user that calls LoginRepository's
     * authenticateUser() method, hides it's implementation from the UI.
     */
    public void authenticateUser(String username, String password) {
        mRepository.authenticateUser(username, password);
    }

}