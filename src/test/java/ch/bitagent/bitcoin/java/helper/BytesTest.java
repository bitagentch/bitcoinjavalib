package ch.bitagent.bitcoin.java.helper;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BytesTest {

    @Test
    void changeOrder() {
        byte[] big = new byte[]{0x00, 0x00, 0x00, 0x01};
        byte[] little = new byte[]{0x01, 0x00, 0x00, 0x00};
        assertArrayEquals(little, Bytes.changeOrder(big));
        assertArrayEquals(big, Bytes.changeOrder(little));
    }

    @Test
    void calculateNewBits() {
        var prevBits = Hex.parse("54d80118").toBytes();
        var timeDifferential = Int.parse(302400);
        var want = Hex.parse("00157617");
        assertEquals(want, Hex.parse(Bytes.calculateNewBits(prevBits, timeDifferential)));
    }

    @Test
    void bitFieldToBytesToBitField() {
        var bitField = new byte[]{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
        var want = Hex.parse("4000600a080000010940").toBytes();
        assertArrayEquals(Bytes.bitFieldToBytes(bitField), want);
        assertArrayEquals(Bytes.bytesToBitField(want), bitField);
    }
}
