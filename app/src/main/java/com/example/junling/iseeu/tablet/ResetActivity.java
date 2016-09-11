package com.example.junling.iseeu.tablet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.example.junling.iseeu.util.DatabaseHelper;

public class ResetActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private SharedPreferences mSharedPref;

    private EditText mPassword;
    private EditText mConfirmPassword;
    private String mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mSharedPref = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mDeviceName = mSharedPref.getString(Constants.KEY_DEVICE_NAME, "Unspecified");

        ((TextView) findViewById(R.id.device_name)).setText(" Device: " + mDeviceName);

        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPass);

    }

    public void reset(View v) {
        // check password and confirm password
        mPassword.setError(null);
        mConfirmPassword.setError(null);

        String password = mPassword.getText().toString();
        String confirmPass = mConfirmPassword.getText().toString();

        // ensure internet is connected before going into privacy mode (subscribing to stdbychannel needs connectivity)
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) { // no established internet connection
            Toast.makeText(ResetActivity.this, "Please make sure device is connected to Internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPass)) {
            new AlertDialog.Builder(ResetActivity.this)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setTitle(R.string.warning)
            .setMessage(getString(R.string.confirm_pass_error))
            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create().show();
        } else {

            // store new password into database and shared preference
            mDbHelper.resetPassword(mDeviceName, password);
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(Constants.KEY_PASSWORD, password);
            editor.commit();

            // display registration success dialogue
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Registration Successful")
                    .setMessage("Device: " + mDeviceName + "\n" + "Password: " + password)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(ResetActivity.this, PrivacyModeActivity.class));
                            finish();
                        }
                    })
                    .create().show();
            Log.e(LOG, "reset(): reset successfully, transitioning into privacy mode");
        }
    }

    public void back(View V) {}
}
