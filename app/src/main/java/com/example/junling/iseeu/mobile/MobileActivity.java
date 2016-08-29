package com.example.junling.iseeu.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.junling.iseeu.R;

public class MobileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
    }

    /** Called when the user clicks the login button */
    public void login(View view) {
        Intent intent = new Intent(MobileActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the register button */
    public void register(View view) {
        Intent intent = new Intent(MobileActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

}
