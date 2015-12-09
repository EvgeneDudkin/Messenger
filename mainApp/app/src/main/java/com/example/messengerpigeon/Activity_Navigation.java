package com.example.messengerpigeon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.messengerpigeon.Fragments.fragment_list_inbox_requests;
import com.example.messengerpigeon.Fragments.fragment_list_outbox_requests;
import com.example.messengerpigeon.Fragments.fragment_protected_messages;
import com.example.messengerpigeon.Fragments.fragments_navigation_item_friends;
import com.example.messengerpigeon.Fragments.fragments_navigation_item_messages;
import com.example.messengerpigeon.Fragments.fragments_navigation_item_setting;
import com.example.messengerpigeon.Navigation_resource.NavigationListAdapter;
import com.example.messengerpigeon.Navigation_resource.Navigation_Item;
import com.example.messengerpigeon.jsonServerRequests.authRequest;

import java.util.ArrayList;
import java.util.List;

public class Activity_Navigation extends AppCompatActivity {

    public static Toolbar toolbar;
    DrawerLayout drawerLayout;
    RelativeLayout drawerPane;
    ListView listViewNavigation;
    public static FragmentManager fragmentManager;
    List<Navigation_Item> listNavigationItems;
    List<Fragment> listFragments;
    TextView profileLogin;
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initToolbar();
        initNavigation();

        profileLogin=(TextView)findViewById(R.id.profile_login);
        profileName=(TextView)findViewById(R.id.profile_name);
        profileLogin.setText(authRequest.getMyLogin());
        profileName.setText(authRequest.getMyName() + " " + authRequest.getMyLastName());
        System.out.println(authRequest.getMyLastName());
    }
    private void initToolbar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.app_name);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);

    }

    private void initNavigation() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        listViewNavigation = (ListView) findViewById(R.id.navigation_list);

        listNavigationItems = new ArrayList<Navigation_Item>();
        listNavigationItems.add(new Navigation_Item("Friends", R.drawable.friends));
        listNavigationItems.add(new Navigation_Item("по англ?? исх", R.drawable.friends));
        listNavigationItems.add(new Navigation_Item("по англ?? вход", R.drawable.friends));
        listNavigationItems.add(new Navigation_Item("Messages", R.drawable.message));
        listNavigationItems.add(new Navigation_Item("Protected messages", R.drawable.message));
        listNavigationItems.add(new Navigation_Item("Settings", R.drawable.settings));

        NavigationListAdapter navigationListAdapter = new NavigationListAdapter(getApplicationContext(),
                R.layout.item_navigation_list, listNavigationItems);
        listViewNavigation.setAdapter(navigationListAdapter);

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new fragments_navigation_item_friends());
        listFragments.add(new fragment_list_outbox_requests());
        listFragments.add(new fragment_list_inbox_requests());
        listFragments.add(new fragments_navigation_item_messages());
        listFragments.add(new fragment_protected_messages());
        listFragments.add(new fragments_navigation_item_setting());


        //load first fragment as default:
        //FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(3)).commit();

        toolbar.setTitle(listNavigationItems.get(3).getTitle());
        listViewNavigation.setItemChecked(3, true);
        drawerLayout.closeDrawer(drawerPane);

        //set listener for navigation items:
        listViewNavigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(listNavigationItems.get(position).getTitle());
                //FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(position)).commit();

                toolbar.setTitle(listNavigationItems.get(position).getTitle());
                listViewNavigation.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
    }
}
