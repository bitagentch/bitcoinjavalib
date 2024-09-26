package ch.bitagent.bitcoin.java.ecc;

import java.math.BigInteger;

/**
 * An Int from a hex value
 */
public class Hex extends Int {

    private Hex(String s) {
        super(s, 16);
    }

    /**
     * <p>parse.</p>
     *
     * @param s a {@link java.lang.String} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Hex} object
     */
    public static Hex parse(String s) {
        return new Hex(s);
    }

    private Hex(byte[] bytes) {
        super(new BigInteger(1, bytes));
    }

    /**
     * <p>parse.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Hex} object
     */
    public static Hex parse(byte[] bytes) {
        return new Hex(bytes);
    }

    /** {@inheritDoc} */
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
