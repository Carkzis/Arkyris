package com.carkzis.arkyris.accounts;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.carkzis.arkyris.APIUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for dealing with the communication between the app and
 * the Django Rest Framework, dealing purely with logging the user in.
 */
public class LoginRepository {

    private static final String LOG_TAG = LoginRepository.class.getSimpleName();

    private final AccountService mAccountService = APIUtils.getAccountService();
    private final MutableLiveData<Boolean> mConnectionError;
    private final MutableLiveData<Integer> mLoginResponseCode;
    private final SharedPreferences mPreferences;

    LoginRepository(Application application) {
        mConnectionError = new MutableLiveData<>();
        mLoginResponseCode = new MutableLiveData<>();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * Wrapper methods for returning LiveData.
     */
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }
    public MutableLiveData<Integer> getLoginResponseCode() {
        return mLoginResponseCode;
    }

    /**
     * Returns the authentication token held in the Shared Preferences.
     */
    public String getToken() {
        return mPreferences.getString("token", null);
    }

    /**
     * Method for attempting to authenticate the user with the provided username
     * and password, then providing the relevant response to the LiveData/Shared Preferences.
     */
    public void authenticateUser(String username, String password) {
        Call<ResponseBody> call = mAccountService.authenticateUser(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * Method called when a response is received.
             */
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call,
                                   @NotNull Response<ResponseBody> response) {

                JSONObject jsonObject;
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Login success!");
                    mLoginResponseCode.postValue(response.code());  // this will be 200
                    SharedPreferences.Editor editor = mPreferences.edit();
                    try {
                        assert response.body() != null;
                        jsonObject = new JSONObject(response.body().string());
                        String token = jsonObject.getString("token");
                        Log.e(LOG_TAG, token);
                        editor.putString("token", token);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "IO Exception...");
                    }

                    editor.putString("username", username);
                    editor.apply();
                }

                if (!response.isSuccessful()) {
                    // Failed login...
                    Log.e(LOG_TAG, "Login failed.");
                    mLoginResponseCode.postValue(response.code());
                }
            }

            /**
             * Method called when there is no response from the server.
             */
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }
        });
    }

}
