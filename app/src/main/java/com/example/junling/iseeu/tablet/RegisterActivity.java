package com.example.junling.iseeu.tablet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
            Bundle info = new Bundle();
            info.putString(Constants.KEY_DEVICE_NAME, deviceName);
            info.putString(Constants.KEY_PASSWORD, password);
            startActivity(new Intent(RegisterActivity.this, RegisterSuccessActivity.class).putExtras(info));
        } else {
            Toast.makeText(RegisterActivity.this, "Please enable WiFi or cellular data to video-chat!", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View v) {}
}
