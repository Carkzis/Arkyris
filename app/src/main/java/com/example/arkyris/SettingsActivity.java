package com.example.arkyris;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class SettingsActivity extends AppCompatActivity {

    private TextView mUsername;
    private SettingsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mUsername = findViewById(R.id.text_settings_name);

        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        mViewModel.getAccountName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String username) {
                // update cached copy of words in adapter
                if (username != null) {
                    mUsername.setText(username);
                }
            }
        });
    }
}