package com.example.arkyris;

import android.app.Application;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

    private static final String LOG_TAG = RegisterRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();


    // constructor
    RegisterRepository(Application application) {
    }

    public void insertUser(RegisterItem newUser) {
        Call<RegisterItem> call = accountService.registerUser(newUser);
        call.enqueue(new Callback<RegisterItem>() {
            @Override
            public void onResponse(Call<RegisterItem> call, Response<RegisterItem> response) {
                if (response.isSuccessful()) {
                    Log.e(LOG_TAG, "Entered?");
                }
            }

            @Override
            public void onFailure(Call<RegisterItem> call, Throwable throwable) {
                Log.e(LOG_TAG, throwable.getMessage());
            }

        });
    }
}