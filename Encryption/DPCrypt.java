import java.util.Random;

/**
 * Double permutation encrypt (decrypt)
 *
 * Created by Arthur on 26.11.2015.
 */
public class DPCrypt {
    // Key1: Number of rows
    private static int key1;
    // Key2: Number of columns
    private static int key2;
    // Key3: Permutations
    private static int[] key3;

    DPCrypt() {
        key1 = 5;
        key2 = 5;
        key3 = new int[] {4, 3, 2, 1, 0};
    }

    DPCrypt(int xkey1, int xkey2) {
        key1 = xkey1;
        key2 = xkey2;
        key3 = new int[] {4, 3, 2, 1, 0};
    }

    DPCrypt(int xkey1, int xkey2, int[] xkey3) {
        key1 = xkey1;
        key2 = xkey2;
        key3 = xkey3;
    }

    public void setKey1(int key) {
        key1 = key;
    }

    public void setKey2(int key) {
        key2 = key;
    }

    public void setKey3(int[] key) {
        key3 = key;
    }

    public int getKey1() {
        return key1;
    }

    public int getKey2() {
        return key2;
    }

    public int[] getKey3() {
        return key3;
    }

    // Generate random key3
    public void generateKey3() {
        key3 = new int[key2];
        Random rnd = new Random();
        for (int i = 0; i < key2; ++i) {
            key3[i] = rnd.nextInt(key2);
        }
    }

    private static char[][] stringToCharArrayEnc(String str) {
        char[][] buffer = new char[key1][key2];

        for (int i = 0; i < str.length(); i++) {
            if(i == key1 * key2) {
                break;
            }
            buffer[i % key1][i / key1] = str.charAt(i);
        }

        if(str.length() < key1*key2) {
            for (int i = str.length(); i < key1 * key2; ++i) {
                buffer[i % key1][key2 - 1] = ' ';
            }
        }

        return buffer;
    }

    private static char[][] stringToCharArrayDec(String str) {
        char[][] buffer = new char[key2][key1];

        for (int i = 0; i < str.length(); i++) {
            if(i == key2 * key1) {
                break;
            }
            buffer[i % key2][i / key2] = str.charAt(i);
        }

        if(str.length() < key1*key2) {
            for (int i = str.length(); i < key1 * key2; ++i) {
                buffer[i % key2][key1 - 1] = ' ';
            }
        }

        return buffer;
    }

    private static char[][] permutationTableEnc(char[][] chartable) {
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

    private static char[][] permutationTableDec(char[][] chartable) {
        for(int i = key3.length - 1; i >= 0; --i) {
            String tmp = new String(chartable[i]);
            chartable[i] =  new String(chartable[key3[i]]).toCharArray();
            chartable[key3[i]] = tmp.toCharArray();
        }
        return chartable;
    }

    private static String charTableToString(char[][] char_table) {
        String buffer = "";

        for (int i = 0; i < char_table.length; i++) {
            String tmp = new String(char_table[i]);
            buffer += tmp;
        }
        return buffer;
    }

    // Encrypt string
    public static String Encrypt (String str) {
        char[][] chartable;
        chartable = stringToCharArrayEnc(str);
        chartable = permutationTableEnc(chartable);

        return charTableToString(chartable);
    }

    // Decrypt string
    public static String Decrypt (String str) {
        char[][] chartable;
        chartable = stringToCharArrayDec(str);
        chartable = permutationTableDec(chartable);

        int i = key1 - 1;
        while (chartable[key2 - 1][i % key1] == ' ') {
            chartable[key2 - 1][i % key1] = '\0';
            i--;
            if (i == 0)
                break;
        }

        return charTableToString(chartable);
    }
}
