package com.example.arkyris.settings;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;
import com.example.arkyris.accounts.AccountService;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public MutableLiveData<String> getChangePasswordSuccess() {
        return mChangePasswordSuccess;
    }

    /**
     * Change the password in the remote database, and return response.
     */
    public void changePassword(String oldPassword, String newPassword) {
        Call<ResponseBody> call = mAccountService.changePassword(
                "Token " + mToken, oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
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
