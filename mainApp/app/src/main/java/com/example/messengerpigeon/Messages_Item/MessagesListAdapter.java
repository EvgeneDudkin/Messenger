package com.example.messengerpigeon.Messages_Item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerpigeon.R;

import java.util.List;

/**
 * Created by Пользователь on 20.11.2015.
 */
public class MessagesListAdapter extends ArrayAdapter<Messages_Item> {
    Context context;
    int resLayout;
    List<Messages_Item> listMessItems;

    public MessagesListAdapter(Context context, int resLayout, List<Messages_Item> listMessItems) {
        super(context, resLayout, listMessItems);

        this.context=context;
        this.resLayout=resLayout;
        this.listMessItems=listMessItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context,resLayout,null);

        TextView tvTitle = (TextView) v.findViewById(R.id.text_friend_name);
        ImageView navIcon = (ImageView) v.findViewById(R.id.image_friend_mess);
        TextView lsMess=(TextView)v.findViewById(R.id.text_last_message);

        Messages_Item navItem = listMessItems.get(position);

        tvTitle.setText(navItem.getNameFriend());
        navIcon.setImageResource(navItem.getResIcon());
        lsMess.setText(navItem.getMessDialog());

        return v;
    }
}
