package com.example.arkyris.accounts;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // wrapper method to get connection error
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<Integer> getLoginResponseCode() {
        return mLoginResponseCode;
    }

    public String getToken() {
        return mPreferences.getString("token", null);
    }

    public void authenticateUser(String username, String password) {
        Call<ResponseBody> call = mAccountService.authenticateUser(username, password);
        call.enqueue(new Callback<ResponseBody>() {
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

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }
        });
    }

}
