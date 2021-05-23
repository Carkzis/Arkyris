package com.example.arkyris.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordViewModel extends AndroidViewModel {

    private static final String LOG_TAG = ChangePasswordViewModel.class.getSimpleName();
    private final ChangePasswordRepository mRepository;
    private final MutableLiveData<String> mLocalPasswordTest;
    private MutableLiveData<String> mChangePasswordSuccess;

    public ChangePasswordViewModel (Application application) {
        super(application);
        mLocalPasswordTest = new MutableLiveData<String>();
        mRepository = new ChangePasswordRepository(application);
        mChangePasswordSuccess = mRepository.getChangePasswordSuccess();
    }

    // getter method for getting checking any connection error
    // hides implementation from the UI
    public MutableLiveData<String> getPasswordEntryFailure() {
        return mLocalPasswordTest;
    }

    public MutableLiveData<String> getChangePasswordSuccess() {
        if (mChangePasswordSuccess == null) {
            mChangePasswordSuccess = new MutableLiveData<String>();
        }
        return mChangePasswordSuccess;
    }

    public void passwordTestHandled() {
        mLocalPasswordTest.postValue("handled");
    }

    public void changePasswordHandled() {
        mChangePasswordSuccess.postValue("handled");
    }


    public void changePassword(String oldPassword, String newPassword1, String newPassword2) {

        String passwordRegex = "[a-zA-Z1-9]{8,20}";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(newPassword1);

        // return if any of the local tests fail
        if (!newPassword1.equals(newPassword2)) {
            mLocalPasswordTest.postValue("not_equal");
            return;
        } else if (!matcher.matches()) {
            mLocalPasswordTest.postValue("incorrect_format");
            return;
        }

        // pass in old password and one of the new password fields, as to get here,
        // newPassword1 will need to equal newPassword2
        mRepository.changePassword(oldPassword, newPassword1);

    }

}
