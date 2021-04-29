package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends AndroidViewModel {

    private static final String LOG_TAG = SettingsViewModel.class.getSimpleName();
    private SettingsRepository mRepository;
    private MutableLiveData<String> mAccountName;

    public SettingsViewModel (Application application) {
        super(application);
        mRepository = new SettingsRepository(application);
        mAccountName = mRepository.getAccountName();
    }

    public MutableLiveData<String> getAccountName() {
        if (mAccountName == null) {
            mAccountName = new MutableLiveData<String>();
        }
        return mAccountName;
    }

}
