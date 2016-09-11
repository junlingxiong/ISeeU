package com.example.junling.iseeu.tablet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.VideoChatActivity;
import com.example.junling.iseeu.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONObject;

import me.kevingleason.pnwebrtc.PnPeerConnectionClient;

/**
 * An interface for receiving incoming calls
 *
 * Tablet
 */
public class IncomingCallActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private String mDeviceName; // tablet device name
    private String mCaller; // mobile caller name
    private Pubnub mPubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        SharedPreferences sharedPref = getSharedPreferences(Constants.TABLET_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mDeviceName = sharedPref.getString(Constants.KEY_DEVICE_NAME, "Unspecified");

        mCaller = getIntent().getStringExtra(Constants.KEY_CALLER_NAME); // returns null if caller is not specified
        ((TextView) findViewById(R.id.caller_text)).setText("Incoming Call \n from " + mCaller);

        // initialise pubnub
        mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        mPubNub.setUUID(mDeviceName);
    }

    public void acceptCall(View view){
        Intent intent = new Intent(this, VideoChatActivity.class);
        intent.putExtra(Constants.JSON_USER_NAME, mDeviceName); // user name: device name (tablet)
        intent.putExtra(Constants.JSON_CALL_USER, mCaller); // call user: caller name (mobile)
        startActivity(intent);
        finish();
        Log.e(LOG, "acceptCall(): call accepted, transitioning to video call");
    }

    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    public void rejectCall(View view){
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.mDeviceName);
        this.mPubNub.publish(this.mCaller, hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                finish(); // goes back to previous activity, whichever that may be
                Log.e(LOG, "rejectCall.successCallback(): rejected call successfully, transitioning back to privacy mode");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_incoming_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
