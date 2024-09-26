package ch.bitagent.bitcoin.java.helper;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;

import java.io.ByteArrayInputStream;

/**
 * <p>Varint class.</p>
 */
public class Varint {

    private Varint() {}

    /**
     * Reads a variable integer from a stream
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public static Int read(ByteArrayInputStream stream) {
        byte bite = Bytes.read(stream, 1)[0];
        if (bite == (byte) 0xfd) {
            // 0xfd means the next two bytes are the number
            return Hex.parse(Bytes.changeOrder(Bytes.read(stream, 2)));
        } else if (bite == (byte) 0xfe) {
            // 0xfe means the next four bytes are the number
            return Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        } else if (bite == (byte) 0xff) {
            // 0xff means the next eight bytes are the number
            return Hex.parse(Bytes.changeOrder(Bytes.read(stream, 8)));
        } else {
            // anything else is just the integer
            return Hex.parse(new byte[]{bite});
        }
    }

    /**
     * Encodes an integer as a varint
     *
     * @param i a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return an array of {@link byte} objects
     */
    public static byte[] encode(Int i) {
        if (i.lt(Hex.parse("fd"))) {
            return i.toBytes(1);
        } else if (i.lt(Hex.parse("10000"))) {
            return Bytes.add(new byte[]{(byte) 0xfd}, i.toBytesLittleEndian(2));
        } else if (i.lt(Hex.parse("100000000"))) {
            return Bytes.add(new byte[]{(byte) 0xfe}, i.toBytesLittleEndian(4));
        } else if (i.lt(Hex.parse("10000000000000000"))) {
            return Bytes.add(new byte[]{(byte) 0xff}, i.toBytesLittleEndian(8));
        }
        throw new IllegalArgumentException(String.format("Int too large: %s", i));
    }
}
