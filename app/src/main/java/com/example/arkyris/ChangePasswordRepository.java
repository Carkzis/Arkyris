package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordRepository {

    private static final String LOG_TAG = LogoutRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    SharedPreferences preferences;
    private MutableLiveData<String> mChangePasswordSuccess;
    private String token;


    ChangePasswordRepository(Application application) {
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        token = preferences.getString("token", null);
        mChangePasswordSuccess = new MutableLiveData<String>();
    }

    public MutableLiveData<String> getChangePasswordSuccess() {
        return mChangePasswordSuccess;
    }

    /**
     * Change the password in the remote database, and return response.
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(String oldPassword, String newPassword) {
        Call<ResponseBody> call = accountService.changePassword(
                "Token " + token, oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Log.e(LOG_TAG, "No connection.");
                mChangePasswordSuccess.postValue("no_connection");
            }
        });
    }

}
