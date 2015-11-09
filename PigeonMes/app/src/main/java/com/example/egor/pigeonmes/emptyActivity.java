package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.egor.pigeonmes.adapter.TabsPagerFragmentAdapter;

public class emptyActivity extends AppCompatActivity {

    MyTask mt;

    private Toolbar toolbar;
    private  DrawerLayout drawerLayout;
    private ViewPager viewPager;
    public Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex);

        initToolbar();
        initNavigationView();
        initTabs();

        JSONObject query = new JSONObject();
        JSONObject friends = new JSONObject();

        try {
            friends.put("login", "qwerty");
            query.put("friends", friends);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mt = new MyTask();
        mt.execute(query.toString());
    }

    private void initToolbar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.friends:
                        showNotificationTab();
                }
                return true;
            }
        });
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout =(TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private  void showNotificationTab() {
        viewPager.setCurrentItem(Constants.TAB_ONE);
    }

    public class MyTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final String ret) {
            System.out.println(ret);
            //msgOkBox.MsgOkBox(ret, ret, emptyActivity.this);
            JSONObject jo = null;
            String s = "";
            try {
                jo = new JSONObject(ret);
                JSONArray arr = jo.getJSONArray("friends");
                for (int i = 0; i < arr.length(); i++) {
                    s += arr.getString(i) + "\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText(s);
        }

        @Override
        protected String doInBackground(String... jsonStr) {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                return sendAndListen(jsonStr[0]);
            } catch (UnknownHostException e1) {
                return "Error 12";
            } catch (IOException e1) {
                return "Error 13";
            }
        }

        //Отправляем строку и ждем ответа
        public String sendAndListen(String text) {
            try {
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //Блок чтения ответа.
                //TODO: Пошаманить тут
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                int s=dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");

            } catch (EOFException e) {
                e.printStackTrace();
                return "Error 11";
            }catch (Exception e) {
                e.printStackTrace();
                return "Error 10";
            }

        }


    }

}
