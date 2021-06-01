package com.example.arkyris.accounts;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterViewModel extends AndroidViewModel {

    private final RegisterRepository mRepository;
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Integer> mRegisterResponseCode;

    public RegisterViewModel (Application application) {
        super(application);
        mRepository = new RegisterRepository();
        mConnectionError = mRepository.getConnectionError();
        mRegisterResponseCode = mRepository.getRegisterResponseCode();
    }

    // getter method for getting checking any correction error
    // hides implementation from the UI
    public MutableLiveData<Boolean> getConnectionError() {
        if (mConnectionError == null) {
            mConnectionError = new MutableLiveData<>();
        }
        return mConnectionError;
    }

    public MutableLiveData<Integer> getRegisterResponseCode() {
        if (mRegisterResponseCode == null) {
            mRegisterResponseCode = new MutableLiveData<>();
        }
        return mRegisterResponseCode;
    }

    public void connectionErrorHandled() {
        mConnectionError.postValue(false);
    }

    public void registerResponseHandled() {
        mRegisterResponseCode.postValue(-1);
    }

    public boolean testUsername (String username) {
        String usernameRegex = "[a-zA-Z1-9]{1,50}";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    public boolean testEmail (String email) {

        // Thank https://howtodoinjava.com/ for this ghastly thing...
        String emailRegex =
                "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\." +
                        "[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public boolean testPasswordMatch (String password1, String password2) {
        return (password1.equals(password2));
    }

    public boolean testPasswordFormat (String password1) {

        String passwordRegex = "[a-zA-Z1-9]{8,20}";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password1);

        return matcher.matches();
    }

    public String failureMessage(boolean userFormat, boolean emailFormat,
                                 boolean passwordMatch, boolean passwordFormat) {
        if (!userFormat) {
            return "User must be less than 50 characters, alphanumberical...";
        } else if (!emailFormat) {
            return "That is not an email...";
        } else if (!passwordMatch) {
            return "The password's don't match...";
        } else if (!passwordFormat) {
            return "The password doesn't meet the requirements...";
        }
        return null;
    }

    // wrapper for insert that calls Repository's insert() method,
    // hides implementation of insert() from UI
    public void insertUser(String username, String email, String password) {
        RegisterItem newUser = new RegisterItem(username, email, password);
        mRepository.insertUser(newUser);
    }

}