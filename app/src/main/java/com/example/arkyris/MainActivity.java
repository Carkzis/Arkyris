package com.example.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private int mCount = 0;
    private TextView mShowCount;
    private Button mZeroButton;
    private Button mCountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Let us begin!");
        mShowCount = (TextView) findViewById(R.id.show_count);
        mZeroButton = (Button) findViewById(R.id.button_zero);
        mCountButton = (Button) findViewById(R.id.button_count);
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
        startActivity(intent);
    }

    public void diaryScreen(View view) {
        Intent intent = new Intent(this, DiaryActivity.class);
        startActivity(intent);
    }
}