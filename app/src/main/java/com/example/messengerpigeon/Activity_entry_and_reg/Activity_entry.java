package com.example.messengerpigeon.Activity_entry_and_reg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.R;

public class Activity_entry extends AppCompatActivity{

    private Toolbar toolbar;
    private Button button_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        button_entry=(Button)findViewById(R.id.button_entry);
        button_entry.setOnClickListener(onClickListenermain);

        initToolbar();
    }

    private void initToolbar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_entry:
                    Intent intentEntry = new Intent(Activity_entry.this, Activity_Navigation.class);
                    Activity_entry.this.startActivity(intentEntry);
                    break;
            }
        }
    };
}
