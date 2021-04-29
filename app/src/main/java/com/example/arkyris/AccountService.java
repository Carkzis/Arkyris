package com.example.arkyris;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountService {

    @POST("register/")
    Call<RegisterItem> registerUser(@Body RegisterItem newUser);

    @POST("login/")
    Call<LoginItem> authenticateUser(@Body LoginItem login);

}
