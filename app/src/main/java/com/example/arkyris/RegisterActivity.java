package com.example.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    }

    public void loginScreen(View view) {
        // This will need to be tested against the online database
        String username = mUsername.getText().toString();

        // Can test locally
        String email = mEmail.getText().toString();
        String password1 = mPassword1.getText().toString();
        String password2 = mPassword2.getText().toString();

        boolean emailAuthenticated = mViewModel.testEmail(email);
        boolean passwordMatchAuthenticated = mViewModel.testPasswordMatch(password1, password2);
        boolean passwordFormatAuthenticated = mViewModel.testPasswordFormat(password1);

        String registerFailureMessage = mViewModel.failureMessage(emailAuthenticated,
                passwordMatchAuthenticated, passwordFormatAuthenticated);

        if (registerFailureMessage != null) {
            Toast.makeText(
                    this,
                    registerFailureMessage,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }



}