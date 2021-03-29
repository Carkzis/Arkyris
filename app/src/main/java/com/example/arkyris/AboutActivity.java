package com.example.arkyris;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

public class AboutActivity extends AppCompatActivity {

    private static final String LOG_TAG = AboutActivity.class.getSimpleName();
    private EditText mWebsiteEditText;
    private EditText mLocationEditText;
    private EditText mShareEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialise variables
        mWebsiteEditText = findViewById(R.id.website_edittext);
        mLocationEditText = findViewById(R.id.location_edittext);
        mShareEditText = findViewById((R.id.share_edittext));

    }

    public void openWebsite(View view) {
        String url = mWebsiteEditText.getText().toString();
        Uri webpage = Uri.parse(url); // encode and parse the url
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage); // action view to view webpage
        if (intent.resolveActivity(getPackageManager()) != null) { // find activity to handle intent
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Can't open website...");
        }
    }

    public void openLocation(View view) {
        // Gets the string for the location
        String loc = mLocationEditText.getText().toString();
        Uri addressUri = Uri.parse("geo:0,0?q=" + loc); // parses a geo search
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
        // finds activity to handle it make sure intent is successful
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Can't do this! Sorry.");
        }
    }

    public void shareText(View view) {
        String shareText = mShareEditText.getText().toString();
        // define a MIME type
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(this) // the activity that launches the intent
                .setType(mimeType) // MIME type to be shared
                .setChooserTitle("Share this text with: ") // appears on system app chooser
                .setText(shareText) // actual text to be shared
                .startChooser(); // show the chooser and send intent
    }

    public void takePicture(View view) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null){
            startActivity(takePicture);
        } else {
            Log.d(LOG_TAG, "No picture for you!");
        }
    }
}

