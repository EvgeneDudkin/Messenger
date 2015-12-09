package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.friendsRequest;
import com.example.messengerpigeon.jsonServerRequests.searchFriendRequest;
import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.serverInfo;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class fragments_navigation_item_friends extends Fragment {

    ListView listViewFriends;
    List<Messages_Item> listFriendsItem;
    private Button button_search;
    EditText tt;
    authRequest authReq = new authRequest();
    boolean true_false = true;
    List<Fragment> listFragmentFriends;
    friend[] listFriends;
    private friendsRequest friendsReq = null;
    private searchFriendRequest searchFriends = null;
    int list_friends=0;
    private static FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends,
                container, false);
        Activity_Navigation.i=0;
        listViewFriends = (ListView) v.findViewById(R.id.list_friends);

        button_search = (Button) v.findViewById(R.id.button_search);
        button_search.setOnClickListener(onClickListenermain);
        tt = (EditText) v.findViewById(R.id.editText_search);
        AuthTask at = new AuthTask();
        list_friends=0;
        at.execute("list");
        return v;
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_search:
                    AuthTask at = new AuthTask();
                    System.out.println(tt.getText().toString());
                    list_friends=2;
                    at.execute("search", authReq.getToken(), tt.getText().toString());
                    tt.setText("");
            }
        }
    };
    private void initListView(){
        listFriends = friendsReq.getListFriends();

        int l = listFriends.length;
        listFriendsItem = new ArrayList<Messages_Item>();
        if (l == 0) {
            true_false = false;
            listFriendsItem.add(new Messages_Item("У вас нет друзей", R.mipmap.ic_account, ""));

        } else {
            true_false=true;
            for (int i = 0; i < l; i++) {
                listFriendsItem.add(new Messages_Item(listFriends[i].FirstName + " " + listFriends[i].LastName, R.mipmap.ic_account, listFriends[i].Login));
            }
        }
        MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(), R.layout.item_messages, listFriendsItem);
        listViewFriends.setAdapter(messagesListAdapter);
        if (l != 0) {

            listFragmentFriends = new ArrayList<Fragment>();
            for (int i = 0; i < l; i++) {
                listFragmentFriends.add(new fragment_profile(listFriends[i],list_friends));
            }

            listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (true_false) {
                        Activity_Navigation.i=1;
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentFriends.get(position)).addToBackStack("1").commit();
                        Activity_Navigation.toolbar.setTitle(listFriends[position].Login);
                    }
                }
            });
        }

    }
    public static  void friends_backButtonWasPressed() {
        Activity_Navigation.i=0;
        fragmentManager.popBackStack();
    }

    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                friendsReq = new friendsRequest();
                searchFriends = new searchFriendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                friendsReq = new friendsRequest();
                System.out.println(ret);
                friendsReq.responseHandler(ret);
                socket.close();
                initListView();


            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                if (data[0].equals("list")) {
                    friendsReq = new friendsRequest();
                    friendsReq.createRequest("0");
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(friendsReq.get_Request());
                } else if (data[0].equals("search")) {
                    searchFriends.createRequest(data[1], data[2]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(searchFriends.get_Request());
                } else
                    return sendAndListen("");
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {

                /*
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.write(text.getBytes(), 0, text.length());
                dos.flush();


                //jsonCrypt.Send(socket, text);

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[555553];
                int s = dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");
                */

                jsonCrypt.Send(socket, text);

                return jsonCrypt.Get(socket);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
    }
}
