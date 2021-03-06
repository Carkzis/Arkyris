package com.example.arkyris.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.arkyris.MainActivity;
import com.example.arkyris.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mViewModel;
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        //preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = mViewModel.getToken();
        if (token != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        // Initialise view variables
        mUsername = findViewById(R.id.edittext_username);
        mPassword = findViewById(R.id.edittext_password);

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

        mViewModel.getLoginResponseCode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer responseCode) {
                if (responseCode == 200) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Success!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Login failed, have you entered the correct details?",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void registerScreen(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (username.equals("") || password.equals("")) {
            Toast.makeText(
                    this,
                    "Not all the fields have been completed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.authenticateUser(username, password);

    }

}