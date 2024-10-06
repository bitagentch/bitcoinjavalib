package ch.bitagent.bitcoin.lib.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bech32Test {

    @Test
    void verifyTrue() {
        assertTrue(Bech32.verify("A12UEL5L"));
        assertTrue(Bech32.verify("a12uel5l"));
        assertTrue(Bech32.verify("an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs"));
        assertTrue(Bech32.verify("abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw"));
        assertTrue(Bech32.verify("11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j"));
        assertTrue(Bech32.verify("split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w"));
        assertTrue(Bech32.verify("?1ezyfcl"));
    }

    @Test
    void verifyFalse() {
        assertFalse(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x20}) + "1nwldj5"));
        assertFalse(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x7F}) + "1axkwrx"));
        assertFalse(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x80}) + "1eym55h"));
        assertFalse(Bech32.verify("an84characterslonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1569pvx"));
        assertFalse(Bech32.verify("pzry9x0s0muk"));
        assertFalse(Bech32.verify("1pzry9x0s0muk"));
        assertFalse(Bech32.verify("x1b4n0q5v"));
        assertFalse(Bech32.verify("li1dgmt3"));
        assertFalse(Bech32.verify("de1lg7wt" + Bytes.byteArrayToString(new byte[]{(byte) 0xff})));
        assertFalse(Bech32.verify("A1G7SGD8"));
        assertFalse(Bech32.verify("10a06t8"));
        assertFalse(Bech32.verify("1qzzfhee"));
    }

    @Test
    void decode() {
        var bech32 = Bech32.decode("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4");
        assertEquals("bc", bech32.getHrp());
        assertArrayEquals(new byte[]{0, 14, 20, 15, 7, 13, 26, 0, 25, 18, 6, 11, 13, 8, 21, 4, 20, 3, 17, 2, 29, 3, 12, 29, 3, 4, 15, 24, 20, 6, 14, 30, 22}, bech32.getDataBytes());
    }

    @Test
    void encode() {
        var bech32 = Bech32.encode("bc", new byte[]{0, 14, 20, 15, 7, 13, 26, 0, 25, 18, 6, 11, 13, 8, 21, 4, 20, 3, 17, 2, 29, 3, 12, 29, 3, 4, 15, 24, 20, 6, 14, 30, 22});
        assertEquals("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4", bech32);
    }

    @Test
    void decodeSegwit() {
        assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6", Bech32.decodeSegwit("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4"));
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", Bech32.decodeSegwit("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7"));
        assertEquals("5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", Bech32.decodeSegwit("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx"));
        assertEquals("6002751e", Bech32.decodeSegwit("BC1SW50QA3JX3S"));
        assertEquals("5210751e76e8199196d454941c45d1b3a323", Bech32.decodeSegwit("bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj"));
        assertEquals("0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", Bech32.decodeSegwit("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy"));

        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KN40WF2"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1rw5uspcuh"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sL5k7"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1zw508d6qejxtdg4y5r3zarvaryvqyzf3du"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pjxtptv"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1gmk9yu"));
    }

    @Test
    void encodeSegwit() {
        assertEquals("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4", Bech32.encodeSegwit("bc", "0014751e76e8199196d454941c45d1b3a323f1433bd6"));
    }

    @Test
    void decodeNostr() {
        assertEquals("7e7e9c42a91bfef19fa929e5fda1b72e0ebc1a4c1141673e2794234d86addf4e", Bech32.decodeNostr("npub10elfcs4fr0l0r8af98jlmgdh9c8tcxjvz9qkw038js35mp4dma8qzvjptg"));
        assertEquals("67dea2ed018072d675f5415ecfaed7d2597555e202d85b3d65ea4e58d2d92ffa", Bech32.decodeNostr("nsec1vl029mgpspedva04g90vltkh6fvh240zqtv9k0t9af8935ke9laqsnlfe5"));
    }

    @Test
    void encodeNostr() {
        Assertions.assertEquals("npub10elfcs4fr0l0r8af98jlmgdh9c8tcxjvz9qkw038js35mp4dma8qzvjptg", Bech32.encodeNostr("npub", "7e7e9c42a91bfef19fa929e5fda1b72e0ebc1a4c1141673e2794234d86addf4e"));
        Assertions.assertEquals("nsec1vl029mgpspedva04g90vltkh6fvh240zqtv9k0t9af8935ke9laqsnlfe5", Bech32.encodeNostr("nsec", "67dea2ed018072d675f5415ecfaed7d2597555e202d85b3d65ea4e58d2d92ffa"));
    }
}