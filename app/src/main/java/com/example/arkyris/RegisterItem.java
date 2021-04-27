package com.example.arkyris;

import com.google.gson.annotations.SerializedName;

public class RegisterItem {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("password")
    private String mPassword;

    public RegisterItem(String username, String email, String password) {
        mUsername = username;
        mEmail = email;
        mPassword = password;
    }

    public void setUsername(String username) { this.mUsername = username; }
    public void setEmail(String email) { this.mEmail = email; }
    public void setPassword(String password) { this.mPassword = password; }

}
