package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.messengerpigeon.History.HistoryListAdapter;
import com.example.messengerpigeon.History.History_Item;
import com.example.messengerpigeon.LoginPasswordValidator;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonRequest.jsonRequest;
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
import java.util.Objects;

/**
 * Created by egor on 20.11.2015.
 */
public class fragments_messages_item extends Fragment {
    ListView listViewHistory;
    List<Fragment> listFragmentHistory;
    List<History_Item> listHistoryItem;
    private Button button_send;
    private String dialogID = "";
    authRequest authReq = new authRequest();
    View vv = null;

    boolean blueFlag = false;
    boolean WhiteFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arg = this.getArguments();
        vv = inflater.inflate(R.layout.fragment_dialog, container, false);
        dialogID = String.valueOf(arg.getByte("dialogId"));

        listViewHistory = (ListView) vv.findViewById(R.id.list_history);
        listViewHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listViewHistory.setStackFromBottom(true);
        listHistoryItem = new ArrayList<History_Item>();
        button_send = (Button) vv.findViewById(R.id.button_send);
        button_send.setOnClickListener(onClickListenermain);

        AuthTask at = new AuthTask();
        at.execute("list", authReq.getToken(), dialogID, "15");

        listViewHistory.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                //Algorithm to check if the last item is visible or not
                final int lastItem = totalItemCount - firstVisibleItem;
                if (!WhiteFlag) {
                    if (lastItem == totalItemCount && totalItemCount != 0) {
                        // you have reached end of list, load more data
                        System.out.println("popali");
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //blank, not using this
            }
        });

        return vv;
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_send) {
                AuthTask at = new AuthTask();
                EditText tt = (EditText) vv.findViewById(R.id.text_Send);
                if (!tt.getText().toString().equals("")) {
                    at.execute("send", authReq.getToken(), dialogID, tt.getText().toString());
                    tt.setText("");
                    //клавиатура должна исчезнуть, но нет ¯\_(ツ)_/¯
                    tt.clearFocus();
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                } else {
                    //клавиатура должна исчезнуть, но нет ¯\_(ツ)_/¯
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            }
        }
    };

    public void loadMore() {

        if (blueFlag == false) {
            blueFlag = true;
            AuthTask at = new AuthTask();
            at.execute("oldM", authReq.getToken(), dialogID, "15");
        }
    }

    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private messageRequest listMsgReq = null;
        private messageRequest sendMsgReq = null;
        private jsonRequest oldMReq = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {

                System.out.println(ret);
                messageRequest request = new messageRequest();
                request.responseHandler(ret);
                if (Objects.equals(request.getRequestType(), "list")) {
                    message[] mess = listMsgReq.getMessages();
                    System.out.println(mess);
                    WhiteFlag=mess.length==0;
                    if (listHistoryItem.size() != 0) {
                        for (int i = 0; i < mess.length; ++i) {
                            listHistoryItem.add(0, new History_Item(mess[i].login, mess[i].text, mess[i].date.toString(), mess[i].messageID));
                        }
                    } else {
                        for (int i = mess.length - 1; i >= 0; i--) {
                            listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, mess[i].date.toString(), mess[i].messageID));
                        }
                    }
                    HistoryListAdapter messagesListAdapter = new HistoryListAdapter(getActivity(), 1, listHistoryItem);

                    listViewHistory.setAdapter(messagesListAdapter);

                    int index = listViewHistory.getFirstVisiblePosition() + mess.length;
                    View v = listViewHistory.getChildAt(listViewHistory.getHeaderViewsCount());
                    int top = (v == null) ? 0 : v.getTop();

                    listViewHistory.setSelectionFromTop(index, top);

                    blueFlag = false;
                } else if (Objects.equals(request.getRequestType(), "send")) {
                    System.out.println(request.getResponse());
                    listViewHistory.setAdapter(null);
                    listHistoryItem.clear();
                    AuthTask at = new AuthTask();
                    at.execute("list", authReq.getToken(), dialogID, "15");
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                if (data[0].equals("list")) {
                    listMsgReq = new messageRequest();
                    listMsgReq.messageListRequest(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(listMsgReq.getMsgListRequest());
                } else if (data[0].equals("send")) {
                    sendMsgReq = new messageRequest();
                    sendMsgReq.sendMessageRequest(data[1], Integer.parseInt(data[2]), data[3]);
                    System.out.println(data[1] + ", " + Integer.parseInt(data[2]) + ", " + data[3]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(sendMsgReq.getMsgSendRequest());
                } else if (data[0].equals("oldM")) {
                    oldMReq = new jsonRequest();
                    String out = oldMReq.lastNmsgRequest(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), null, listHistoryItem.get(0).getMessageId());
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(out);
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
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.write(text.getBytes(), 0, text.length());
                System.out.println(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                byte buffer[] = new byte[1024];
                String ret = "";
                int ss = dis.read(buffer);

                while (ss > 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer, 0, ss);
                    byte result[] = baos.toByteArray();
                    ret += new String(result, "UTF-8");
                    ss = dis.read(buffer);
                }
                return ret;

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }


    }
}
