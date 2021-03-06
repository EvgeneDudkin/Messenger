package com.example.messengerpigeon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.messengerpigeon.Fragments.fragment_list_inbox_requests;
import com.example.messengerpigeon.Fragments.fragment_list_outbox_requests;
import com.example.messengerpigeon.Fragments.fragment_pr_mess_1;
import com.example.messengerpigeon.Fragments.fragment_pr_mess_2;
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
    public static int i = 0;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.app_name);
        /*toolbar.inflateMenu(R.menu.menu);*/
    }

    private void initNavigation() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        listViewNavigation = (ListView) findViewById(R.id.navigation_list);

        listNavigationItems = new ArrayList<Navigation_Item>();
        listNavigationItems.add(new Navigation_Item("Друзья", R.mipmap.ic_account_multiple));
        listNavigationItems.add(new Navigation_Item("Исходящие запросы", R.mipmap.ic_account_multiple));
        listNavigationItems.add(new Navigation_Item("Входящие запросы", R.mipmap.ic_account_multiple));
        listNavigationItems.add(new Navigation_Item("Диалоги", R.mipmap.ic_message));
        listNavigationItems.add(new Navigation_Item("Защищенные диалоги", R.mipmap.ic_message));
        listNavigationItems.add(new Navigation_Item("Запросы", R.mipmap.ic_message));
        listNavigationItems.add(new Navigation_Item("Настройки", R.mipmap.ic_settings));

        NavigationListAdapter navigationListAdapter = new NavigationListAdapter(getApplicationContext(),
                R.layout.item_navigation_list, listNavigationItems);
        listViewNavigation.setAdapter(navigationListAdapter);

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new fragments_navigation_item_friends());
        listFragments.add(new fragment_list_outbox_requests());
        listFragments.add(new fragment_list_inbox_requests());
        listFragments.add(new fragments_navigation_item_messages());
        listFragments.add(new fragment_pr_mess_1());
        listFragments.add(new fragment_pr_mess_2());
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

    @Override
    public void onBackPressed() {

        switch (i) {
            case 0:
                openQuitDialog();
                break;
            case 1:
                fragments_navigation_item_friends.friends_backButtonWasPressed();
                break;
            case 2:
                fragments_navigation_item_messages.messages_backButtonWasPressed();
                break;
            case 3:
                fragment_list_inbox_requests.in_backButtonWasPressed();
                break;
            case 4:
                fragment_list_outbox_requests.out_backButtonWasPressed();
                break;
            case 5:
                fragment_pr_mess_1.protected_mess_backButtonWasPressed();
                break;
        }
    }

    private  void openQuitDialog() {
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                Activity_Navigation.this);
        quitDialog.setTitle("Закрыть приложение?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                System.exit(0);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quitDialog.getContext();
            }
        });

        quitDialog.show();
    }
}
