package ch.bitagent.bitcoin.java.ecc;

import java.math.BigInteger;

/**
 * An Int from a hex value
 */
public class Hex extends Int {

    private Hex(String s) {
        super(s, 16);
    }

    public static Hex parse(String s) {
        return new Hex(s);
    }

    private Hex(byte[] bytes) {
        super(new BigInteger(1, bytes));
    }

    public static Hex parse(byte[] bytes) {
        return new Hex(bytes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%x", super.bigInt()));
        if (builder.length() % 2 == 1) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }
}
