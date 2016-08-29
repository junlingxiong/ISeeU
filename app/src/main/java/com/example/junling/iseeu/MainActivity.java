package com.example.junling.iseeu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

import com.example.junling.iseeu.mobile.MobileActivity;
import com.example.junling.iseeu.tablet.TabletActivity;
import com.example.junling.iseeu.util.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private final String LOG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        //dbHelper.onCreate(dbHelper.getWritableDatabase());
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);

        // TODO: check hardware features: camera and auto-focus
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Gateway to tablet version of the application
     */
    public void tVersion(View view) {
        Intent intent = new Intent(this, TabletActivity.class);
        startActivity(intent);
    }

    /**
     * Gateway to mobile version of the application
     */
    public void mVersion(View view) {
        Intent intent = new Intent(this, MobileActivity.class);
        startActivity(intent);
    }
}
