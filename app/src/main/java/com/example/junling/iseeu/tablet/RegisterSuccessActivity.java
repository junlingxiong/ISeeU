package com.example.junling.iseeu.tablet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junling.iseeu.MainActivity;
import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;

public class RegisterSuccessActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private String mDevice;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success_tablet);

        Bundle info = getIntent().getExtras();
        if (info == null || !info.containsKey(Constants.KEY_DEVICE_NAME) || !info.containsKey(Constants.KEY_PASSWORD)) {
            startActivity(new Intent(this, RegisterActivity.class));
            Toast.makeText(this, "Please re-enter the tablet device name and password!", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        mDevice = info.getString(Constants.KEY_DEVICE_NAME);
        mPassword = info.getString(Constants.KEY_PASSWORD);
        ((TextView) findViewById(R.id.device_name)).setText("Device Name: " + (mDevice.equals("")? "Unknown" : mDevice));
        ((TextView) findViewById(R.id.password)).setText("Password: " + (mPassword.equals("")? "Unknown" : mPassword));

    }

    public void back(View v) {
        Bundle info = new Bundle();
        info.putString(Constants.KEY_DEVICE_NAME, mDevice);
        info.putString(Constants.KEY_PASSWORD, mPassword);
        startActivity(new Intent(RegisterSuccessActivity.this, PrivacyModeActivity.class).putExtras(info));
        this.finish();
    }

    public void reset(View v) {
        startActivity(new Intent(RegisterSuccessActivity.this, ResetActivity.class));
    }
}
