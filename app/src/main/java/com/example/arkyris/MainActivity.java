package com.example.arkyris;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "com.example.android.Arkyris.extra.MESSAGE";
    public static final int TEXT_REQUEST = 1;
    private EditText mMessageEditText;
    private int mCount = 0;
    private TextView mShowCount;
    private Button mZeroButton;
    private Button mCountButton;
    private TextView mPublicTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "onCreate");

        // Initialise view variables
        mShowCount = (TextView) findViewById(R.id.show_count);
        mZeroButton = (Button) findViewById(R.id.button_zero);
        mCountButton = (Button) findViewById(R.id.button_count);
        mMessageEditText = findViewById(R.id.editText_main);
        mPublicTextView = findViewById(R.id.text_public_message);

        // get received implicit intent
        Intent outsideIntent = getIntent();
        Uri uri = outsideIntent.getData(); // intent data is always uri
        if (uri != null) {
            String uri_string = getString(R.string.uri_label) + uri.toString();
            TextView textView = findViewById(R.id.text_uri_message);
            textView.setText(uri_string);
        }

        // Restore the state
        if (savedInstanceState != null) {
            // if the public text is visible, set the textview with the saved text
            boolean isVisible = savedInstanceState.getBoolean("public_text_visible");
            if (isVisible){
                mPublicTextView.setText(savedInstanceState.getString("public_text"));
                mPublicTextView.setVisibility(View.VISIBLE);
            }
            mShowCount.setText(savedInstanceState.getString("show_count"));
        }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("show_count", mShowCount.getText().toString());
        if (mPublicTextView.getVisibility() == View.VISIBLE){
            outState.putBoolean("public_text_visible", true);
            outState.putString("public_text", mPublicTextView.getText().toString());
        }
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
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    public void showToast(View view) {
        Toast toast = Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void countUp(View view) {
        mCount++;
        mZeroButton.setBackgroundColor(getResources().getColor(R.color.green));
        if (mCount % 2 == 0)
            view.setBackgroundColor(getResources().getColor(R.color.even));
        else
            view.setBackgroundColor(getResources().getColor(R.color.odd));
        if (mShowCount != null)
            mShowCount.setText(Integer.toString(mCount));
    }

    public void zeroCount(View view) {
        if (mCount == 0) {
            Toast.makeText(this, R.string.toast_zero, Toast.LENGTH_SHORT).show();
        } else {
            mZeroButton.setBackgroundColor(getResources().getColor(R.color.zero_inactive));
            mCountButton.setBackgroundColor(getResources().getColor(R.color.even));
            mCount = 0;
            if (mShowCount != null) {
                mShowCount.setText(Integer.toString(mCount));
            }
        }
    }

    public void aboutScreen(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent); // for when a return result isn't needed
    }

    public void diaryScreen(View view) {
        Intent intent = new Intent(this, DiaryActivity.class);
        startActivityForResult(intent, TEXT_REQUEST); // this is important, remember it!!!
    }

    public void launchSecondActivity(View view) {
        Log.d(LOG_TAG, "Send button clicked!");
        Intent intent = new Intent(this, DiaryActivity.class);
        String message = mMessageEditText.getText().toString();
        mMessageEditText.getText().clear();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // test that the correct Intent is processed
        Log.d(LOG_TAG, String.valueOf(resultCode));
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String publicisedMessage = data.getStringExtra(DiaryActivity.EXTRA_PUBLICISE);
                mPublicTextView.setText(publicisedMessage);
                mPublicTextView.setVisibility(View.VISIBLE);
            }
        }
    }

}