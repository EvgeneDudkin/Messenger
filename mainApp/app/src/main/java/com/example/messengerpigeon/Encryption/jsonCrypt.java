package com.example.messengerpigeon.Encryption;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Arthur on 07.12.2015.
 */
public class jsonCrypt {
    public static int key1; // = 107;
    public static int key2; // = 131;
    public static int[] key3; /* = {34, 25, 52, 88, 69, 95, 33, 34, 71, 83,
            100, 71, 21, 77, 100, 5, 98, 17, 5, 71, 96, 118, 4, 64, 82, 29, 25,
            88, 127, 3, 75, 122, 32, 113, 54, 127, 32, 90, 123, 49, 68, 35, 86,
            24, 68, 22, 7, 45, 40, 96, 60, 98, 107, 104, 65, 72, 7, 70, 125, 108,
            124, 67, 16, 112, 53, 91, 73, 113, 73, 85, 86, 18, 31, 32, 6, 5, 40,
            7, 59, 35, 61, 129, 1, 96, 96, 58, 45, 22, 83, 104, 15, 98, 89, 4,
            43, 15, 79, 26, 114, 95, 67, 111, 15, 121, 100, 56, 11, 59, 106, 80,
            36, 85, 95, 2, 110, 34, 25, 58, 43, 43, 0, 8, 27, 73, 47, 41, 39, 99,
            111, 4, 18};*/
    public static int[] key4; /* = {13, 6, 37, 86, 7, 97, 95, 72, 106, 105,
            25, 95, 7, 2, 29, 35, 53, 103, 48, 7, 37, 52, 63, 68, 22, 84, 3,
            0, 94, 25, 74, 21, 76, 28, 47, 23, 71, 30, 26, 91, 32, 47, 46, 66,
            106, 30, 81, 69, 72, 99, 43, 54, 33, 44, 27, 80, 8, 24, 29, 39, 98,
            4, 28, 47, 94, 12, 8, 28, 77, 57, 29, 71, 61, 36, 21, 68, 104, 1, 3,
            32, 40, 11, 101, 73, 70, 14, 36, 8, 102, 78, 21, 81, 77, 82, 43, 0,
            78, 52, 104, 52, 68, 3, 65, 104, 70, 6, 52};*/

    public static int salt;

    public static void Send(Socket socket, String text) {
        try {
            String encrypt_text = Encrypt(text);
            String length = String.valueOf(encrypt_text.length());

            System.out.println(encrypt_text);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(length.getBytes());
            dos.flush();

            dos = new DataOutputStream(socket.getOutputStream());
            for (int i = 0; i < encrypt_text.length(); i += 1023) {
                String tmp = encrypt_text.substring(i, encrypt_text.length());
                if (i + 1023 < encrypt_text.length()) {
                    tmp = encrypt_text.substring(i, i + 1023);
                }
                dos.write(tmp.getBytes());
            }
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String Get(Socket socket) {
        String text = "";
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            byte buffer[] = new byte[1024];
            int ss = dis.read(buffer);

            while (ss != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(buffer, 0, ss);
                byte result[] = baos.toByteArray();
                text += new String(result, "UTF-8");
                ss = dis.read(buffer);
            }

            JSONObject ret = new JSONObject(text);
            int length = ret.getInt("key");
            salt = ret.getInt("salt");
            String encrypt_text = ret.getString("query");
            text = Decrypt(encrypt_text, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private static String Encrypt(String text) {
        Random rnd = new Random();
        salt = rnd.nextInt(16127);

        getMyKeyBitch(text.length());
        System.out.println(key1 + " " + key2 + " " + salt);
        for (int i = 0; i < key2; ++i)
            System.out.print(key3[i] + " ");
        System.out.println();
        for (int i = 0; i < key1; ++i)
            System.out.print(key4[i] + " ");
        System.out.println();

        JSONObject new_json = new JSONObject();
        String text_encrypt = DPCrypt.Encrypt(text, key1, key2, key3, key4);

        try {
            new_json.put("query", text_encrypt);
            new_json.put("key", text.length());
            new_json.put("salt", salt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

        return new_json.toString();
    }

    private static String Decrypt(String text, int length) {
        getMyKeyBitch(length);

        System.out.println(key1 + " " + key2 + " " + salt);
        for (int i = 0; i < key2; ++i)
            System.out.print(key3[i] + " ");
        System.out.println();
        for (int i = 0; i < key1; ++i)
            System.out.print(key4[i] + " ");
        System.out.println();

        return DPCrypt.Decrypt(text, key1, key2, key3, key4, length);
    }

    private static void getMyKeyBitch(int length) {
        key1 = (int) Math.sqrt(length) + 1 + salt % 13;
        key2 = (int) Math.sqrt(length) + 1 + key1 % 13;
        key3 = new int[key2];
        key4 = new int[key1];
        key3[0] = (length * 37 * key1) % key2;
        for (int i = 1; i < key2; i++) {
            key3[i] = (key3[i - 1] * 37 * key1 + i) % key2;
        }
        ;
        key4[0] = (length * 37 * key2) % key1;
        for (int i = 1; i < key1; i++) {
            key4[i] = (key4[i - 1] * 37 * key3[i % key2] + i) % key1;
        }
        ;
    }
}
