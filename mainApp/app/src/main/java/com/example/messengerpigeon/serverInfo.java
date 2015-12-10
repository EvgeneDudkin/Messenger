package com.example.messengerpigeon;

/**
 * Created by Kirill2 on 29.10.2015.
 */
public class serverInfo {
   private static String IP = "172.20.205.88";
    //private static String IP = "217.197.0.29";
    //private static String IP = "192.168.43.108";

    private static int port = 3000;

    public static String getIP() {
        return IP;
    }

    public static int getPort() {
        return port;
    }
}
