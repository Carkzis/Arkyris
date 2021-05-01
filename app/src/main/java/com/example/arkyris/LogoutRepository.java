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

public class LogoutRepository {

    private static final String LOG_TAG = LogoutRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private MutableLiveData<String> mAccountName;
    private MutableLiveData<String> mLogoutSuccess;
    private MutableLiveData<Boolean> mAutoLoggedOut;
    String token;

    LogoutRepository(Application application) {
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<String>();
        mLogoutSuccess = new MutableLiveData<String>();
        mAutoLoggedOut = new MutableLiveData<Boolean>();
        token = preferences.getString("token", null);
    }

    // hides implementation from the UI
    public MutableLiveData<String> getAccountName() {
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    public MutableLiveData<String> getLogoutSuccess() {
        return mLogoutSuccess;
    }

    public MutableLiveData<Boolean> getAutoLoggedOut() {
        return mAutoLoggedOut;
    }

    public void logout() {
        Call<ResponseBody> call = accountService.logout("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out normally.");
                    preferences.edit().clear().apply();
                    mLogoutSuccess.postValue("success_one");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out locally only as token mismatch or absent.");
                    preferences.edit().clear().apply();
                    // considered a success as if there is no token to remove, no harm
                    mLogoutSuccess.postValue("success_one");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                // Note: this will log out regardless of connection, as it just deletes the token
                // however, more security could be added to remove the token once a connection
                // is available again
                Log.e(LOG_TAG, "Logged out locally as no connection.");
                preferences.edit().clear().apply();
                mLogoutSuccess.postValue("no_connection_one");
            }
        });
    }

    public void logoutAll() {
        Call<ResponseBody> call = accountService.logoutAll("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out normally.");
                    preferences.edit().clear().apply();
                    mLogoutSuccess.postValue("success_all");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Not logged out anywhere as token mismatch or absent.");
                    mLogoutSuccess.postValue("fail_all");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Log.e(LOG_TAG, "Not logged out anywhere as no connection.");
                mLogoutSuccess.postValue("no_connection_all");
            }
        });
    }

    /**
     * This will log the user out from the current device if they have been logged out remotely
     * e.g. from logoutAll()
     */
    public void loggedIn() {
        Call<ResponseBody> call = accountService.loggedIn("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Member is logged in.");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Member is logged out, so delete  token.");
                    preferences.edit().clear().apply();
                    mAutoLoggedOut.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Log.e(LOG_TAG, "No connection, so cannot check login status.");
                // Do nothing here, won't log out users if they are not logged in.
            }
        });
    }


}
