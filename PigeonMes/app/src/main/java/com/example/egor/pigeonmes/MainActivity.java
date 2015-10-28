package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {


    public void onAuthClick(View v) {

        // doStuff
        Intent intentApp = new Intent(MainActivity.this,
                AuthorizationActivity.class);

        MainActivity.this.startActivity(intentApp);


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
