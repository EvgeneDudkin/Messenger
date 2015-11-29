/**
 * Created by Arthur on 29.11.2015.
 */

import java.math.BigInteger;

public class Pair {
    public BigInteger x;
    public BigInteger y;

    Pair() {
        x = BigInteger.ZERO;
        y = BigInteger.ZERO;
    }

    Pair(BigInteger xx, BigInteger yy) {
        x = xx;
        y = yy;
    }
}
