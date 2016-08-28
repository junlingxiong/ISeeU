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

import com.example.junling.iseeu.entities.*;

public class RegistrationActivity extends AppCompatActivity {

    //UI References
    private EditText deviceNumberView;
    private EditText passwordView;
    private EditText callerNameView;
    private View registerFormView;

    private Caller caller;
    private Tablet device;

    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        deviceNumberView = (EditText) findViewById(R.id.deviceNumber);
        passwordView = (EditText) findViewById(R.id.password);
        callerNameView = (EditText) findViewById(R.id.callerName);

        registerFormView = (View) findViewById(R.id.register_form);
    }

    public void register (View view){
        deviceNumberView.setError(null);
        passwordView.setError(null);
        callerNameView.setError(null);

        boolean cancel = false;
        View focusView = null;

        String deviceNumber = deviceNumberView.getText().toString();
        String password = passwordView.getText().toString();
        String callerName = callerNameView.getText().toString();

        boolean hasTablet = dbHelper.hasTablet(deviceNumber);
        if(!hasTablet){

        }else{

        }


    }

    public void back (View view){
        finish();
    }

}
