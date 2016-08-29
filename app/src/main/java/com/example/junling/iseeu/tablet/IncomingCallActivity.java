package com.example.junling.iseeu.tablet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junling.iseeu.R;
import com.example.junling.iseeu.mobile.VideoChatActivity;
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

    private String mCallee;
    private String mCaller;

    private Pubnub mPubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        Bundle extras = getIntent().getExtras();
        if (extras==null || !extras.containsKey(Constants.JSON_CALL_USER)){
            Intent intent = new Intent(this, PrivacyModeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Need to pass mCallee to IncomingCallActivity in intent extras (Constants.JSON_CALL_USER).",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.mCallee = extras.getString(Constants.JSON_USER_NAME);
        this.mCaller = extras.getString(Constants.JSON_CALL_USER, "Unknown");
        ((TextView) findViewById(R.id.caller_text)).setText("Incoming Video Call \n from " + mCaller);

        // initialise pubnub
        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.mCallee);
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

    public void acceptCall(View view){
        Bundle info = new Bundle();
        info.putString(Constants.JSON_USER_NAME, mCallee); // callee
        info.putString(Constants.JSON_CALL_USER, mCaller); // caller
        Intent intent = new Intent(IncomingCallActivity.this, VideoChatActivity.class);
        startActivity(intent.putExtras(info));
        IncomingCallActivity.this.finish();
    }

    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    public void rejectCall(View view){
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.mCallee);
        this.mPubNub.publish(this.mCaller, hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Bundle info = new Bundle();
                info.putString(Constants.JSON_USER_NAME, mCallee); // callee
                info.putString(Constants.JSON_CALL_USER, mCaller); // caller
                Intent intent = new Intent(IncomingCallActivity.this, PrivacyModeActivity.class);
                startActivity(intent.putExtras(info));
                IncomingCallActivity.this.finish();
            }
        });
    }


}
