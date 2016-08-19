package com.example.junling.iseeu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {

    //UI References
    private EditText registrationEmailView;
    private EditText registrationPassword1View;
    private EditText registrationPassword2View;
    private View registerProgressView;
    private View registerFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationEmailView = (EditText) findViewById(R.id.registration_email);
        registrationPassword1View = (EditText) findViewById(R.id.registration_password);
        registrationPassword2View = (EditText) findViewById(R.id.registration_repeat_password);

        registerProgressView = (View) findViewById(R.id.register_progress);
        registerFormView = (View) findViewById(R.id.register_form);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

}
