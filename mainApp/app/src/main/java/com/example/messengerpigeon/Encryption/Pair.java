/**
 * Created by Arthur on 29.11.2015.
 */

package com.example.messengerpigeon.Encryption;
import java.math.BigInteger;

public class Pair {
    public BigInteger x;
    public BigInteger y;

    public Pair() {
        x = BigInteger.ZERO;
        y = BigInteger.ZERO;
    }

    public Pair(BigInteger xx, BigInteger yy) {
        x = xx;
        y = yy;
    }
}
