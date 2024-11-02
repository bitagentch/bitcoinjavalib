package ch.bitagent.bitcoin.lib.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bech32Test {

    @Test
    void verifyTrue() {
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("A12UEL5L"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("a12uel5l"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w"));
        assertSame(Bech32.Encoding.BECH32, Bech32.verify("?1ezyfcl"));
    }

    @Test
    void verifyTrueM() {
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("A1LQFN3A"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("a1lqfn3a"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("an83characterlonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber11sg7hg6"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("11llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllludsr8"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("split1checkupstagehandshakeupstreamerranterredcaperredlc445v"));
        assertSame(Bech32.Encoding.BECH32M, Bech32.verify("?1v759aa"));
    }

    @Test
    void verifyFalse() {
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x20}) + "1nwldj5"));
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x7F}) + "1axkwrx"));
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x80}) + "1eym55h"));
        assertNull(Bech32.verify("an84characterslonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1569pvx"));
        assertNull(Bech32.verify("pzry9x0s0muk"));
        assertNull(Bech32.verify("1pzry9x0s0muk"));
        assertNull(Bech32.verify("x1b4n0q5v"));
        assertNull(Bech32.verify("li1dgmt3"));
        assertNull(Bech32.verify("de1lg7wt" + Bytes.byteArrayToString(new byte[]{(byte) 0xff})));
        assertNull(Bech32.verify("A1G7SGD8"));
        assertNull(Bech32.verify("10a06t8"));
        assertNull(Bech32.verify("1qzzfhee"));
    }

    @Test
    void verifyFalseM() {
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x20}) + "1xj0phk"));
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x7F}) + "1g6xzxy"));
        assertNull(Bech32.verify(Bytes.byteArrayToString(new byte[]{(byte) 0x80}) + "1vctc34"));
        assertNull(Bech32.verify("an84characterslonghumanreadablepartthatcontainsthetheexcludedcharactersbioandnumber11d6pts4"));
        assertNull(Bech32.verify("qyrz8wqd2c9m"));
        assertNull(Bech32.verify("1qyrz8wqd2c9m"));
        assertNull(Bech32.verify("y1b0jsk6g"));
        assertNull(Bech32.verify("lt1igcx5c0"));
        assertNull(Bech32.verify("in1muywd"));
        assertNull(Bech32.verify("mm1crxm3i"));
        assertNull(Bech32.verify("au1s5cgom"));
        assertNull(Bech32.verify("M1VUXWEZ"));
        assertNull(Bech32.verify("16plkw9"));
        assertNull(Bech32.verify("1p2gdwpf"));
    }

    @Test
    void decode() {
        var bech32 = Bech32.decode("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4");
        assertEquals("bc", bech32.getHrp());
        assertArrayEquals(new byte[]{0, 14, 20, 15, 7, 13, 26, 0, 25, 18, 6, 11, 13, 8, 21, 4, 20, 3, 17, 2, 29, 3, 12, 29, 3, 4, 15, 24, 20, 6, 14, 30, 22}, bech32.getDataBytes());
        assertSame(Bech32.Encoding.BECH32, bech32.getEncoding());
    }

    @Test
    void encode() {
        var bech32 = Bech32.encode("bc", new byte[]{0, 14, 20, 15, 7, 13, 26, 0, 25, 18, 6, 11, 13, 8, 21, 4, 20, 3, 17, 2, 29, 3, 12, 29, 3, 4, 15, 24, 20, 6, 14, 30, 22}, Bech32.Encoding.BECH32);
        assertEquals("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4", bech32);
    }

    @Test
    void decodeSegwit() {
        assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6", Bech32.decodeSegwit("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4"));
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", Bech32.decodeSegwit("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC1SW50QA3JX3S"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj"));
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
    void decodeSegwitM() {
        assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6", Bech32.decodeSegwit("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4"));
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", Bech32.decodeSegwit("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7"));
        assertEquals("5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", Bech32.decodeSegwit("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kt5nd6y"));
        assertEquals("6002751e", Bech32.decodeSegwit("BC1SW50QGDZ25J"));
        assertEquals("5210751e76e8199196d454941c45d1b3a323", Bech32.decodeSegwit("bc1zw508d6qejxtdg4y5r3zarvaryvaxxpcs"));
        assertEquals("0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", Bech32.decodeSegwit("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy"));
        assertEquals("5120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", Bech32.decodeSegwit("tb1pqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesf3hn0c"));
        assertEquals("512079be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", Bech32.decodeSegwit("bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqzk5jj0"));

        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vq5zuyut"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqh2y7hd"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1z0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqglt7rf"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC1S0XLXVLHEMJA6C4DQV22UAPCTQUPFHLXM9H8Z3K2E72Q4K9HCZ7VQ54WELL"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kemeawh"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1q0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vq24jc47"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1p38j9r5y49hruaue7wxjce0updqjuyyx0kh56v8s25huc6995vvpql3jow4"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC130XLXVLHEMJA6C4DQV22UAPCTQUPFHLXM9H8Z3K2E72Q4K9HCZ7VQ7ZWS8R"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1pw5dgrnzv"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7v8n0nx0muaewav253zgeav"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vq47Zagq"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7v07qwwzcrf"));
        assertThrowsExactly(IllegalArgumentException.class, () -> Bech32.decodeSegwit("tb1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vpggkg4j"));
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