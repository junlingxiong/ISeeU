package com.example.junling.iseeu.tablet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.mobile.GreetingActivity;
import com.example.junling.iseeu.util.Constants;
import com.example.junling.iseeu.util.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper = new DatabaseHelper(this);
    private SharedPreferences sharedpreferences;
    private EditText deviceNumberView;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "NameKey";
    public static final String DeviceNum = "DeviceNumKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tablet);
        deviceNumberView = (EditText) findViewById(R.id.device_name);
    }


    public void register(View v) {
        //check if device number exist
        deviceNumberView.setError(null);
        String deviceNumber = deviceNumberView.getText().toString();
        boolean hasTablet = dbHelper.hasTablet(deviceNumber);
        if(!hasTablet){
            //no such tablet
            showAlertDialog_device();
        }else{
            //store device number in the sharepreference
            String deviceNumSession = deviceNumber;
            sharedpreferences = getSharedPreferences(Constants.MOBILE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.KEY_DEVICE_NAME, deviceNumSession);
            editor.commit();

            //if device exist, redirect to reset password page
            Intent intent = new Intent(this, ResetActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void showAlertDialog_device(){

        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle(R.string.warning);
        alertDialog.setMessage(getString(R.string.login_fail_message_device));
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void back(View v) {
        finish();
    }
}
