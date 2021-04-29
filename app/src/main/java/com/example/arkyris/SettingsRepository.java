package com.example.arkyris;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.MutableLiveData;

public class SettingsRepository {

    private static final String LOG_TAG = SettingsRepository.class.getSimpleName();
    AccountService accountService = APIUtils.getAccountService();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private MutableLiveData<String> mAccountName;

    SettingsRepository(Application application) {
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        mAccountName = new MutableLiveData<String>();
    }

    // getter method for getting checking any correction error
    // hides implementation from the UI
    public MutableLiveData<String> getAccountName() {
        String username = preferences.getString("username", null);
        mAccountName.postValue(username);
        return mAccountName;
    }




}
