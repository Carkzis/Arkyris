package com.carkzis.arkyris.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.arkyris.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private ChangePasswordViewModel mViewModel;
    private EditText mOldPassword;
    private EditText mNewPassword1;
    private EditText mNewPassword2;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);

        mOldPassword = findViewById(R.id.edittext_old_password);
        mNewPassword1 = findViewById(R.id.edittext_new_password1);
        mNewPassword2 = findViewById(R.id.edittext_new_password2);
        mLoadingIndicator = findViewById(R.id.loading_indicator_change_password);

        mViewModel.getPasswordEntryFailure().observe(this, failure -> {
            mLoadingIndicator.setVisibility(View.GONE);
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
            if (!failure.equals("handled")) {
                mViewModel.passwordTestHandled();
            }

        });

        mViewModel.getChangePasswordSuccess().observe(this, response -> {
            mLoadingIndicator.setVisibility(View.GONE);
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
            if (!response.equals("handled")) {
                mViewModel.changePasswordHandled();
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

        mLoadingIndicator.setVisibility(View.VISIBLE);
        // This will test passwords first
        mViewModel.changePassword(oldPassword, newPassword1, newPassword2);

    }
}