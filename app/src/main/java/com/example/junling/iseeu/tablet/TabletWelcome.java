package com.example.junling.iseeu.tablet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.junling.iseeu.R;

public class TabletWelcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_welcome);
    }


    /** Called when the user clicks the register button */
    public void register(View view) {
        Intent intent = new Intent(this, TabletActivity.class);
        startActivity(intent);
    }
}
