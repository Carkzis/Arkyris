package com.carkzis.arkyris.settings;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.carkzis.arkyris.APIUtils;
import com.carkzis.arkyris.accounts.AccountService;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for dealing with the communication between the app and
 * the Django Rest Framework, dealing purely with changing a user's password.
 */
public class ChangePasswordRepository {

    private static final String LOG_TAG = LogoutRepository.class.getSimpleName();
    private final AccountService mAccountService = APIUtils.getAccountService();
    private final MutableLiveData<String> mChangePasswordSuccess;
    private final String mToken;

    ChangePasswordRepository(Application application) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mToken = mPreferences.getString("mToken", null);
        mChangePasswordSuccess = new MutableLiveData<>();
    }

    /**
     * Returns the password success status (could be replacing with enum rather than String).
     */
    public MutableLiveData<String> getChangePasswordSuccess() {
        return mChangePasswordSuccess;
    }

    /**
     * Change the password in the remote database, and return the response.
     */
    public void changePassword(String oldPassword, String newPassword) {
        Call<ResponseBody> call = mAccountService.changePassword(
                "Token " + mToken, oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call,
                                   @NotNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Password changed.");
                    mChangePasswordSuccess.postValue("success");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "That isn't your old password...");
                    mChangePasswordSuccess.postValue("failed");
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call,
                                  @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Log.e(LOG_TAG, "No connection.");
                mChangePasswordSuccess.postValue("no_connection");
            }
        });
    }

}
