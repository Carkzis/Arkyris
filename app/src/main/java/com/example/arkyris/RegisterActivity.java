package com.example.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel mViewModel;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword1;
    private EditText mPassword2;

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


        mViewModel.getConnectionError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connectionError) {
                // update cached copy of words in adapter
                if (connectionError) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Connection error...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getRegisterResponseCode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer responseCode) {
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
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Apologies, something went wrong and we don't know why.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loginScreen(View view) {
        // This will need to be tested against the online database
        String username = mUsername.getText().toString();

        // Can test locally
        String email = mEmail.getText().toString();
        String password1 = mPassword1.getText().toString();
        String password2 = mPassword2.getText().toString();

        if (username.equals("") || email.equals("") || password1.equals("") || password2.equals("")) {
            Toast.makeText(
                    this,
                    "Not all the fields have been completed.",
                    Toast.LENGTH_SHORT).show();
        }

        boolean usernameAuthenicated = mViewModel.testUsername(username);
        boolean emailAuthenticated = mViewModel.testEmail(email);
        boolean passwordMatchAuthenticated = mViewModel.testPasswordMatch(password1, password2);
        boolean passwordFormatAuthenticated = mViewModel.testPasswordFormat(password1);

        String registerFailureMessage = mViewModel.failureMessage(usernameAuthenicated,
                emailAuthenticated, passwordMatchAuthenticated, passwordFormatAuthenticated);

        if (registerFailureMessage != null) {
            Toast.makeText(
                    this,
                    registerFailureMessage,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.insertUser(username, email, password1);

        // for testing
        //mViewModel.insertUser("MasterMan", "dragon@dragon.com", "nooooo");

    }



}