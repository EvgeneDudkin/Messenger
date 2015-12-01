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
import android.widget.TextView;

import com.example.messengerpigeon.History.HistoryListAdapter;
import com.example.messengerpigeon.History.History_Item;
import com.example.messengerpigeon.LoginPasswordValidator;
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
    private Button button_send;
    private String dialogID="";
    authRequest authReq= new authRequest();
    View vv=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arg=this.getArguments();
        vv = inflater.inflate(R.layout.fragment_dialog, container, false);
        dialogID= String.valueOf(arg.getByte("dialogId"));

        listViewHistory=(ListView)vv.findViewById(R.id.list_history);
        listHistoryItem=new ArrayList<History_Item>();
        button_send=(Button)vv.findViewById(R.id.button_send);
        button_send.setOnClickListener(onClickListenermain);

        AuthTask at = new AuthTask();
        at.execute("list",authReq.getToken(), dialogID,"5");
        return vv;
    }
    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_send:
                    AuthTask at = new AuthTask();
                    EditText tt=(EditText)vv.findViewById(R.id.text_Send);
                    at.execute("send",authReq.getToken(),dialogID,tt.getText().toString());
                    tt.setText("");
            }
        }
    };
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private messageRequest listMsgReq=null;
        private messageRequest sendMsgReq=null;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {

                System.out.println(ret);
                messageRequest request=new messageRequest();
                request.responseHandler(ret);
                if(request.getRequestType()=="list") {
                    message[] mess = listMsgReq.getMessages();
                    System.out.println(mess);

                    for (int i = mess.length-1; i >= 0; i--) {
                        listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, mess[i].date.toString()));
                    }
                    HistoryListAdapter messagesListAdapter = new HistoryListAdapter(getActivity(), 1, listHistoryItem);

                    listViewHistory.setAdapter(messagesListAdapter);
                    listViewHistory.smoothScrollToPosition(mess.length - 1);
                }
                else
                    if(request.getRequestType()=="send")
                    {
                        System.out.println(request.getResponse());
                    }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                if(data[0].equals("list")) {
                    listMsgReq=new messageRequest();
                    listMsgReq.messageListRequest(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(listMsgReq.getMsgListRequest());
                }
                else
                if(data[0].equals("send")) {
                    sendMsgReq=new messageRequest();
                    sendMsgReq.sendMessageRequest(data[1], Integer.parseInt(data[2]), data[3]);
                    System.out.println(data[1]+", "+Integer.parseInt(data[2])+", "+data[3]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(sendMsgReq.getMsgSendRequest());
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
                dos.write(text.getBytes(),0,text.length());
                System.out.println(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[10000000];
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
