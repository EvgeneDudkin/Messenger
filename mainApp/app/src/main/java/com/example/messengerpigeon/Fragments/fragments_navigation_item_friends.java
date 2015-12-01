package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.friendsRequest;
import com.example.messengerpigeon.jsonServerRequests.searchFriendRequest;
import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.serverInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class fragments_navigation_item_friends extends Fragment{

    ListView listViewFriends;
    List<Messages_Item> listFriendsItem;
    private Button button_search;
    EditText tt ;
    authRequest authReq= new authRequest();
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_friends,
                    container, false);
            listViewFriends=(ListView)v.findViewById(R.id.list_friends);

            button_search=(Button)v.findViewById(R.id.button_search);
            button_search.setOnClickListener(onClickListenermain);
            tt=(EditText)v.findViewById(R.id.editText_search);
            AuthTask at = new AuthTask();
            at.execute("list",authReq.getToken(), "1","20");
            return v;
        }
    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_search:
                    AuthTask at = new AuthTask();

                    System.out.println(tt.getText().toString());
                    at.execute("search", authReq.getToken(), tt.getText().toString());
                    tt.setText("");
            }
        }
    };
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private friendsRequest friendsReq=null;
        private searchFriendRequest searchFriends = null;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            try{
                friendsReq=new friendsRequest();
                searchFriends = new searchFriendRequest();
            }
            catch (Exception e){
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

                friend[] listFriends=friendsReq.getListFriends();
                int l=listFriends.length;
                listFriendsItem=new ArrayList<Messages_Item>();
                if (l == 0){
                    listFriendsItem.add(new Messages_Item("Поиск не дал результатов",R.drawable.account,""));

                }
                else {
                    for (int i = 0; i < l; i++) {
                        listFriendsItem.add(new Messages_Item(listFriends[i].FirstName + " " + listFriends[i].LastName, R.drawable.account, listFriends[i].Login));
                    }
                }
                MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(), R.layout.item_messages, listFriendsItem);
                listViewFriends.setAdapter(messagesListAdapter);
                socket.close();

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                if(data[0].equals("list")) {
                    friendsReq = new friendsRequest();
                    friendsReq.createRequest();
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(friendsReq.get_Request());
                }
                else
                if(data[0].equals("search")) {
                    searchFriends.createRequest(data[1],data[2]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(searchFriends.get_Request());
                }
                else
                    return sendAndListen("");
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[555553];
                int s=dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
    }
}
