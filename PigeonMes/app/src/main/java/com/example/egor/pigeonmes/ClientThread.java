package com.example.egor.pigeonmes;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Kirill2 on 28.10.2015.
 */
/*public class ClientThread implements Runnable {
    public ClientThread(String s) {
        msg = s;
    }
    private String msg = null;
    public String ret = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    Socket socket = null;
    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(IP);
            System.out.println(serverAddr);
            socket = new Socket(IP, port);
            ret = sendAndListen(msg);
            authActivity.feedBack(ret);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public String sendAndListen(String text) {
        try {
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataOutputStream.writeUTF(text);
            dataOutputStream.flush();
            dataInputStream = new DataInputStream(socket.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            int s=dataInputStream.read(buffer);

            baos.write(buffer, 0, s);
            byte result[] = baos.toByteArray();
            return new String(result, "UTF-8");

        } catch (EOFException e) {
            e.printStackTrace();
            return e.toString();
        }catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }


}*/
