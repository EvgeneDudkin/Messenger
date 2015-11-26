package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.messengerpigeon.History.HistoryListAdapter;
import com.example.messengerpigeon.History.History_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.messageRequest;
import com.example.messengerpigeon.miniClasses.message;
import com.example.messengerpigeon.serverInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 20.11.2015.
 */
public class fragments_messages_item extends Fragment {
    ListView listViewHistory;
    List<Fragment> listFragmentHistory;
    List<History_Item> listHistoryItem;

    authRequest authReq= new authRequest();
    View v=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_dialog, container, false);

        listViewHistory=(ListView)v.findViewById(R.id.list_history);
        listHistoryItem=new ArrayList<History_Item>();

        AuthTask at = new AuthTask();
        at.execute(authReq.getToken());
        return v;
    }
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private messageRequest mesReq=null;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mesReq=new messageRequest();
            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                mesReq.responseHandler(ret);

                message[] mess=mesReq.getMessages();
                System.out.println(mess);
                  int l=mess.length;

                for(int i=0;i<l;i++) {
                    listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, "time"));
                }
                HistoryListAdapter messagesListAdapter = new HistoryListAdapter(getActivity(), 1, listHistoryItem);

                listViewHistory.setAdapter(messagesListAdapter);
                listViewHistory.smoothScrollToPosition(l-1);

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                mesReq.createRequest(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                return sendAndListen(mesReq.get_Request());
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
