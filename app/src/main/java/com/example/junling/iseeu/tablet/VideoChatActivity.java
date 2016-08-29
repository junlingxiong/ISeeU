package com.example.junling.iseeu.tablet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junling.iseeu.MainActivity;
import com.example.junling.iseeu.R;
import com.example.junling.iseeu.mobile.GreetingActivity;
import com.example.junling.iseeu.util.Constants;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import me.kevingleason.pnwebrtc.PnPeer;
import me.kevingleason.pnwebrtc.PnRTCClient;
import me.kevingleason.pnwebrtc.PnRTCListener;

/**
 * An interface for ongoing video-chat
 *
 * Mobile and Tablet
 */
public class VideoChatActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();
    // arbitrary tags used to identify tracks and streams
    public static final String VIDEO_TRACK_ID = "videoPN";
    public static final String AUDIO_TRACK_ID = "audioPN";
    public static final String LOCAL_MEDIA_STREAM_ID = "localStreamPN";

    private PeerConnectionFactory pcFactory;
    private PnRTCClient pnRTCClient; // PnWebRTC client which will handle all signaling for you
    private VideoSource localVideoSource; // WebRTC wrapper around the Android Camera API to handle local video
    // used to render media streams to the GLSurfaceView
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private MediaStream mLocalMediaStream;
    // Graphics Library Surface View, made to have content rendered to it
    private GLSurfaceView mVideoView;

    private String mUsername;
    private String mPassword; // TODO: assign value to password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        initialisePnRTCClient(); // 1
        gatherVideoAndAudioResources(); // 2
        setUpCall(); // 3. signaling
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mVideoView.onPause();
        this.localVideoSource.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mVideoView.onResume();
        this.localVideoSource.restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.localVideoSource != null) {
            this.localVideoSource.stop();
        }
        if (this.pnRTCClient != null) {
            this.pnRTCClient.onDestroy(); // handle connection cleanup
        }
    }

    private void initialisePnRTCClient() {
        // retrieve user name passed in from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(Constants.JSON_USER_NAME)) { // send a user back to MainActivity if they did not attach a mUsername to the intent
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Need to pass mUsername to VideoChatActivity in intent extras (Constants.JSON_USER_NAME).",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.mUsername = extras.getString(Constants.JSON_USER_NAME, "");
        // These globals effect the PnPeerConnectionClient as well, so set them before instantiating your PnWebRTCClient.
        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        this.pcFactory = new PeerConnectionFactory();

        // PnWebRTCClient contains everything you will need to develop video chat applications.
        // This class has all the functions for signaling with WebRTC protocols,
        // including SDP Offer Options known as MediaConstraints, default MediaConstraint is used when not specified
        this.pnRTCClient = new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, this.mUsername);
        // Currently, the PnRTCClient has default video, audio, and PeerConnection configurations. No need to customize them for this app. However, if you wish to in the future, the README of the PnWebRTC Repo has some useful information on the Client
    }

    /**
     * The end goal of capturing Video and Audio sources locally, is to create and attach them to a MediaStream.
     * We then attach this MediaStream to any outgoing PeerConnections that we create.
     * That is how video and audio are streamed from peer to peer with WebRTC
     */
    private void gatherVideoAndAudioResources() {
        // Returns the number of cams & front/back face device name
        int camNumber = VideoCapturerAndroid.getDeviceCount();
        String frontFacingCam = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        String backFacingCam  = VideoCapturerAndroid.getNameOfBackFacingDevice();

        // Creates a VideoCapturerAndroid instance for the device name using the FRONT facing camera
        VideoCapturer capturer = VideoCapturerAndroid.create(frontFacingCam);

        // First create a Video Source, then we can make a Video Track
        localVideoSource = pcFactory.createVideoSource(capturer, this.pnRTCClient.videoConstraints());
        VideoTrack localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);

        // First we create an AudioSource then we can create our AudioTrack
        AudioSource audioSource = pcFactory.createAudioSource(this.pnRTCClient.audioConstraints());
        AudioTrack localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        // To create your own constraints:
//        MediaConstraints videoConstraints = new MediaConstraints();
//        videoConstraints.mandatory.add(
//                new MediaConstraints.KeyValuePair("maxWidth", "1280"));
//        ...
        // PnSignalingParams holds all the constraints for a PeerConnection, Video, and Audio, as well as the list of ICE Servers.
//        PnSignalingParams params = new PnSignalingParams(pcConstraints, videoConstraints, audioConstraints);
//        pnRTCClient.setSignalParams(params);

        mLocalMediaStream = pcFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        // Now we can add our tracks.
        mLocalMediaStream.addTrack(localVideoTrack);
        mLocalMediaStream.addTrack(localAudioTrack);
        //The MediaStream object is now ready to be shared.
        // The last step before we start signaling and opening video chats is to set up our GLSurfaceView and renderers.
        // Then we set that view, and pass a Runnable to run once the surface is ready
        mVideoView = (GLSurfaceView) findViewById(R.id.gl_surface);
        VideoRendererGui.setView(mVideoView, null);

        // Now that VideoRendererGui is ready, we can get our VideoRenderer.
        // IN THIS ORDER. Effects which is on top or bottom
        // VideoRendererGui.create(x, y, width, height, ScaleType, mirror?)
        // Here x and y are starting position with (0,0) being the top left.
        // The width and height are percentages of the GLSurfaceView.
        // For ScaleType I used SCALE_ASPECT_FILL, and I only chose to mirror (flip) the local stream.
        remoteRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        localRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
        // The only thing left to do now is set up the PnWebRTC Signaling.
    }

    /**
     *
     */
    private void setUpCall() {
        // First attach the RTC Listener so that callback events will be triggered
        this.pnRTCClient.attachRTCListener(new MyRTCListener());
        this.pnRTCClient.attachLocalMediaStream(mLocalMediaStream); // will trigger our onLocalStream() callback

        // Listen on a channel. This is your "phone number," also set the max chat users.
        this.pnRTCClient.listenOn(this.mUsername); // begin to listen for calls on our mUsername
        this.pnRTCClient.setMaxConnections(1);

        // If Constants.JSON_CALL_USER is in the intent extras, auto-connect them.
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(Constants.JSON_CALL_USER)) {
             String callNum = extras.getString(Constants.JSON_CALL_USER, "");
            connectToUser(callNum);
        }
    }

    public void connectToUser(String user) {
        this.pnRTCClient.connect(user);
    }

    public void hangup(View view) {
        this.pnRTCClient.closeAllConnections();
        Bundle info = new Bundle();
        info.putString(Constants.KEY_DEVICE_NAME, mUsername);
        info.putString(Constants.KEY_PASSWORD, mPassword);
        startActivity(new Intent(VideoChatActivity.this, PrivacyModeActivity.class).putExtras(info));
    }


    /**
     * Signaling relies almost entirely callbacks, so take a moment and read about all the callbacks offered by PnWebRTC.
     * Your app's functionality relies on your implementation of a PnRTCListener. Take a moment to think about app design and how you should use these callbacks.
     *
     * PnRTCListener is an abstract class that should be extended to implement all desired WebRTC callbacks.
     * This is what connects and powers your application.
     *
     * The best way of extending this is using a private class nested in your video activity that extends PnRTCListener
     * and implements the callbacks as you app requires.
     */
    private class MyRTCListener extends PnRTCListener {
        @Override
        public void onLocalStream(final MediaStream localStream) { // We simply attach a local renderer to our local stream's VideoTrack.
            VideoChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
                }
            });
        }

        @Override // connected
        public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) { // if we receive a PeerConnection that has a peer's MediaStream attached to it, we probably want to display it fullscreen
            super.onAddRemoteStream(remoteStream, peer);
            Log.e(LOG, "onAddRemoteStream(): connected!");
            VideoChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(remoteStream.videoTracks.size()==0) return;
                        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
                        // update sizes of the renderers. This will display the remote user fullscreen and a mirrored image of your stream in the bottom right of the GL Surface.
                        VideoRendererGui.update(remoteRender, 0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
                        VideoRendererGui.update(localRender, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
                        ((TextView) findViewById(R.id.status_text)).setText("Connected");
                        ((Button) findViewById(R.id.hangup_button)).setText("Hang Up");
                    }
                    catch (Exception e){ e.printStackTrace(); }
                }
            });
        }

        @Override
        public void onMessage(PnPeer peer, Object message) {
            /// Handle Message
        }

        @Override // hangup
        public void onPeerConnectionClosed(PnPeer peer) {
            super.onPeerConnectionClosed(peer);
            // Quit back to GreetingActivity
            Bundle info = new Bundle();
            info.putString(Constants.KEY_DEVICE_NAME, mUsername);
            info.putString(Constants.KEY_PASSWORD, mPassword);
            Intent intent = new Intent(VideoChatActivity.this, PrivacyModeActivity.class);
            startActivity(intent.putExtras(info));
            finish();
        }
    }
}

