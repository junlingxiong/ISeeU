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
import com.example.junling.iseeu.util.Constants;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tablet);
    }

    public void register(View v) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String deviceName = ((EditText) findViewById(R.id.device_name)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            // store the device name and password into persistent storage, after which retrieval is ready for video call
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.KEY_DEVICE_NAME, deviceName);
            editor.putString(Constants.KEY_PASSWORD, password);
            editor.commit();
            // display registration success dialogue
            new AlertDialog.Builder(this)
                    .setTitle("Registration Successful")
                    .setMessage("Device Name: " + deviceName + "\n" + "Password: " + password)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RegisterActivity.this, PrivacyModeActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RegisterActivity.this, ResetActivity.class));
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else {
            Toast.makeText(this, "Please enable WiFi or cellular data to video-chat!", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View v) {}
}
