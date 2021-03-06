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

import com.example.messengerpigeon.Encryption.DPCrypt;
import com.example.messengerpigeon.Encryption.Pair;
import com.example.messengerpigeon.Encryption.RSACrypt;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.History.HistoryListAdapter;
import com.example.messengerpigeon.History.History_Item;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

//import com.example.messengerpigeon.Encryption.RSACrypt;

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
    boolean WhiteFlag = false;

    boolean alreadyAtTop = false;

    boolean scrolling=false;
    boolean alreadyAtBottom=false;
    boolean messagesLoaded=false;
    int align=0;
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
                if(messagesLoaded) {
                    System.out.println(firstVisibleItem + " asdasd " + (totalItemCount));
                    int lastItem = totalItemCount - firstVisibleItem;
                    if (!alreadyAtTop) {
                        if (lastItem == totalItemCount && totalItemCount != 0) {
                            // you have reached end of list, load more data
                            System.out.println("popali");
                            alreadyAtTop = true;
                            align = 1;
                            loadOldHistory();
                        }
                    }
                    if (!alreadyAtBottom && scrolling) {
                        //CHTO ZA PYAT???????????
                        if (firstVisibleItem + 5 == totalItemCount && totalItemCount != 0) {
                            // you have reached end of list, load more data
                            System.out.println("popali2");
                            alreadyAtBottom = true;
                            align = -1;
                            loadNewMessages();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //blank, not using this
                if (scrollState == SCROLL_STATE_IDLE) {
                    scrolling = false;
                    alreadyAtBottom = false;
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    scrolling = true;
                }
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

    public void loadOldHistory() {
        AuthTask at = new AuthTask();
        at.execute("oldM", authReq.getToken(), dialogID, "15");
    }
    public void loadNewMessages()
    {
        AuthTask at = new AuthTask();
        at.execute("newM", authReq.getToken(), dialogID, "15");
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

                    Date now = new Date();
                    if (listHistoryItem.size() != 0) {
                        if(alreadyAtTop && align==1) {
                        for (int i = 0; i < mess.length; ++i) {

                            if (mess[i].date.getTime() + (1000 * 60 * 60 * 24) - (1000 * 60 * 60 * 4) > now.getTime()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                String str = sdf.format(mess[i].date);
                                listHistoryItem.add(0, new History_Item(mess[i].login, mess[i].text, str, mess[i].messageID));
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMM");
                                String str = sdf.format(mess[i].date);
                                listHistoryItem.add(0, new History_Item(mess[i].login, mess[i].text, str, mess[i].messageID));
                            }

                            //listHistoryItem.add(0, new History_Item(mess[i].login, mess[i].text, mess[i].date.toString(), mess[i].messageID));
                        }
                        }
                        else
                        if(alreadyAtBottom && align==-1)
                        {
                            for (int i = mess.length - 1; i >= 0; i--) {
                                listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, mess[i].date.toString(), mess[i].messageID));
                            }
                        }

                    } else {
                        for (int i = mess.length - 1; i >= 0; i--) {
                            if (mess[i].date.getTime() + (1000 * 60 * 60 * 24) - (1000 * 60 * 60 * 4) > now.getTime()) {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                String str = sdf.format(mess[i].date);
                                listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, str, mess[i].messageID));
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMM");
                                String str = sdf.format(mess[i].date);
                                listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, str, mess[i].messageID));
                            }
                            //listHistoryItem.add(new History_Item(mess[i].login, mess[i].text, mess[i].date.toString(), mess[i].messageID));
                        }
                    }
                    if(mess.length!=0) {
                        HistoryListAdapter messagesListAdapter = new HistoryListAdapter(getActivity(), 1, listHistoryItem);

                        listViewHistory.setAdapter(messagesListAdapter);

                        int index = listViewHistory.getFirstVisiblePosition() + mess.length;
                        View v = listViewHistory.getChildAt(listViewHistory.getHeaderViewsCount());
                        int top = (v == null) ? 0 : v.getTop();
                        if(align!=-1)
                        listViewHistory.setSelectionFromTop(index, top);
                    }
                    if(align==1)
                        alreadyAtTop = mess.length == 0;
                    else
                    if(align==-1)
                        alreadyAtBottom= mess.length == 0;
                    align=0;
                    messagesLoaded=true;

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

            /*
            Pair publicKey1 = new Pair(new BigInteger("731290067"), new BigInteger("4540687"));
            Pair publicKey2 = new Pair(new BigInteger("959258543"), new BigInteger("4958761"));
            BigInteger privateKey1 = new BigInteger("60210103");
            BigInteger privateKey2 = new BigInteger("907563841");

            int intlength = 16;
            Pair publicKey = new Pair();
            BigInteger privateKey = RSACrypt.generateKeys(publicKey, intlength);
            System.out.println(publicKey.x.toString() + " " + publicKey.y.toString());
            System.out.println(privateKey);
            int key1 = 107;
            int key2 = 113;
            int[] key3 = DPCrypt.generateKey3(key2);
            int[] key4 = DPCrypt.generateKey4(key1);
            */

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

                    /*
                    System.out.println(data[3]);
                    //String encryptMessage = RSACrypt.Encrypt(data[3],publicKey1, intlength);
                    String encryptMessage = DPCrypt.Encrypt(data[3],key1,key2,key3,key4);
                    System.out.println(encryptMessage);
                    //String decryptMessage = RSACrypt.Decrypt(encryptMessage, publicKey1, privateKey1, intlength);
                    String decryptMessage = DPCrypt.Decrypt(encryptMessage,key1,key2,key3,key4,data[3].length());
                    System.out.println(decryptMessage);
                    sendMsgReq.sendMessageRequest(data[1], Integer.parseInt(data[2]), encryptMessage);
                    System.out.println(data[1] + ", " + Integer.parseInt(data[2]) + ", " + encryptMessage);
                    */
                    sendMsgReq.sendMessageRequest(data[1], Integer.parseInt(data[2]), data[3]);
                    System.out.println(data[1] + ", " + Integer.parseInt(data[2]) + ", " + data[3]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(sendMsgReq.getMsgSendRequest());
                } else if (data[0].equals("oldM")) {
                    oldMReq = new jsonRequest();
                    String out = oldMReq.lastNmsgRequest(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), null, listHistoryItem.get(0).getMessageId(),-1);
                    System.out.println(out);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(out);
                }
                else if (data[0].equals("newM")) {
                    oldMReq = new jsonRequest();
                    String out = oldMReq.lastNmsgRequest(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), null,-1, listHistoryItem.get(listHistoryItem.size()-1).getMessageId());
                    System.out.println(out);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(out);
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

                /*
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.write(text.getBytes(), 0, text.length());
                System.out.println(text);
                dos.flush();
*/

                jsonCrypt.Send(socket, text);
/*
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                byte buffer[] = new byte[1024];
                String ret = "";
                int ss = dis.read(buffer);

                while(ss != -1) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer, 0, ss);
                    byte result[] = baos.toByteArray();
                    ret += new String(result, "UTF-8");
                    ss = dis.read(buffer);
                }
                */

                return jsonCrypt.Get(socket);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }


    }
}
