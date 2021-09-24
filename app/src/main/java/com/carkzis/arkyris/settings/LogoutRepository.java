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
 * the Django Rest Framework, dealing purely with logging a user out.
 */
public class LogoutRepository {

    private static final String LOG_TAG = LogoutRepository.class.getSimpleName();
    private final AccountService mAccountService = APIUtils.getAccountService();
    private final SharedPreferences mPreferences;
    private final MutableLiveData<String> mAccountName;
    private final MutableLiveData<String> mLogoutSuccess;
    private final MutableLiveData<Boolean> mAutoLoggedOut;
    private final String token;

    LogoutRepository(Application application) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<>();
        mLogoutSuccess = new MutableLiveData<>();
        mAutoLoggedOut = new MutableLiveData<>();
        token = mPreferences.getString("token", null);
    }

    /**
     * Wrapper methods for returning the username from the Shared Preferences.
     * Bit of an odd place to put this, but the name is displayed on the settings screen,
     * from where you can log out.
     */
    public MutableLiveData<String> getAccountName() {
        String username = mPreferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }

    /**
     * Wrapper methods for returning LiveData.
     */
    public MutableLiveData<String> getLogoutSuccess() {
        return mLogoutSuccess;
    }
    public MutableLiveData<Boolean> getAutoLoggedOut() {
        return mAutoLoggedOut;
    }

    /**
     * Method for logging the method out of the device.
     * Note: the loggout is performed regardless of if it succeeds with the server.
     */
    public void logout() {
        Call<ResponseBody> call = mAccountService.logout("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call,
                                   @NotNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out normally.");
                    mPreferences.edit().clear().apply();
                    mLogoutSuccess.postValue("success_one");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out locally only as token mismatch or absent.");
                    mPreferences.edit().clear().apply();
                    // considered a success as if there is no token to remove, no harm
                    mLogoutSuccess.postValue("success_one");
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                // Note: this will log out regardless of connection, as it just deletes the token
                // however, more security could be added to remove the token once a connection
                // is available again
                Log.e(LOG_TAG, "Logged out locally as no connection.");
                mPreferences.edit().clear().apply();
                mLogoutSuccess.postValue("no_connection_one");
            }
        });
    }

    /**
     * Method for logging the method out of all the user's devices.
     * Note: This will only allow the user to log out if this succeeds, unlike logging out
     * of a single device.
     */
    public void logoutAll() {
        Call<ResponseBody> call = mAccountService.logoutAll("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call,
                                   @NotNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Logged out normally.");
                    mPreferences.edit().clear().apply();
                    mLogoutSuccess.postValue("success_all");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Not logged out anywhere as token mismatch or absent.");
                    mLogoutSuccess.postValue("fail_all");
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
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
        Call<ResponseBody> call = mAccountService.loggedIn("Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call,
                                   @NotNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Member is logged in.");
                }

                if (!response.isSuccessful()) {
                    Log.e(LOG_TAG, "Member is logged out, so delete  token.");
                    mPreferences.edit().clear().apply();
                    mAutoLoggedOut.postValue(true);
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                Log.e(LOG_TAG, "No connection, so cannot check login status.");
                // Do nothing here.
            }
        });
    }


}
