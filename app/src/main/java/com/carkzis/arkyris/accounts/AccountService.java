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

/**
 * Service interface for performing calls to the Django Rest Framework relating
 * to the user's account.
 */
public interface AccountService {

    /**
     * Posts a RegisterItem object to the Django Rest Framework.
     */
    @POST("register/")
    Call<RegisterItem> registerUser(@Body RegisterItem newUser);

    /**
     * Attempts to log in a user using the username and password provided.
     */
    @FormUrlEncoded
    @POST("login/")
    Call<ResponseBody> authenticateUser(@Field("username") String username,
                                        @Field("password") String password);

    /**
     * Logs out a user supplying the stored token.
     */
    @POST("logout/")
    Call<ResponseBody> logout(@Header("Authorization") String token);

    /**
     * Logs out the user from all devices.
     */
    @POST("logoutall/")
    Call<ResponseBody> logoutAll(@Header("Authorization") String token);

    /**
     * Returns user information using the stored token for authorisation.
     */
    @GET("user/")
    Call<ResponseBody> loggedIn(@Header("Authorization") String token);

    /**
     * Requests a change of password, using the provided current password and new password,
     * as well as the stored token for authorisation.
     */
    @FormUrlEncoded
    @PUT("change_password/")
    Call<ResponseBody> changePassword(@Header("Authorization") String token,
                                      @Field("old_password") String oldPassword,
                                      @Field("new_password") String newPassword);
}
