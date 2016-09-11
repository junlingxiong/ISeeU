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
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyModeActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private String mDeviceName; // callee
    private String mPassword;
    private Pubnub mPubNub;
    private String mStdbyChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_mode);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mDeviceName = sharedPreferences.getString(Constants.KEY_DEVICE_NAME, "Unspecified");
        mPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, "Unspecified");

        this.mStdbyChannel = this.mDeviceName + Constants.STDBY_SUFFIX;

        ((TextView) findViewById(R.id.device_name)).setText("Device: " + mDeviceName);
        ((TextView) findViewById(R.id.password)).setText("Password: " + mPassword);

        initPubNub();

        ToggleButton mode = (ToggleButton) findViewById(R.id.privacy_mode_button);
        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                    setUserStatus(Constants.STATUS_AVAILABLE);
                } else {
                    setUserStatus(Constants.STATUS_BUSY);
                }
            }
        });
        mode.setChecked(false); // initial mode is in privacy mode (i.e. status: busy)
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(PrivacyModeActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) { // request for permission
            ActivityCompat.requestPermissions(PrivacyModeActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(this.mPubNub==null){
            initPubNub();
        } else {
            subscribeStdBy();
        }
    }

    /**
     * Subscribe to standby channel so that it doesn't interfere with the WebRTC Signaling.
     */
    public void initPubNub(){
        Log.e(LOG, "initPubNub");
        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.mDeviceName);
        subscribeStdBy();
    }

    /**
     * Subscribe to the standby channel to receive incoming calls
     */
    private void subscribeStdBy() {
        try {
            this.mPubNub.subscribe(mStdbyChannel, new Callback() { // subscribe to incoming calls with a callback
                @Override
                public void successCallback(String channel, Object message) { // received incoming call
                    Log.e(LOG + "MA-success", "successCallback(): MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(Constants.JSON_CALL_USER)) return; //Ignore Signaling messages.
                        String callerName = jsonMsg.getString(Constants.JSON_CALL_USER);
                        // Consider Accept/Reject call here
                        Intent intent = new Intent(PrivacyModeActivity.this, IncomingCallActivity.class);
                        intent.putExtra(Constants.KEY_CALLER_NAME, callerName);
                        startActivity(intent);
                        Log.e(LOG, "subscribeStdBy.successCallback(): received incoming call");
                        // does NOT finish() this activity when there's an incoming call (because if user rejects, we can come back)
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.e(LOG + "MA-iPN", "connectCallback(): CONNECTED: " + message.toString());
                    setUserStatus(Constants.STATUS_AVAILABLE); // set callee (tablet) status to be 'available'
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.e(LOG + "MA-iPN","errorCallback(): ERROR: " + error.toString());
                }
            });
            Log.e(LOG, "subscribeStdBy(): registered to receive incoming calls at " + mStdbyChannel);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set callee status to be AVAILABLE, BUSY or OFFLINE in the standbyChannel
     * @param status
     */
    private void setUserStatus(String status){
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.JSON_STATUS, status);
            this.mPubNub.setState(this.mStdbyChannel, this.mDeviceName, state, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.e(LOG + "MA-sUS","successCallback(): State Set: " + message.toString());
                }
            });
            Log.e(LOG, "setUserStatus(): " + status);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void unsubscribeStdBy() {
        mPubNub.unsubscribe(mStdbyChannel); // unsubscribe from the channel
        Log.e(LOG, "unsubscribeStdBy(): unsubscribed to incoming calls from " + mStdbyChannel);
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
