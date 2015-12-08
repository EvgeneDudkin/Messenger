package com.example.messengerpigeon.Encryption;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Arthur on 07.12.2015.
 */
public class jsonCrypt {
    public static void Send(Socket socket, String text)
    {
        try {
            String crypt_text = jsonCrypt.Crypt(text);
            String length = String.valueOf(crypt_text.length());

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(length.getBytes());
            dos.flush();

            dos = new DataOutputStream(socket.getOutputStream());
            for (int i = 0; i < crypt_text.length(); i += 1023) {
                String tmp = crypt_text.substring(i, crypt_text.length());
                if (i + 1023 < crypt_text.length()) {
                    tmp = crypt_text.substring(i, i + 1023);
                }
                dos.write(tmp.getBytes());
            }
            dos.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String Crypt(String text) {
        int key1 = 18;
        int key2 = 17;
        int[] key3 = {2, 6, 4, 5, 1, 4, 2, 6, 2, 12, 4, 6, 8, 4, 2, 4, 9};
        int[] key4 = {2, 6, 4, 5, 1, 4, 2, 6, 2, 12, 4, 6, 8, 4, 2, 4, 9, 11};

        JSONObject new_json = new JSONObject();
        String text_crypt = DPCrypt.Encrypt(text, key1, key2, key3, key4);

        try {
            new_json.put("query", text_crypt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

        return new_json.toString();
    }
}
