package com.example.arkyris;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterViewModel extends ViewModel {

    public boolean testEmail (String email) {

        // Thank https://howtodoinjava.com/ for this ghastly thing...
        String emailRegex =
                "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\." +
                        "[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() == true){
            Log.e("HELP", "true");
        } else {
            Log.e("HELP", "false");
        }

        return matcher.matches();
    }

}