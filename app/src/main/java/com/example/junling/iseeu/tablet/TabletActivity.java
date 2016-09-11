package com.example.junling.iseeu.tablet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.junling.iseeu.R;

public class TabletActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);
    }


    /** Called when the user clicks the register button */
    public void register(View view) {
        Log.e(LOG, "register(): transitioning into register device");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
