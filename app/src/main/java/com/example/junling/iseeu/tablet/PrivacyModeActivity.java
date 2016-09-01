package com.example.junling.iseeu.tablet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mDevice = sharedPreferences.getString(Constants.KEY_DEVICE_NAME, "Unspecified");
        mPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, "Unspecified");
        ((TextView) findViewById(R.id.device_name)).setText("Device Name: " + mDevice);
        ((TextView) findViewById(R.id.password)).setText("Password: " + mPassword);

        mPubNub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        mPubNub.setUUID(this.mDevice);

        ((ToggleButton) findViewById(R.id.privacy_mode_button)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // default state is checked
                    // check network connection
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo == null || !networkInfo.isConnected()) { // no established internet connection
                        Toast.makeText(PrivacyModeActivity.this, "Please enable WiFi or cellular data to video-chat!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    registerToReceive(); // register to receive incoming calls
                } else {
                    declineToReceive(); // unsubscribe from the incoming call channel
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(PrivacyModeActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) { // request for permission
            ActivityCompat.requestPermissions(PrivacyModeActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA);
        }
    }

    /**
     * Subscribe to the standby channel to receive incoming calls
     *
     * pubnub.subscribe() should be called before pubnub.unsubscribe()
     */
    private void registerToReceive() {
        try {
            String stdbyChannel = this.mDevice + Constants.STDBY_SUFFIX;
            this.mPubNub.subscribe(stdbyChannel, new Callback() { // subscribe to incoming calls with a callback
                @Override
                public void successCallback(String channel, Object message) {
                    Log.e("MA-success", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(Constants.JSON_CALL_USER)) return;
                        String callerName = jsonMsg.getString(Constants.JSON_CALL_USER);
                        // Consider Accept/Reject call here
                        Intent intent = new Intent(PrivacyModeActivity.this, IncomingCallActivity.class);
                        intent.putExtra(Constants.KEY_CALLER_NAME, callerName);
                        startActivity(intent);
                        // does NOT finish() this activity when there's an incoming call (because if user rejects, we can come back)
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Log.e(LOG, "initPubNub(): registered to receive incoming calls at " + stdbyChannel);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private void declineToReceive() {
        String stdbyChannel = this.mDevice + Constants.STDBY_SUFFIX;
        mPubNub.unsubscribe(stdbyChannel); // unsubscribe from the channel
        Log.e(LOG, "declineToReceive(): unsubscribed to incoming calls from " + stdbyChannel);
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
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // permission denied
                    findViewById(R.id.privacy_mode_button).setEnabled(false);
                    Toast.makeText(PrivacyModeActivity.this, "Please enable camera access to start video-chat!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
