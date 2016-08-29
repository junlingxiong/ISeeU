package com.example.junling.iseeu.mobile;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An interface that places outgoing video calls
 *
 * Mobile
 */
public class GreetingActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private Pubnub mPubNub;
    private String mUsername; // caller
    private String mCallNumET; // callee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        Bundle info = getIntent().getExtras();
        if (info == null || !info.containsKey(Constants.KEY_DEVICE_NAME) || !info.containsKey(Constants.KEY_CALLER_NAME)) {
            startActivity(new Intent(this, com.example.junling.iseeu.tablet.RegisterActivity.class));
            Toast.makeText(this, "Please re-enter the tablet device name and caller name!", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        mCallNumET  = info.getString(Constants.KEY_DEVICE_NAME);
        mUsername = info.getString(Constants.KEY_CALLER_NAME);

        // TODO: this can be checked at log in page / register page (e.g. auto-fill edit text if value exists in shared pref)
        //get log in session if exists
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String callerName = prefs.getString("NameKey", null);
        String deviceNumber = prefs.getString("DeviceNumKey", null);

        // welcome message in main page.
        ((TextView) findViewById(R.id.greeting_text)).setText("Hello " + mUsername + "!");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) { // no established internet connection
            Toast.makeText(GreetingActivity.this, "Please enable WiFi or cellular data to video-chat!", Toast.LENGTH_SHORT).show();
            findViewById(R.id.call_button).setEnabled(false);
            return;
        }

        if (ContextCompat.checkSelfPermission(GreetingActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.call_button).setEnabled(true);
            initPubNub();
        } else { // request for permission
            ActivityCompat.requestPermissions(GreetingActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA);
        }
    }

    /**
     * This function subscribes you to the mUsername's standby channel.
     * When it receives a message, it pulls out the JSON_CALL_USER field, call_user.
     *
     * In this demo, we will create a video that simply requires you pass it your user_name in the intent.
     * If you also provide the intent with a JSON_CALL_USER, it will try to auto-connect you to that user.
     * You can see that we send the user to VideoChatActivity
     */
    private void initPubNub() { // handles receiving calls
        this.mPubNub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.mUsername);
    }

    /**
     * an EditText and a Button in your activity.
     * When we push the button, we will invoke the function @makeCall
     * which will send a JSON message to the other user to show we would like to chat.
     * @param view
     */
    public void makeCall(View view){ // check validity of the call number
        String callNum = mCallNumET; // callee
        callNum = ((EditText) findViewById(R.id.callerName)).getText().toString(); // TODO: for debugging
        if (callNum.isEmpty() || callNum.equals(this.mUsername)) {
            Toast.makeText(this, "Call number is not valid!", Toast.LENGTH_SHORT).show();
            return;
        }
        dispatchCall(callNum);
    }

    /**
     * Check that user is online. If they are, dispatch the call by publishing to their standby
     * channel. If the publish was successful, then change activities over to the video chat.
     * The called user will then have the option to accept of decline the call. If they accept,
     * they will be brought to the video chat activity as well, to connect video/audio. If
     * they decline, a hangup will be issued, and the VideoChat adapter's @onHangup callback will be invoked.
     *
     * This sends a JSONObject to the other user's standby channel with the information {"call_user":"<YourUsername>"}.
     * We then send the user to the VideoChatActivity.
     *
     * This will trigger the other user's PubNub callback so both users will be in VideoChatActivity calling each other.
     * This is alright since the PnWebRTC API does not allow duplicate calls, so only the first SDP to be received will be used.
     * @param callNum
     */
    public void dispatchCall(final String callNum) {
        final String callNumStdBy = callNum + Constants.STDBY_SUFFIX;
        this.mPubNub.hereNow(callNumStdBy, new Callback() { // Read presence information from a channel
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " +" CH - " + callNumStdBy + " " + message.toString());
                try {
//                    int occupancy = ((JSONObject) message).getInt(Constants.JSON_OCCUPANCY);
//                    if (occupancy == 0) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(GreetingActivity.this, "User " + callNum + " is not online!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        return;
//                    }
                    JSONObject jsonCall = new JSONObject();
                    jsonCall.put(Constants.JSON_CALL_USER, mUsername);
                    jsonCall.put(Constants.JSON_CALL_TIME, System.currentTimeMillis());
                    mPubNub.publish(callNumStdBy, jsonCall, new Callback() { // publish an outgoing call with a callback
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.e("MA-dC", "SUCCESS: " + message.toString());
                            Bundle info = new Bundle();
                            info.putString(Constants.JSON_USER_NAME, mUsername); // caller: mobile
                            info.putString(Constants.JSON_CALL_USER, callNum); // callee: tablet
                            Intent intent = new Intent(GreetingActivity.this, VideoChatActivity.class);
                            startActivity(intent.putExtras(info));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findViewById(R.id.call_button).setEnabled(true);
                    initPubNub();
                } else {
                    findViewById(R.id.call_button).setEnabled(false);
                    Toast.makeText(GreetingActivity.this, "Please enable camera access to start video-chat!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
