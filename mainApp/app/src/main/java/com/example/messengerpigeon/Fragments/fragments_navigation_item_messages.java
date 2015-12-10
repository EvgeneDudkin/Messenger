package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.listDialogsRequest;
import com.example.messengerpigeon.miniClasses.dialog;
import com.example.messengerpigeon.serverInfo;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class fragments_navigation_item_messages extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView text;
    int countDialog;

    public static String TAG = "MyLog";
    ListView listViewMessage;
    List<Fragment> listFragmentMess;
    List<Messages_Item> listMessItem;
    View v;
    private static FragmentManager fragmentManager;

    listDialogsRequest req = new listDialogsRequest();

    private SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_messages, container, false);
        Activity_Navigation.i = 0;
        AuthTask at = new AuthTask();
        at.execute(authRequest.getToken());

        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
        mSwipeLayout.setOnRefreshListener(this);

        return v;
    }

    @Override
    public void onRefresh() {
        Log.d("my_tag", "refresh");
        System.out.println("test");
        listMessItem.clear();
        AuthTask at = new AuthTask();
        at.execute(authRequest.getToken());
        // stop refresh
        mSwipeLayout.setRefreshing(false);
        System.out.println();
    }

    private void initListDialog() {
        final dialog[] dialog = req.getDialogs();
        countDialog = dialog.length;

        listViewMessage = (ListView) v.findViewById(R.id.list_messages);

        listMessItem = new ArrayList<Messages_Item>();
        for (int i = 0; i < countDialog; i++) {
            listMessItem.add(new Messages_Item(dialog[i].getName(), R.mipmap.ic_account, dialog[i].Login));
        }

        MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(),
                R.layout.item_messages, listMessItem);
        listViewMessage.setAdapter(messagesListAdapter);

        listFragmentMess = new ArrayList<Fragment>();
        for (int i = 0; i < countDialog; i++) {
            Bundle arg = new Bundle();
            arg.putByte("dialogId", (byte) dialog[i].Id);
            fragments_messages_item it = new fragments_messages_item();
            it.setArguments(arg);
            listFragmentMess.add(it);
        }

        listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Activity_Navigation.i = 2;
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentMess.get(position)).addToBackStack("2").commit();
                Activity_Navigation.toolbar.setTitle(dialog[position].getName());
            }
        });
    }

    public static void messages_backButtonWasPressed() {
        Activity_Navigation.i = 0;
        Activity_Navigation.toolbar.setTitle("Сообщения");
        fragmentManager.popBackStack();
    }

    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private listDialogsRequest listDialogsRequest = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listDialogsRequest = new listDialogsRequest();
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                socket.close();
                System.out.println(ret);
                listDialogsRequest.responseHandler(ret);
                listDialogsRequest.errorHandler();
                initListDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... data) {
            try {
                listDialogsRequest.createRequest(data[0]);
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                System.out.println(listDialogsRequest.get_Request());
                return sendAndListen(listDialogsRequest.get_Request());
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {

                jsonCrypt.Send(socket, text);

                return jsonCrypt.Get(socket);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }

    }
}
