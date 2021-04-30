package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordViewModel extends AndroidViewModel {

    private static final String LOG_TAG = ChangePasswordViewModel.class.getSimpleName();
    private MutableLiveData<String> mLocalPasswordTest;

    public ChangePasswordViewModel (Application application) {
        super(application);
        mLocalPasswordTest = new MutableLiveData<String>();
    }

    // getter method for getting checking any connection error
    // hides implementation from the UI
    public MutableLiveData<String> getPasswordEntryFailure() {
        return mLocalPasswordTest;
    }

    public void changePassword(String password1, String password2) {

        String passwordRegex = "[a-zA-Z1-9]{8,20}";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password1);

        // return if any of the local tests fail
        if (!password1.equals(password2)) {
            mLocalPasswordTest.postValue("not_equal");
            return;
        } else if (!matcher.matches()) {
            mLocalPasswordTest.postValue("incorrect_format");
            return;
        }

    }

}
