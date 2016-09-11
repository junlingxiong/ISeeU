package com.example.junling.iseeu.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.example.junling.iseeu.util.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private EditText mDeviceName;
    private EditText mPassword;
    private EditText mCaller;

    private DatabaseHelper mDbHelper;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);

        mDeviceName = (EditText) findViewById(R.id.device_name);
        mPassword = (EditText) findViewById(R.id.password);
        mCaller = (EditText) findViewById(R.id.callerName);

        mDbHelper = new DatabaseHelper(this);
        mSharedPref = getSharedPreferences(Constants.MOBILE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void register (View view){
        mDeviceName.setError(null);
        mPassword.setError(null);
        mCaller.setError(null);

        String deviceName = mDeviceName.getText().toString();
        String password = mPassword.getText().toString();
        String callerName = mCaller.getText().toString();

        // ensure internet is connected before going into greetings (subscribing to stdbychannel needs connectivity)
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) { // no established internet connection
            Toast.makeText(RegisterActivity.this, "Please make sure device is connected to Internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasTablet = mDbHelper.hasTablet(deviceName);
        if(!hasTablet){ // no such tablet
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
            boolean isPassCorrect = mDbHelper.checkPass(deviceName, password);
            if(!isPassCorrect) {
                new android.app.AlertDialog.Builder(RegisterActivity.this)
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .setTitle(R.string.warning)
                        .setMessage(getString(R.string.login_fail_message_pass))
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                // store device name and caller name into database and shared preference
                mDbHelper.createCaller(deviceName, callerName);
                mSharedPref = getSharedPreferences(Constants.MOBILE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString(Constants.KEY_CALLER_NAME, callerName);
                editor.putString(Constants.KEY_DEVICE_NAME, deviceName);
                editor.commit();

                Intent intent = new Intent(this, GreetingActivity.class);
                startActivity(intent);
                finish();
                Log.e(LOG, "register(): register successful, transitioning to greetings");
            }

        }

    }

    public void back (View view){
        finish();
    }

}
