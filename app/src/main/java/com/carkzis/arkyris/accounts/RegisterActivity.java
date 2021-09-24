package com.carkzis.arkyris.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.arkyris.R;

/**
 * Activity for registering a new user.
 */
public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel mViewModel;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword1;
    private EditText mPassword2;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        // Initialise view variables
        mUsername = findViewById(R.id.edittext_new_username);
        mEmail = findViewById(R.id.edittext_email);
        mPassword1 = findViewById(R.id.edittext_register_password1);
        mPassword2 = findViewById(R.id.edittext_register_password2);
        mLoadingIndicator = findViewById(R.id.loading_indicator_register);

        setUpConnectionErrorObserver();
        setUpResponseCodeObserver();
    }

    /**
     * Sets up an observer for viewing and responding to connection errors.
     */
    private void setUpConnectionErrorObserver() {
        // This shows if there is no connection, the call to the database fails.
        mViewModel.getConnectionError().observe(this, connectionError -> {
            mLoadingIndicator.setVisibility(View.GONE);
            // Update cached copy of words in adapter
            if (connectionError) {
                Toast.makeText(
                        getApplicationContext(),
                        "Connection error...",
                        Toast.LENGTH_SHORT).show();
                mViewModel.connectionErrorHandled();
            }
        });
    }

    /**
     * Sets up an observer for responding to the response code when attempting to log in.
     * 200 is a pass, 400 is returned if the username already exists, any other errors
     * (except no connection) show here.
     */
    private void setUpResponseCodeObserver() {
        mViewModel.getRegisterResponseCode().observe(this, responseCode -> {
            mLoadingIndicator.setVisibility(View.GONE);
            if (responseCode == 200) {
                Toast.makeText(
                        getApplicationContext(),
                        "Account created, please login!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else if (responseCode == 400) {
                Toast.makeText(
                        getApplicationContext(),
                        "That username already exists...",
                        Toast.LENGTH_SHORT).show();
                mViewModel.registerResponseHandled();
            } else if (responseCode != -1) {
                Toast.makeText(
                        getApplicationContext(),
                        "Apologies, something went wrong and we don't know why.",
                        Toast.LENGTH_SHORT).show();
                mViewModel.registerResponseHandled();
            }
        });
    }

    /**
     * Validates the registration inputs via the ViewModel and inserts the data.
     */
    public void registerUser(View view) {
        // This will need to be tested against the online database.
        String username = mUsername.getText().toString();

        // Can test locally.
        String email = mEmail.getText().toString();
        String password1 = mPassword1.getText().toString();
        String password2 = mPassword2.getText().toString();

        if (username.equals("") || email.equals("") || password1.equals("")
                || password2.equals("")) {
            Toast.makeText(
                    this,
                    "Not all the fields have been completed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean usernameAuthenticated = mViewModel.testUsername(username);
        boolean emailAuthenticated = mViewModel.testEmail(email);
        boolean passwordMatchAuthenticated = mViewModel.testPasswordMatch(password1, password2);
        boolean passwordFormatAuthenticated = mViewModel.testPasswordFormat(password1);

        String registerFailureMessage = mViewModel.failureMessage(usernameAuthenticated,
                emailAuthenticated, passwordMatchAuthenticated, passwordFormatAuthenticated);

        if (registerFailureMessage != null) {
            Toast.makeText(
                    this,
                    registerFailureMessage,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mViewModel.insertUser(username, email, password1);

    }

}