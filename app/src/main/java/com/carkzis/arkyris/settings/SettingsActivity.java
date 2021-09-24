package com.carkzis.arkyris.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.carkzis.arkyris.accounts.LoginActivity;
import com.carkzis.arkyris.entries.IrisViewModel;
import com.example.arkyris.R;

import java.util.Objects;

/**
 * Activity for allowing the user to change their password or logging out.
 */
public class SettingsActivity extends AppCompatActivity {

    private TextView mUsername;
    private SettingsViewModel mViewModel;
    private IrisViewModel mIrisViewModel;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialise toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mUsername = findViewById(R.id.text_settings_name);
        mLoadingIndicator = findViewById(R.id.loading_indicator_settings);

        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        mIrisViewModel = ViewModelProviders.of(this).get(IrisViewModel.class);

        setUpAccountName();
        setUpLogoutSuccessObserver();

    }

    /**
     * Observes the account name from the repository.
     */
    public void setUpAccountName() {
        mViewModel.getAccountName().observe(this, username -> {
            // update cached copy of words in adapter
            if (username != null) {
                mUsername.setText(username);
            }
        });
    }

    /**
     * Observes the success of logging the user out.
     */
    public void setUpLogoutSuccessObserver() {
        mViewModel.getLogoutSuccess().observe(this, successString -> {
            // update cached copy of words in adapter
            mLoadingIndicator.setVisibility(View.GONE);
            if (successString != null) {
                String toastMessage = null;
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
                    case "handled":
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + successString);
                }

                if (!successString.equals("handled")) {
                    Toast.makeText(
                            getApplicationContext(),
                            toastMessage,
                            Toast.LENGTH_SHORT).show();
                    mViewModel.logoutSuccessHandled();
                }

                if (loggedOut) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // Need to clear the user Room entries so they do not flash up for the
                    // next user
                    mIrisViewModel.deleteAll();
                    // Need to prevent the user from going back to the logged in area!
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Shows a dialog with options relating to logging the member out, and
     * sends a request to the server via the ViewModel.
     */
    public void logoutUser(View view) {
        String[] choices = {
                getString(R.string.logout_one),
                getString(R.string.logout_all),
                getString(R.string.cancel_logout)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leaving so soon?");
        builder.setItems(choices, (dialog, which) -> {
            if (getString(R.string.logout_one).equals(choices[which])) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mViewModel.logout();
            } else if (getString(R.string.logout_all).equals(choices[which])) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                mViewModel.logoutAll();
            }
        });
        builder.show();
    }

    /**
     * Sets up an intent to traverse to the ChangePasswordActivity.
     */
    public void changePasswordScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }
}