package ch.bitagent.bitcoin.java.helper;

import ch.bitagent.bitcoin.java.ecc.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base58Test {

    @Test
    void base58() {
        var addr = "mnrVtF8DWjMu839VW3rBfgYaAfKk8983Xf";
        var h160 = Hex.parse(Base58.decode(addr));
        var want = Hex.parse("507b27411ccf7f16f10297de6cef3f291623eddf");
        assertEquals(h160, want);
        var got = Base58.encodeChecksum(Bytes.add(new byte[]{0x6f}, h160.toBytes()));
        assertEquals(got, addr);
    }

    @Test
    void p2pkhAddress() {
        var h160 = Hex.parse("74d691da1574e6b3c192ecfb52cc8984ee7b6c56");
        var want = "1BenRpVUFK65JFWcQSuHnJKzc4M8ZP8Eqa";
        assertEquals(want, Base58.h160toP2pkhAddress(h160.toBytes(), false));
        assertEquals(want, Base58.h160toP2pkhAddress(h160.toBytes(), null));
        want = "mrAjisaT4LXL5MzE81sfcDYKU3wqWSvf9q";
        assertEquals(want, Base58.h160toP2pkhAddress(h160.toBytes(), true));
    }

    @Test
    void p2shAddress() {
        var h160 = Hex.parse("74d691da1574e6b3c192ecfb52cc8984ee7b6c56");
        var want = "3CLoMMyuoDQTPRD3XYZtCvgvkadrAdvdXh";
        assertEquals(want, Base58.h160toP2shAddress(h160.toBytes(), false));
        assertEquals(want, Base58.h160toP2shAddress(h160.toBytes(), null));
        want = "2N3u1R6uwQfuobCqbCgBkpsgBxvr1tZpe7B";
        assertEquals(want, Base58.h160toP2shAddress(h160.toBytes(), true));
    }
}