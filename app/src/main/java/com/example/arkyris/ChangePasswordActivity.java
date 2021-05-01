package com.example.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class ChangePasswordActivity extends AppCompatActivity {

    private ChangePasswordViewModel mViewModel;
    private EditText mOldPassword;
    private EditText mNewPassword1;
    private EditText mNewPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);

        mOldPassword = findViewById(R.id.edittext_old_password);
        mNewPassword1 = findViewById(R.id.edittext_new_password1);
        mNewPassword2 = findViewById(R.id.edittext_new_password2);

        mViewModel.getPasswordEntryFailure().observe(this, failure -> {
            // update cached copy of words in adapter
            if (failure.equals("not_equal")) {
                Toast.makeText(
                        getApplicationContext(),
                        "The new passwords do not match!",
                        Toast.LENGTH_SHORT).show();
            } else if (failure.equals("incorrect_format")) {
                Toast.makeText(
                        getApplicationContext(),
                        "New password requirements not met!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getChangePasswordSuccess().observe(this, response -> {
            if (response.equals("failed")) {
                Toast.makeText(
                        getApplicationContext(),
                        "Something went wrong...",
                        Toast.LENGTH_SHORT).show();
            } else if (response.equals("no_connection")) {
                Toast.makeText(
                        getApplicationContext(),
                        "No connection.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Password changed successfully.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void changePassword(View view) {
        String oldPassword = mOldPassword.getText().toString();
        String newPassword1 = mNewPassword1.getText().toString();
        String newPassword2 = mNewPassword2.getText().toString();

        // this could be moved to ViewModel
        if (oldPassword.equals("") || newPassword1.equals("") || newPassword2.equals("")) {
            Toast.makeText(
                    this,
                    "Not all the fields have been completed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // This will test passwords first
        mViewModel.changePassword(oldPassword, newPassword1, newPassword2);

    }
}