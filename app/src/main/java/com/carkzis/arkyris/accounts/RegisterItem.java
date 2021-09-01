package com.carkzis.arkyris.accounts;

import com.google.gson.annotations.SerializedName;

public class RegisterItem {

    @SerializedName("username")
    private final String mUsername;

    @SerializedName("email")
    private final String mEmail;

    @SerializedName("password")
    private final String mPassword;

    public RegisterItem(String username, String email, String password) {
        mUsername = username;
        mEmail = email;
        mPassword = password;
    }


}
