package com.carkzis.arkyris.accounts;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AccountService {

    @POST("register/")
    Call<RegisterItem> registerUser(@Body RegisterItem newUser);

    @FormUrlEncoded
    @POST("login/")
    Call<ResponseBody> authenticateUser(@Field("username") String username,
                                        @Field("password") String password);

    @POST("logout/")
    Call<ResponseBody> logout(@Header("Authorization") String token);

    @POST("logoutall/")
    Call<ResponseBody> logoutAll(@Header("Authorization") String token);

    @GET("user/")
    Call<ResponseBody> loggedIn(@Header("Authorization") String token);

    @FormUrlEncoded
    @PUT("change_password/")
    Call<ResponseBody> changePassword(@Header("Authorization") String token,
                                      @Field("old_password") String oldPassword,
                                      @Field("new_password") String newPassword);
}
