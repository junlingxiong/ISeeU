package com.example.junling.iseeu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TabletWelcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_welcome);
    }


    /** Called when the user clicks the register button */
    public void register(View view) {
        Intent intent = new Intent(this, TabletMain.class);
        startActivity(intent);
    }
}
