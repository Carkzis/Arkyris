package com.example.arkyris;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

    private static final String LOG_TAG = RegisterRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    private MutableLiveData<Boolean> mConnectionError;

    public String mRegisterResponse;

    // constructor
    RegisterRepository(Application application) {
        mConnectionError = new MutableLiveData<Boolean>();
    }

    // wrapper method to get connection error
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public void insertUser(RegisterItem newUser) {
        Call<RegisterItem> call = accountService.registerUser(newUser);
        call.enqueue(new Callback<RegisterItem>() {
            @Override
            public void onResponse(Call<RegisterItem> call, Response<RegisterItem> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entered!");
                    mRegisterResponse = "Entered";
                }

                JSONObject jsonObject;
                if (!response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        // This error is for where the username already exists.
                        String errorMessage = jsonObject.getString("username");
                        errorMessage = errorMessage.substring(1, errorMessage.length() - 1);
                        Log.e(LOG_TAG, errorMessage);
                        mRegisterResponse = errorMessage;
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "IO Exception...");
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }

        });
    }
}