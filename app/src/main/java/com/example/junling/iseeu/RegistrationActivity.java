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
    private EditText deviceNumber;
    private EditText password;
    private EditText callerName;
    private View registerFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        deviceNumber = (EditText) findViewById(R.id.deviceNumber);
        password = (EditText) findViewById(R.id.password);
        callerName = (EditText) findViewById(R.id.callerName);

        registerFormView = (View) findViewById(R.id.register_form);
    }

}
