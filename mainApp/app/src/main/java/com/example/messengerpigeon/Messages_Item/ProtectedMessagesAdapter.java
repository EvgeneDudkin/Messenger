package com.example.messengerpigeon.Messages_Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.messengerpigeon.R;

import java.util.List;

/**
 * Created by Пользователь on 09.12.2015.
 */
public class ProtectedMessagesAdapter extends ArrayAdapter<ProtectedMessagesItem> {
    Context context;
    int resLayout;
    List<ProtectedMessagesItem> listMessItems;

    public ProtectedMessagesAdapter(Context context, int resLayout, List<ProtectedMessagesItem> listMessItems) {
        super(context, resLayout, listMessItems);

        this.context=context;
        this.resLayout=resLayout;
        this.listMessItems=listMessItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View v = View.inflate(context,resLayout,null);

        convertView = LayoutInflater.from(context).inflate(resLayout, null);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout_pr);

        TextView tvTitle = (TextView) v.findViewById(R.id.text_friend_name);
        ImageView navIcon = (ImageView) v.findViewById(R.id.image_friend_mess);
        TextView lsMess=(TextView)v.findViewById(R.id.text_last_message);

        ProtectedMessagesItem navItem = listMessItems.get(position);

        tvTitle.setText(navItem.getNameFriend());
        navIcon.setImageResource(navItem.getResIcon());
        lsMess.setText(navItem.getMessDialog());

        if(navItem.getStatus().equals("true")){
            layout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_light));
        }

        return convertView;
    }
}
