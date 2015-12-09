package com.example.messengerpigeon.History;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;

import java.util.List;

/**
 * Created by Пользователь on 25.11.2015.
 */
public class HistoryListAdapter extends ArrayAdapter<History_Item> {
    Context context;
    int resLayout;
    List<History_Item> listHistItems;
    String str;
    authRequest authReq=new authRequest();
    public HistoryListAdapter(Context context, int idl, List<History_Item> history_items) {
        super(context, idl, history_items);

        this.context=context;
        this.listHistItems=history_items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tvTitle;
        TextView lsMess;
        String login=authReq.getMyLogin();
        str=listHistItems.get(position).getName_sender();
        if(str.equals(login))
        {
            resLayout=R.layout.item_mess_me;
        }
        else
        {
            resLayout=R.layout.item_mess_other;
        }

        View v = View.inflate(context,resLayout,null);

        if(resLayout==R.layout.item_mess_me){
            tvTitle = (TextView) v.findViewById(R.id.text_me_mess);
            lsMess=(TextView)v.findViewById(R.id.text_me_time);
        }
        else{
            tvTitle = (TextView) v.findViewById(R.id.text_other_mess);
            lsMess=(TextView)v.findViewById(R.id.text_other_time_mess);
        }
        History_Item navItem = listHistItems.get(position);

        tvTitle.setText(navItem.getMessage_one());
        lsMess.setText(navItem.getTime_send());

        return v;
    }
}
