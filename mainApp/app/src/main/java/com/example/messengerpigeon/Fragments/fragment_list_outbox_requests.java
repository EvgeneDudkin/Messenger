package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.friendsRequest;
import com.example.messengerpigeon.jsonServerRequests.searchFriendRequest;
import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.serverInfo;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Пользователь on 07.12.2015.
 */
public class fragment_list_outbox_requests extends Fragment {

    private static final int LAYOUT = R.layout.fragment_view_pager_list_out_req;
    ListView listViewFriends;
    friendsRequest friendsReq = null;
    List<Messages_Item> listFriendsItem;
    List<Fragment> listFragmentFriends;
    public friend[] listFriends;
    private View view;
    boolean true_false = true;
    private static FragmentManager fragmentManager;

    public static fragment_list_outbox_requests getInstance() {
        Bundle args = new Bundle();
        fragment_list_outbox_requests fragment = new fragment_list_outbox_requests();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        Activity_Navigation.i=0;
        listViewFriends = (ListView) view.findViewById(R.id.list_out_req);
        AuthTask at = new AuthTask();
        at.execute();

        return view;
    }

    private void initListView() {
        listFriends = friendsReq.getListFriends();

        int l = listFriends.length;
        listFriendsItem = new ArrayList<Messages_Item>();
        if (l == 0) {
            true_false = false;
            listFriendsItem.add(new Messages_Item("Нет исходящих запросов", R.mipmap.ic_account, ""));
        } else {
            for (int i = 0; i < l; i++) {
                listFriendsItem.add(new Messages_Item(listFriends[i].FirstName + " " + listFriends[i].LastName, R.mipmap.ic_account, listFriends[i].Login));
            }
        }
        MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(), R.layout.item_messages, listFriendsItem);
        listViewFriends.setAdapter(messagesListAdapter);

        if (l != 0) {

            listFragmentFriends = new ArrayList<Fragment>();
            for (int i = 0; i < l; i++) {
                listFragmentFriends.add(new fragment_profile(listFriends[i],1));
            }

            listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (true_false) {
                        Activity_Navigation.i=4;
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentFriends.get(position)).addToBackStack("4").commit();
                        Activity_Navigation.toolbar.setTitle(listFriends[position].Login);
                    }
                }
            });
        }
    }
    public static  void out_backButtonWasPressed() {
        Activity_Navigation.i=0;
        Activity_Navigation.toolbar.setTitle("Исходящие запросы");
        fragmentManager.popBackStack();
    }

    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;

        private searchFriendRequest searchFriends = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                friendsReq = new friendsRequest();
                searchFriends = new searchFriendRequest();

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("4");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                socket.close();
                friendsReq = new friendsRequest();
                friendsReq.responseHandler(ret);
                System.out.println("q1");
                System.out.println("q2");
                initListView();

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                friendsReq = new friendsRequest();
                friendsReq.createRequest("1");
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                return sendAndListen(friendsReq.get_Request());

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {
                /*
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
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
