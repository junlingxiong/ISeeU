package com.example.junling.iseeu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An interface that listens for incoming calls or places outgoing video calls
 */
public class GreetingActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    private Pubnub mPubNub;
    private String username;
    private EditText mCallNumET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        this.mCallNumET  = (EditText) findViewById(R.id.call_num); // TODO: the number to call
        this.username = "xioz"; // TODO: user name upon registration

        initPubNub();
    }

    /**
     * This function subscribes you to the username's standby channel.
     * When it receives a message, it pulls out the JSON_CALL_USER field, call_user.
     *
     * In this demo, we will create a video that simply requires you pass it your user_name in the intent.
     * If you also provide the intent with a JSON_CALL_USER, it will try to auto-connect you to that user.
     * You can see that we send the user to VideoChatActivity
     */
    private void initPubNub() { // handles receiving calls
        String stdbyChannel = this.username + Constants.STDBY_SUFFIX;
        this.mPubNub = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.username);
        try {
            this.mPubNub.subscribe(stdbyChannel, new Callback() { // subscribe to incoming calls with a callback
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-success", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(Constants.JSON_CALL_USER)) return;
                        String user = jsonMsg.getString(Constants.JSON_CALL_USER);
                        // Consider Accept/Reject call here
                        Intent intent = new Intent(GreetingActivity.this, VideoChatActivity.class);
                        intent.putExtra(Constants.USER_NAME, username);
                        intent.putExtra(Constants.JSON_CALL_USER, user);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * an EditText and a Button in your activity.
     * When we push the button, we will invoke the function @makeCall
     * which will send a JSON message to the other user to show we would like to chat.
     * @param view
     */
    public void makeCall(View view){ // check validity of the call number
        String callNum = mCallNumET.getText().toString();
        if (callNum.isEmpty() || callNum.equals(this.username)) {
            Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_SHORT).show();
        }
        dispatchCall(callNum);
    }

    /**
     * This sends a JSONObject to the other user's standby channel with the information {"call_user":"<YourUsername>"}.
     * We then send the user to the VideoChatActivity.
     *
     * This will trigger the other user's PubNub callback so both users will be in VideoChatActivity calling each other.
     * This is alright since the PnWebRTC API does not allow duplicate calls, so only the first SDP to be received will be used.
     * @param callNum
     */
    public void dispatchCall(final String callNum) {
        final String callNumStdBy = callNum + Constants.STDBY_SUFFIX;
        JSONObject jsonCall = new JSONObject();
        try {
            jsonCall.put(Constants.JSON_CALL_USER, this.username);
            mPubNub.publish(callNumStdBy, jsonCall, new Callback() { // publish an outgoing call with a callback
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-dCall", "SUCCESS: " + message.toString());
                    Intent intent = new Intent(GreetingActivity.this, VideoChatActivity.class);
                    intent.putExtra(Constants.USER_NAME, username);
                    intent.putExtra(Constants.CALL_USER, callNum);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
