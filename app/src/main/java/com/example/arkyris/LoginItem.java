package com.example.arkyris;

import com.google.gson.annotations.SerializedName;

public class LoginItem {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    public LoginItem(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public void setUsername(String username) { this.mUsername = username; }
    public void setPassword(String password) { this.mPassword = password; }

    public String getUsername() { return mUsername; }
}
