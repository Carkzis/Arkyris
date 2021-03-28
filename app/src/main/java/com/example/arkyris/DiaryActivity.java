package com.example.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class DiaryActivity extends AppCompatActivity {

    private static final String LOG_TAG = DiaryActivity.class.getSimpleName();
    public static final String EXTRA_PUBLICISE = "com.example.android.Arkyris.extra.PUBLICISE";
    private EditText mReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initiate attributes
        mReply = findViewById(R.id.editText_publicise);

        // This will only do something if the Send button was received
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.text_message);
        textView.setText(message);
        if (message != null) {
            TextView headerTextView = findViewById(R.id.text_header);
            headerTextView.setText(R.string.message_received);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        /**
         * This will send the latest colour to a friend. Just a placeholder message currently.
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Choose your recipients!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                // define a MIME type, text currently but will be "image/jpeg"
                String mimeType = "text/plain";

                ShareCompat.IntentBuilder
                        .from(DiaryActivity.this)
                        .setType(mimeType)
                        .setChooserTitle("Share your colour: ") // appears on system app chooser
                        .setText("Feeling pink!") // placeholder, will be image
                        .startChooser(); // show the chooser and send intent
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_diary, menu);
        return true;
    }

    /**
     * This will mostly be replaced, but the concept of
     * public diary entries is on a list of things to do.
     *
     * @param view is the "publicise" button
     */
    public void returnReply(View view) {
        Log.d(LOG_TAG, "Publicise button clicked!");
        String publicise = mReply.getText().toString();
        Log.d(LOG_TAG, publicise);
        Intent publiciseIntent = new Intent(); // don't reuse intent used to get to the Diary
        publiciseIntent.putExtra(EXTRA_PUBLICISE, publicise);
        // set result for returning to main activity
        setResult(RESULT_OK, publiciseIntent);
        Log.d(LOG_TAG, "End DiaryActivity");
        finish(); // closes the activity and returns to the main activity
    }

    /**
     * This will add a colour entry into the diary.
     * It currently just shows a toast as a placeholder.
     *
     * @param view is the ImageView that enters a particular colour to the diary.
     */
    public void addEntry(View view) {
        displayToast(getString(R.string.diary_entry_added));
        // TODO: Add a way to enter the entry into the SQL database, and update the entry history
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}