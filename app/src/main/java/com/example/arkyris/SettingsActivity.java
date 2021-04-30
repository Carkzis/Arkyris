package com.example.arkyris;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        mViewModel.getLogoutSuccess().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String successString) {
                // update cached copy of words in adapter
                if (successString != null) {
                    String toastMessage;
                    boolean loggedOut = false;
                    switch (successString) {
                        case "success_one":
                            toastMessage = "Logged out!";
                            loggedOut = true;
                            break;
                        case "no_connection_one":
                            toastMessage = "Logged out...";
                            loggedOut = true;
                            break;
                        case "success_all":
                            toastMessage = "Logged out of all accounts!";
                            loggedOut = true;
                            break;
                        case "fail_all":
                            toastMessage = "Could not log out of all accounts, " +
                                    "try logging out locally.";
                            break;
                        case "no_connection_all":
                            toastMessage = "Could not log out of all accounts, " +
                                    "there is no connection.";
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + successString);
                    }
                    Toast.makeText(
                            getApplicationContext(),
                            toastMessage,
                            Toast.LENGTH_SHORT).show();
                    if (loggedOut) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        // Need to prevent the user from going back to the logged in area!
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void logoutUser(View view) {
        String[] choices = {
                getString(R.string.logout_one),
                getString(R.string.logout_all),
                getString(R.string.cancel_logout)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leaving so soon?");
        builder.setItems(choices, (dialog, which) -> {
            if (getString(R.string.logout_one).equals(choices[which])) {
                mViewModel.logout();
            } else if (getString(R.string.logout_all).equals(choices[which])) {
                mViewModel.logoutAll();
            } else {
                // Do nothing
            }
        });
        builder.show();
    }


}