package com.example.junling.iseeu.tablet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import me.kevingleason.pnwebrtc.PnPeerConnectionClient;

public class PrivacyModeActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private String mDevice; // callee
    private String mPassword;
    private Pubnub mPubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_mode);

        Bundle info = getIntent().getExtras();
        if (info == null || !info.containsKey(Constants.KEY_DEVICE_NAME) || !info.containsKey(Constants.KEY_PASSWORD)) {
            startActivity(new Intent(this, RegisterActivity.class));
            Toast.makeText(this, "Please re-enter the tablet device name and password!", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        mDevice = info.getString(Constants.KEY_DEVICE_NAME, "tablet"); // mDevice = "tablet" if not device name is entered by the user, for debugging
        mPassword = info.getString(Constants.KEY_PASSWORD);
        ((TextView) findViewById(R.id.device_name)).setText("Device Name: " + (mDevice.equals("")? "Unknown" : mDevice));
        ((TextView) findViewById(R.id.password)).setText("Password: " + (mPassword.equals("")? "Unknown" : mPassword));

        if (ContextCompat.checkSelfPermission(PrivacyModeActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.privacy_mode_button).setEnabled(true);
            initPubNub();
        } else { // request for permission
            ActivityCompat.requestPermissions(PrivacyModeActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA);
        }

        ((ToggleButton) findViewById(R.id.privacy_mode_button)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerToReceive(); // register to receive incoming calls
                } else {
                    declineToReceive(); // unsubscribe from the incoming call channel
                }
            }
        });
    }

    private void initPubNub() { // handles receiving calls
        mPubNub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        mPubNub.setUUID(this.mDevice);
    }

    private void registerToReceive() {
        try {
            String stdbyChannel = this.mDevice + Constants.STDBY_SUFFIX;
            this.mPubNub.subscribe(stdbyChannel, new Callback() { // subscribe to incoming calls with a callback
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-success", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(Constants.JSON_CALL_USER)) return;
                        String caller = jsonMsg.getString(Constants.JSON_CALL_USER);
                        // Consider Accept/Reject call here
                        Bundle info = new Bundle();
                        info.putString(Constants.JSON_USER_NAME, mDevice); // callee
                        info.putString(Constants.JSON_CALL_USER, caller); // caller
                        startActivity(new Intent(PrivacyModeActivity.this, IncomingCallActivity.class).putExtras(info));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
        Log.e(LOG, "initPubNub(): registered to receive incoming calls");
    }

    private void declineToReceive() {
        String stdbyChannel = this.mDevice + Constants.STDBY_SUFFIX;
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.mDevice);
        mPubNub.unsubscribe(stdbyChannel); // unsubscribe from the channel
        Log.e(LOG, "declineToReceive(): unsubscribed to incoming calls");
    }

    public void reset(View v) {
        startActivity(new Intent(PrivacyModeActivity.this, ResetActivity.class));
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findViewById(R.id.privacy_mode_button).setEnabled(true);
                    initPubNub();
                } else {
                    findViewById(R.id.privacy_mode_button).setEnabled(false);
                    Toast.makeText(PrivacyModeActivity.this, "Please enable camera access to start video-chat!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
