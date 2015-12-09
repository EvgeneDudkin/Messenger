package com.example.messengerpigeon.Navigation_resource;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerpigeon.R;

import java.util.List;

public class NavigationListAdapter extends ArrayAdapter<Navigation_Item> {
    Context context;
    int resLayout;
    List<Navigation_Item> listNavItems;
    TextView tvTitle;

    public NavigationListAdapter(Context context, int resLayout, List<Navigation_Item> listNavItems) {
        super(context, resLayout, listNavItems);

        this.context = context;
        this.listNavItems = listNavItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 1 || position == 2 || position==4) {
            System.out.println("123");
            this.resLayout = R.layout.small_item;
        } else {
            this.resLayout = R.layout.item_navigation_list;
        }

        View v = View.inflate(context, resLayout, null);
        Navigation_Item navItem = listNavItems.get(position);

        if (resLayout == R.layout.small_item) {
            tvTitle = (TextView) v.findViewById(R.id.title_mini);
            tvTitle.setText(navItem.getTitle());
        } else {
            tvTitle = (TextView) v.findViewById(R.id.title);
            ImageView navIcon = (ImageView) v.findViewById(R.id.imageIcon);
            tvTitle.setText(navItem.getTitle());
            navIcon.setImageResource(navItem.getResIcon());
        }


        return v;
    }
}
