package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private static final String LOG_TAG = RegisterRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Integer> mLoginResponseCode;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    // wrapper method to get connection error
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<Integer> getLoginResponseCode() {
        return mLoginResponseCode;
    }

    // constructor
    LoginRepository(Application application) {
        mConnectionError = new MutableLiveData<Boolean>();
        mLoginResponseCode = new MutableLiveData<Integer>();
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void authenticateUser(String username, String password) {
        Call<ResponseBody> call = accountService.authenticateUser(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                JSONObject jsonObject;
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Login success!");
                    mLoginResponseCode.postValue(response.code());  // this will be 200
                    editor = preferences.edit();
                    try {
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
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }
        });
    }

}
