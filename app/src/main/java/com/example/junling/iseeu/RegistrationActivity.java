package com.example.junling.iseeu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "NameKey";
    public static final String DeviceNum = "DeviceNumKey";

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
        System.out.printf(deviceNumber);
        String password = passwordView.getText().toString();
        String callerName = callerNameView.getText().toString();

        boolean hasTablet = dbHelper.hasTablet(deviceNumber);
        if(!hasTablet){
            //no such tablet
            showAlertDialog_device();
        }
        else {
            boolean isPassCorrect = dbHelper.checkPass(deviceNumber, password);
            if(!isPassCorrect)
                showAlertDialog_pass();
            else{
                dbHelper.createCaller(deviceNumber, callerName);

                //After log in successfully, create a log in session to store login user's information
                String nameSession = callerName;
                String deviceNumSession = deviceNumber;

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(Name, nameSession);
                editor.putString(DeviceNum, deviceNumSession);
                editor.commit();

                Intent intent = new Intent(this, GreetingActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }

    private void showAlertDialog_device(){

        final AlertDialog alertDialog = new AlertDialog.Builder(RegistrationActivity.this).create();
        alertDialog.setTitle(R.string.warning);
        alertDialog.setMessage(getString(R.string.login_fail_message_device));
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showAlertDialog_pass(){

        final AlertDialog alertDialog = new AlertDialog.Builder(RegistrationActivity.this).create();
        alertDialog.setTitle(R.string.warning);
        alertDialog.setMessage(getString(R.string.login_fail_message_pass));
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void back (View view){
        finish();
    }

}
