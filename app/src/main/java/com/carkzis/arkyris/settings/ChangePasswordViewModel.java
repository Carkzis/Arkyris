package com.carkzis.arkyris.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ViewModel for the ChangePasswordActivity.
 */
public class ChangePasswordViewModel extends AndroidViewModel {

    private final ChangePasswordRepository mRepository;
    private final MutableLiveData<String> mLocalPasswordTest;
    private MutableLiveData<String> mChangePasswordSuccess;

    public ChangePasswordViewModel (Application application) {
        super(application);
        mLocalPasswordTest = new MutableLiveData<>();
        mRepository = new ChangePasswordRepository(application);
        mChangePasswordSuccess = mRepository.getChangePasswordSuccess();
    }

    /**
     * Getter method for getting checking any password change failure error from the repository,
     * and hides the implementation from the UI.
     */
    public MutableLiveData<String> getPasswordEntryFailure() {
        return mLocalPasswordTest;
    }

    /**
     * Getter method for checking the password change success message from the repository,
     * and hides the implementation from the UI.
     */
    public MutableLiveData<String> getChangePasswordSuccess() {
        if (mChangePasswordSuccess == null) {
            mChangePasswordSuccess = new MutableLiveData<>();
        }
        return mChangePasswordSuccess;
    }

    /**
     * These methods reset the values held in the LiveData after any events (such as
     * generating a toast in response to an error) so that they do not happen more than once.
     */
    public void passwordTestHandled() {
        mLocalPasswordTest.postValue("handled");
    }
    public void changePasswordHandled() {
        mChangePasswordSuccess.postValue("handled");
    }

    /**
     * This method validates the old and new password inputs using a regex, and checks that
     * both new password inputs match, before changing the password remotely via
     * the repository if all is well.
     */
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

        // Pass in old password and one of the new password fields, as to get here,
        // newPassword1 will need to equal newPassword2.
        mRepository.changePassword(oldPassword, newPassword1);

    }

}
