/**
 * Large prime number generator
 * <p>
 * Created by Arthur on 26.11.2015.
 */

import sun.security.util.BitArray;

import java.math.BigInteger;
import java.util.Random;

public abstract class PrimeNumber {
    private static final BigInteger bigTwo = new BigInteger("2");
    private static final int[] firstPrimeNumbers = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29
            , 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109
            , 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197
            , 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283
            , 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389
            , 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487
            , 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599
            , 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691
            , 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811
            , 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919
            , 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997};

    // The expansion in powers of two
    private static boolean[] degreeExpansion(BigInteger a, int length) {
        boolean[] exp = new boolean[length / 2];

        for (int i = length / 2 - 1; i >= 0; --i) {
            exp[i] = a.divideAndRemainder(bigTwo)[1].equals(BigInteger.ONE);
            a = a.divide(bigTwo);
        }

        return exp;
    }

    // The method of repeated squaring and multiplication
    // ( = a^t mod m )
    private static BigInteger modularExponentiation(BigInteger num, BigInteger a, BigInteger t, int length) {
        length = length * 4;
        BigInteger x = BigInteger.ONE;
        boolean[] exp = degreeExpansion(t, length);

        for (int i = length / 2 - 1; i >= 0; --i) {
            if (exp[i]) {
                x = x.multiply(a).divideAndRemainder(num)[1];
                a = a.multiply(a).divideAndRemainder(num)[1];
            } else {
                a = a.multiply(a).divideAndRemainder(num)[1];
            }
        }

        return x;
    }

    // Miller–Rabin primality test
    private static boolean MRTest(BigInteger num, int length) {
        int r = length;
        int s = 0;
        boolean check;
        BitArray bits = new BitArray(length / 2);
        BigInteger t = num.subtract(BigInteger.ONE);
        Random rnd = new Random();

        while (t.divideAndRemainder(bigTwo)[1].equals(BigInteger.ZERO)) {
            t = t.divide(bigTwo);
            s++;
        }

        for (int i = 0; i < r; ++i) {
            check = true;
            bits.set(length / 2 - 2, true);
            for (int j = 2; j < length / 2 - 2; ++j) {
                bits.set(j, rnd.nextBoolean());
            }

            BigInteger a = new BigInteger(bits.toByteArray());
            BigInteger x = modularExponentiation(num, a, t, length);

            if (x.equals(BigInteger.ONE) || x.equals(num.subtract(BigInteger.ONE))) {
                continue;
            }

            for (int j = 0; j < s - 1; ++j) {
                x = modularExponentiation(num, x, bigTwo, length);
                if (x.equals(BigInteger.ONE)) {
                    return false;
                }

                if (x.equals(num.subtract(BigInteger.ONE))) {
                    check = false;
                    break;
                }
            }

            if (check) {
                return false;
            }
        }
        return true;
    }

    // Primality test
    private static boolean isPrimeNumber(BigInteger num, int length) {
        for (int i = 0; i < firstPrimeNumbers.length; ++i) {
            BigInteger prime = new BigInteger(String.valueOf(firstPrimeNumbers[i]));
            if (num.divideAndRemainder(prime)[1].equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return MRTest(num, length);
    }

    // Generate random prime number specified length (bits)
    public static BigInteger generate(int length) {
        length *= 2;
        BitArray bits = new BitArray(length / 2);
        Random rnd = new Random();

        bits.set(1, true);
        bits.set(length / 2 - 1, true);
        for (int j = 2; j < length / 2 - 1; ++j) {
            bits.set(j, rnd.nextBoolean());
        }

        BigInteger num = new BigInteger(bits.toByteArray());

        while (!isPrimeNumber(num, length)) {
            num = num.add(bigTwo);
        }

        return num;
    }
}
