package com.example.junling.iseeu.tablet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.example.junling.iseeu.util.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private SharedPreferences mSharedPref;
    private EditText mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tablet);
        mDeviceName = (EditText) findViewById(R.id.device_name);
    }


    public void register(View v) {
        // check if device name exist
        mDeviceName.setError(null);
        String deviceName = mDeviceName.getText().toString();
        boolean hasTablet = mDbHelper.hasTablet(deviceName);
        if(!hasTablet) {
            new android.app.AlertDialog.Builder(RegisterActivity.this)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setTitle(R.string.warning)
                    .setMessage(getString(R.string.login_fail_message_device))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            // store device number in the shared preferences
            mSharedPref = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(Constants.KEY_DEVICE_NAME, deviceName);
            editor.commit();

            //if device exist, redirect to reset password page
            Intent intent = new Intent(RegisterActivity.this, ResetActivity.class);
            startActivity(intent);
            finish();
            Log.e(LOG, "register(): register successful, transitioning into reset password");
        }
    }

    public void back(View v) {
        finish();
    }
}
