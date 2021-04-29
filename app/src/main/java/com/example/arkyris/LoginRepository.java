package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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
        editor = preferences.edit();
    }

    public void authenticateUser(LoginItem login) {
        Call<LoginItem> call = accountService.authenticateUser(login);
        call.enqueue(new Callback<LoginItem>() {
            @Override
            public void onResponse(Call<LoginItem> call, Response<LoginItem> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Login success!");
                    mLoginResponseCode.postValue(response.code());  // this will be 200

                    // TODO: need to get JSON for sharepreferences
                }

                if (!response.isSuccessful()) {
                    // Failed login...
                    Log.e(LOG_TAG, "Login failed.");
                    mLoginResponseCode.postValue(response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }
        });
    }

}
