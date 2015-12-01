package com.example.messengerpigeon.Fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.miniClasses.dialog;

import java.util.ArrayList;
import java.util.List;

public class fragments_navigation_item_messages extends Fragment {
    TextView text;
    int countDialog;

    public static String TAG="MyLog";
    ListView listViewMessage;
    List<Fragment> listFragmentMess;
    List<Messages_Item> listMessItem;

    authRequest req= new authRequest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_messages, container, false);

        Log.d(TAG,"1");

        dialog[] dialog= req.getDialogs();
        Log.d(TAG,"11");
        countDialog=dialog.length;

        Log.d(TAG,"2");
        listViewMessage = (ListView) v.findViewById(R.id.list_messages);

        Log.d(TAG,"3");
        listMessItem = new ArrayList<Messages_Item>();
        for (int i = 0; i < countDialog; i++) {
            listMessItem.add(new Messages_Item(Integer.toString(dialog[i].Id), R.drawable.account,dialog[i].Name));
        }

        Log.d(TAG,"4");
        MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(),
                R.layout.item_messages, listMessItem);
        listViewMessage.setAdapter(messagesListAdapter);


        Log.d(TAG, "5");

        listFragmentMess = new ArrayList<Fragment>();
        for (int i = 0; i < countDialog; i++) {
            Bundle arg=new Bundle();
            arg.putByte("dialogId", (byte) dialog[i].Id);
            fragments_messages_item it=new fragments_messages_item();
            it.setArguments(arg);
            listFragmentMess.add(it);
        }


        Log.d(TAG,"6");
        listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentMess.get(position)).commit();

            }
        });

        return v;
    }
}
