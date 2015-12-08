package com.example.messengerpigeon.Encryption; /**
 * Rivest-Shamir-Adleman encrypt (decrypt)
 * <p>
 * Created by Arthur on 29.11.2015.
 */

import java.math.BigInteger;


public abstract class RSACrypt {
    //  Greatest common divisor (Euclid's algorithm)
    private static BigInteger gcd(BigInteger a, BigInteger num, Pair t) {
        if (a.equals(BigInteger.ZERO)) {
            t.x = BigInteger.ZERO;
            t.y = BigInteger.ONE;
            return num;
        }

        Pair t1 = new Pair(BigInteger.ZERO, BigInteger.ONE);
        BigInteger d = gcd(num.divideAndRemainder(a)[1], a, t1);
        t.x = t1.y.subtract(num.divideAndRemainder(a)[0].multiply(t1.x));
        t.y = t1.x;

        return d;
    }

    // Generate keys
    // Return private key, change public keys
    public static BigInteger generateKeys(Pair publicKey, int length) {
        BigInteger x = PrimeNumber.generate(length);
        BigInteger y = PrimeNumber.generate(length);
        BigInteger numEulerNumber = x.subtract(BigInteger.ONE).multiply(y.subtract(BigInteger.ONE));

        publicKey.x = x.multiply(y);
        publicKey.y = PrimeNumber.generate(length * 2 - 8);
        Pair t = new Pair(BigInteger.ONE, BigInteger.ZERO);
        while (!(gcd(publicKey.y, numEulerNumber, t).equals(BigInteger.ONE))) {
            t = new Pair(BigInteger.ONE, BigInteger.ZERO);
            publicKey.y = PrimeNumber.generate(length * 2 - 8);
        }

        BigInteger privateKey = t.x.divideAndRemainder(numEulerNumber)[1].add(numEulerNumber).
                divideAndRemainder(numEulerNumber)[1];

        return privateKey;
    }

    // Encrypt string
    public static String Encrypt(String str, Pair publicKey, int length) {
        char[] chars = str.toCharArray();
        BigInteger[] encrypt_chars = new BigInteger[chars.length];
        String encrypt_string = new String("");

        for (int i = 0; i < chars.length; ++i) {
            BigInteger symb = new BigInteger(String.valueOf((int) (chars[i])));
            encrypt_chars[i] = PrimeNumber.modularExponentiation(publicKey.x, symb, publicKey.y, length);
        }

        for (int i = 0; i < encrypt_chars.length; ++i) {
            encrypt_string += encrypt_chars[i].toString() + "%";
        }

        return encrypt_string;
    }

    // Decrypt string
    public static String Decrypt(String str, Pair publicKey, BigInteger privateKey, int length) {
        char[] crypt_chars = str.toCharArray();
        String decrypt_string = "";

        for (int i = 0; i < crypt_chars.length; ++i) {
            if (crypt_chars[i] == '\0') {
                break;
            }

            BigInteger crypt_num = BigInteger.ZERO;
            while (crypt_chars[i] != '%') {
                BigInteger decrypt_digit = new BigInteger(String.valueOf((int) (crypt_chars[i]) - '0'));
                crypt_num = crypt_num.multiply(BigInteger.TEN).add(decrypt_digit);
                i++;
            }

            int decrypt_num = PrimeNumber.modularExponentiation(publicKey.x, crypt_num, privateKey, length).intValue();
            char decrypt_char = (char) decrypt_num;
            decrypt_string += decrypt_char;
        }

        return decrypt_string;
    }
}
