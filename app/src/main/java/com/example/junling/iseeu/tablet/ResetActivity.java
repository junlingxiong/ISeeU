package com.example.junling.iseeu.tablet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.example.junling.iseeu.util.DatabaseHelper;

public class ResetActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper = new DatabaseHelper(this);
    private SharedPreferences sharedpreferences;
    private EditText passwordView;
    private EditText confirmPassView;

    private String deviceNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // tablet reset password
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MOBILE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        deviceNum  = sharedPreferences.getString(Constants.KEY_DEVICE_NAME, "Unspecified");

        ((TextView) findViewById(R.id.device_name)).setText(" Tablet Number: " + deviceNum);

        passwordView = (EditText) findViewById(R.id.password);
        confirmPassView = (EditText) findViewById(R.id.confirmPass);

    }

    public void reset(View v) {
        //check pass and confirmpass
        passwordView.setError(null);
        confirmPassView.setError(null);

        String password = passwordView.getText().toString();
        String confirmPass = confirmPassView.getText().toString();

        if(!password.equals(confirmPass)){
            showAlertDialog_confirmPass();
        }else{
            dbHelper.resetPassword(deviceNum,password);

            //for video
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                // store the device name and password into persistent storage, after which retrieval is ready for video call
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_DEVICE_NAME, deviceNum);

                editor.commit();
                // display registration success dialogue
                new AlertDialog.Builder(this)
                        .setTitle("Registration Successful")
                        .setMessage("Device Name: " + deviceNum + "\n" + "New Password: " + password)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(ResetActivity.this, PrivacyModeActivity.class));
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            } else {
                Toast.makeText(this, "Please enable WiFi or cellular data to video-chat!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAlertDialog_confirmPass(){
        final AlertDialog alertDialog = new AlertDialog.Builder(ResetActivity.this).create();
        alertDialog.setTitle(R.string.warning);
        alertDialog.setMessage(getString(R.string.confirm_pass_error));
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void back(View V) {}
}
