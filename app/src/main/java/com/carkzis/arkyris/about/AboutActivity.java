package com.carkzis.arkyris.about;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arkyris.R;

/**
 * Activity for the about information, which supplies information about the app and how to
 * use it.
 */
public class AboutActivity extends AppCompatActivity {

    private static final String LOG_TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the buttons on the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

}

