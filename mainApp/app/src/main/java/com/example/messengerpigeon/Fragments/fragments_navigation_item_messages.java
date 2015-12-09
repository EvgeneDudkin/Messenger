package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragments_navigation_item_messages extends Fragment {
    TextView text;
    int countDialog;

    public static String TAG = "MyLog";
    ListView listViewMessage;
    List<Fragment> listFragmentMess;
    List<Messages_Item> listMessItem;
    View v;

    listDialogsRequest req = new listDialogsRequest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_messages, container, false);

        AuthTask at = new AuthTask();
        at.execute(authRequest.getToken());


        return v;
    }

    private void initListDialog() {
        final dialog[] dialog = req.getDialogs();
        countDialog = dialog.length;

        listViewMessage = (ListView) v.findViewById(R.id.list_messages);

        listMessItem = new ArrayList<Messages_Item>();
        Date tmp = new Date();
        for (int i = 0; i < countDialog; i++) {
            if (dialog[i].DateTime.getDate() < tmp.getDate())
                listMessItem.add(new Messages_Item(dialog[i].getName(), R.drawable.account, dialog[i].DateTime.getDay() +    dialog[i].LastMsg));
            else
                listMessItem.add(new Messages_Item(dialog[i].getName(), R.drawable.account, dialog[i].DateTime.getTime() + dialog[i].LastMsg));

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

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentMess.get(position)).commit();
                Activity_Navigation.toolbar.setTitle(dialog[position].getName());
            }
        });
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
                /* !!!
                jsonCrypt.Send(socket, text);

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.write(text.getBytes(), 0, text.length());
                dos.flush();


                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
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
