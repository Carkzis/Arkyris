package com.example.arkyris;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SandboxActivity extends AppCompatActivity {

    private static final String LOG_TAG = SandboxActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandbox);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
