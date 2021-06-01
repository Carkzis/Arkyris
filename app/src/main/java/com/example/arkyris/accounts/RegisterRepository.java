package com.example.arkyris.accounts;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.arkyris.APIUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

    private static final String LOG_TAG = RegisterRepository.class.getSimpleName();
    private final AccountService mAccountService = APIUtils.getAccountService();
    private final MutableLiveData<Boolean> mConnectionError;
    private final MutableLiveData<Integer> mRegisterResponseCode;

    // constructor
    RegisterRepository() {
        mConnectionError = new MutableLiveData<>();
        mRegisterResponseCode = new MutableLiveData<>();
    }

    // wrapper method to get connection error
    public MutableLiveData<Boolean> getConnectionError() {
        return mConnectionError;
    }

    public MutableLiveData<Integer> getRegisterResponseCode() {
        return mRegisterResponseCode;
    }

    public void insertUser(RegisterItem newUser) {
        Call<RegisterItem> call = mAccountService.registerUser(newUser);
        call.enqueue(new Callback<RegisterItem>() {
            @Override
            public void onResponse(@NotNull Call<RegisterItem> call,
                                   @NotNull Response<RegisterItem> response) {
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
            public void onFailure(@NotNull Call<RegisterItem> call,
                                  @NotNull Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
                mConnectionError.postValue(true);
            }

        });
    }
}