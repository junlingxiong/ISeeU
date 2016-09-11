package com.example.junling.iseeu.util;

/**
 * Created by Chuck on 25/8/16.
 */
public class Constants {

    public static final String STDBY_SUFFIX = "-stdby"; // reserving a suffix like -stdby from your users, and using it as a standby channel
    public static final String PUB_KEY = "pub-c-cae83f92-606f-4f28-bfc9-0f30f4d2a9fb"; // Use Your Pub Key
    public static final String SUB_KEY = "sub-c-7f2d25ce-6a8a-11e6-80e7-02ee2ddab7fe"; // Use Your Sub Key
    // will be used as a key when we place or receive calls
    public static final String JSON_USER_NAME = "user_name"; // callee
    public static final String JSON_CALL_USER = "call_user"; // caller
    public static final String JSON_CALL_TIME = "call_time";
    public static final String JSON_OCCUPANCY = "occupancy";
    public static final String JSON_STATUS    = "status";

    public static final String STATUS_AVAILABLE = "Available"; // privacy mode off
    public static final String STATUS_OFFLINE   = "Offline"; // unsubscribed
    public static final String STATUS_BUSY      = "Busy"; // privacy mode on

    public static final int REQUEST_CAMERA = 1;

    public static final String KEY_DEVICE_NAME = "KEY_DEVICE_NAME";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_CALLER_NAME = "KEY_CALLER_NAME";
    public static final String KEY_USER_NAME = "KEY_USER_NAME";
    public static final String KEY_CALL_USER = "KEY_CALL_USER"; // If Constants.CALL_USER is in the intent extras, auto-connect them.


    public static final String MOBILE_SHARED_PREFERENCES = "MOBILE_SHARED_PREFERENCES";
    public static final String TABLET_SHARED_PREFERENCES = "TABLET_SHARED_PREFERENCES";


}
