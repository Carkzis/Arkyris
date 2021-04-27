package com.example.arkyris;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button registerButton = findViewById(R.id.button_register);

    }

    //    public void registerScreen(View view) {
//        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//        Fragment registerFragment = new RegisterFragment();
//        fragmentTransaction.replace(R.id.login_fragment, registerFragment).commit();
//    }

}