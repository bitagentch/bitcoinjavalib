package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IntTest {

    @Test
    void mod() {
        assertEquals(Int.parse(1), Int.parse(7).mod(Int.parse(3)));
        assertEquals(Int.parse(12), Int.parse(-27).mod(Int.parse(13)));

        Int prime = Int.parse(19);
        assertEquals(Int.parse(15), Int.parse(7+8).mod(prime));
        assertEquals(Int.parse(9), Int.parse(11+17).mod(prime));
        assertEquals(Int.parse(10), Int.parse(-9).mod(prime));
        assertEquals(Int.parse(0), Int.parse(9+10).mod(prime));
        assertEquals(Int.parse(12), Int.parse(6-13).mod(prime));
        assertEquals(Int.parse(15), Int.parse(15).mod(prime));
        assertEquals(Int.parse(3), Int.parse(136).mod(prime));
        assertEquals(Int.parse(1), Int.parse(343).mod(prime));
        assertEquals(Int.parse(7), Int.parse("282429536481").mod(prime));

        prime = Int.parse(57);
        assertEquals(Int.parse(20), Int.parse(44+33).mod(prime));
        assertEquals(Int.parse(37), Int.parse(9-29).mod(prime));
        assertEquals(Int.parse(51), Int.parse(17+42+49).mod(prime));
        assertEquals(Int.parse(41), Int.parse(52-30-38).mod(prime));

        prime = Int.parse(97);
        assertEquals(Int.parse(23), Int.parse(95*45*31).mod(prime));
        assertEquals(Int.parse(68), Int.parse(17*13*19*44).mod(prime));
        assertEquals(Int.parse(63), Int.parse(12).pow(Int.parse(7)).mul(Int.parse(77).pow(Int.parse(49))).mod(prime));
    }

    @Test
    void pow() {
        assertEquals(343, Int.parse(7).pow(Int.parse(3)).intValue());
        assertEquals(Int.parse("282429536481"), Int.parse(9).pow(Int.parse(12)));
        assertEquals(35831808, Int.parse(12).pow(Int.parse(7)).intValue());
        assertEquals(Int.parse("274186162637269249876356254390911428454103218717687870970501383734654777901371192140475380237"), Int.parse(77).pow(Int.parse(49)));
    }

    @Test
    void powMod() {
        Int prime = Int.parse(31);
        assertEquals(Int.parse(29), Int.parse(17).powMod(prime.sub(Int.parse(4)), prime));
    }

    @Test
    void intToLittleEndian() {
        Int n = Int.parse(1);
        byte[] want = new byte[]{0x01, 0x00, 0x00, 0x00};
        assertArrayEquals(want, n.toBytesLittleEndian(4));

        n = Int.parse(10011545);
        want = new byte[]{(byte) 0x99, (byte) 0xc3, (byte) 0x98, 0x00, 0x00, 0x00, 0x00, 0x00};
        assertArrayEquals(want, n.toBytesLittleEndian(8));
    }

    @Test
    void littleEndianToInt() {
        Int h = Hex.parse("99c3980000000000");
        Int want = Int.parse(10011545);
        assertEquals(want, Hex.parse(Bytes.changeOrder(h.toBytes())));

        h = Hex.parse("a135ef0100000000");
        want = Int.parse(32454049);
        assertEquals(want, Hex.parse(Bytes.changeOrder(h.toBytes())));
    }
}
