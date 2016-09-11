package com.example.junling.iseeu.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.junling.iseeu.R;

public class MobileActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
    }

    public void login(View view) {
        Intent intent = new Intent(MobileActivity.this, LoginActivity.class);
        startActivity(intent);
        Log.e(LOG, "login(): transitioning to log in");
    }

    public void register(View view) {
        Intent intent = new Intent(MobileActivity.this, RegisterActivity.class);
        startActivity(intent);
        Log.e(LOG, "register(): transitioning to register");

    }

}
