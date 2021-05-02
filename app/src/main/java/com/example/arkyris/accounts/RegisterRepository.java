package com.example.arkyris.accounts;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

    private static final String LOG_TAG = RegisterRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    private MutableLiveData<Boolean> mConnectionError;
    private MutableLiveData<Integer> mRegisterResponseCode;

    // constructor
    RegisterRepository(Application application) {
        mConnectionError = new MutableLiveData<Boolean>();
        mRegisterResponseCode = new MutableLiveData<Integer>();
    }

    // wrapper method to get connection error
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<Integer> getRegisterResponseCode() {
        return mRegisterResponseCode;
    }

    public void insertUser(RegisterItem newUser) {
        Call<RegisterItem> call = accountService.registerUser(newUser);
        call.enqueue(new Callback<RegisterItem>() {
            @Override
            public void onResponse(Call<RegisterItem> call, Response<RegisterItem> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entered!");
                    mRegisterResponseCode.postValue(response.code());  // this will be 200
                }

                if (!response.isSuccessful()) {
                    // This error is for where the username already exists.
                    Log.e(LOG_TAG, "Same username used...");
                    mRegisterResponseCode.postValue(response.code()); // assume this is 400
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