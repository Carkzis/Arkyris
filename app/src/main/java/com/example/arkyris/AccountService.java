package com.example.arkyris;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AccountService {

    @POST("register/")
    Call<RegisterItem> registerUser(@Body RegisterItem newUser);

    @FormUrlEncoded
    @POST("login/")
    Call<ResponseBody> authenticateUser(@Field("username") String username,
                                        @Field("password") String password);

}
