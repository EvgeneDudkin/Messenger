package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initToolbarmain();
    }

    /*public void initToolbarmain() {
        toolbar = (Toolbar) findViewById(R.id.toolbarmain);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }*/

    public void onAuthClick(View v) {
        // doStuff
        Intent intentApp = new Intent(MainActivity.this,
                authActivity.class);
        MainActivity.this.startActivity(intentApp);
    }

    public void onRegClick(View v) {
        // doStuff
        Intent intentApp = new Intent(MainActivity.this,
                regActivity.class);
        MainActivity.this.startActivity(intentApp);
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

}
