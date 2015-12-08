package com.example.messengerpigeon.Encryption; /**
 * Double permutation encrypt (decrypt)
 * <p>
 * Created by Arthur on 26.11.2015.
 */

import java.util.Random;

public abstract class DPCrypt {
    // Key1: Number of rows
    private static int key1;
    // Key2: Number of columns
    private static int key2;
    // Key3: First permutations
    private static int[] key3;
    // Key3: Second permutations
    private static int[] key4;

    // Generate random key3
    public static int[] generateKey3(int key2_) {
        int[] random_key3 = new int[key2_];
        Random rnd = new Random();
        for (int i = 0; i < key2_; ++i) {
            random_key3[i] = rnd.nextInt(key2_);
        }
        return random_key3;
    }

    // Generate random key3
    public static int[] generateKey4(int key1_) {
        int[] random_key4 = new int[key1_];
        Random rnd = new Random();
        for (int i = 0; i < key1_; ++i) {
            random_key4[i] = rnd.nextInt(key1_);
        }
        return random_key4;
    }

    // Converts string to char array
    // (Encryption)
    private static char[][] stringToCharArrayEnc(String str) {
        char[][] buffer = new char[key1][key2];

        for (int i = 0; i < str.length(); i++) {
            if (i == key1 * key2) {
                break;
            }
            buffer[i % key1][i / key1] = str.charAt(i);
        }

        if (str.length() < key1 * key2) {
            for (int i = str.length(); i < key1 * key2; ++i) {
                buffer[i % key1][key2 - 1] = ' ';
            }
        }

        return buffer;
    }

    // Converts string to char array
    // (Decryption)
    private static char[][] stringToCharArrayDec(String str) {
        char[][] buffer = new char[key2][key1];

        for (int i = 0; i < str.length(); i++) {
            if (i == key2 * key1) {
                break;
            }
            buffer[i % key2][i / key2] = str.charAt(i);
        }

        if (str.length() < key1 * key2) {
            for (int i = str.length(); i < key1 * key2; ++i) {
                buffer[i % key2][key1 - 1] = ' ';
            }
        }

        return buffer;
    }

    // Change the order of columns
    // (Encrypt)
    private static char[][] permutationTableEncFirst(char[][] chartable) {
        for (int i = 0; i < chartable.length; ++i) {
            char[] row = chartable[i];
            for (int j = 0; j < key3.length; ++j) {
                char tmp = chartable[i][j];
                chartable[i][j] = chartable[i][key3[j]];
                chartable[i][key3[j]] = tmp;
            }
        }
        return chartable;
    }

    // Change the order of rows
    // (Encrypt)
    private static char[][] permutationTableEncSecond(char[][] chartable) {
        for (int i = 0; i < key4.length; ++i) {
            String tmp = new String(chartable[i]);
            chartable[i] = chartable[key4[i]];
            chartable[key4[i]] = tmp.toCharArray();
        }
        return chartable;
    }

    // Change the order of columns
    // (Decrypt)
    private static char[][] permutationTableDecFirst(char[][] chartable) {
        for (int i = chartable.length; i >= 0; --i) {
            char[] row = chartable[i];
            for (int j = 0; j < key4.length; ++j) {
                char tmp = chartable[i][j];
                chartable[i][j] = chartable[i][key4[j]];
                chartable[i][key4[j]] = tmp;
            }
        }
        return chartable;
    }

    // Change the order of rows
    // (Decrypt)
    private static char[][] permutationTableDecSecond(char[][] chartable) {
        for (int i = key4.length - 1; i >= 0; --i) {
            String tmp = new String(chartable[i]);
            chartable[i] = new String(chartable[key4[i]]).toCharArray();
            chartable[key4[i]] = tmp.toCharArray();
        }
        return chartable;
    }

    // Convert char table to string
    private static String charTableToString(char[][] char_table) {
        String buffer = "";

        for (int i = 0; i < char_table.length; i++) {
            String tmp = new String(char_table[i]);
            buffer += tmp;
        }
        return buffer;
    }

    // Encrypt string
    public static String Encrypt(String str, int key1_, int key2_, int[] key3_, int[] key4_) {
        key1 = key1_;
        key2 = key2_;
        key3 = key3_;
        key4 = key4_;
        char[][] chartable;
        chartable = stringToCharArrayEnc(str);
        chartable = permutationTableEncFirst(chartable);
        chartable = permutationTableEncSecond(chartable);

        return charTableToString(chartable);
    }

    // Decrypt string
    public static String Decrypt(String str, int key1_, int key2_, int[] key3_, int[] key4_) {
        key1 = key1_;
        key2 = key2_;
        key3 = key3_;
        key4 = key4_;
        char[][] chartable;
        chartable = stringToCharArrayDec(str);
        chartable = permutationTableDecFirst(chartable);
        chartable = permutationTableDecSecond(chartable);

        int i = key1 - 1;
        while (chartable[key2 - 1][i % key1] == ' ') {
            chartable[key2 - 1][i % key1] = '\0';
            i--;
            if (i == -1)
                break;
        }

        return charTableToString(chartable);
    }
}
