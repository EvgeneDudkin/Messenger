package com.example.messengerpigeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.messengerpigeon.Activity_entry_and_reg.Activity_entry;
import com.example.messengerpigeon.Activity_entry_and_reg.Activity_reg;

public class MainActivity extends AppCompatActivity {
    private Button button_entry;
    private Button button_reg;

    public static SharedPreferences mSettings;
    public static final String APP_PREFERENCES_PUBLIK_KEY = "publicKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__main);

        button_entry=(Button)findViewById(R.id.button_entry_main);
        button_reg=(Button)findViewById(R.id.button_reg_main);

        button_entry.setOnClickListener(onClickListenermain);
        button_reg.setOnClickListener(onClickListenermain);

        mSettings = getSharedPreferences(APP_PREFERENCES_PUBLIK_KEY, Context.MODE_PRIVATE);
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_entry_main:
                    Intent intentEntry = new Intent(MainActivity.this, Activity_entry.class);
                    MainActivity.this.startActivity(intentEntry);
                    break;
                case R.id.button_reg_main:
                    Intent intentReg = new Intent(MainActivity.this, Activity_reg.class);
                    MainActivity.this.startActivity(intentReg);
            }
        }
    };

}
