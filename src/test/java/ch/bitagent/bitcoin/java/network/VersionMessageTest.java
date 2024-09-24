package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionMessageTest {

    @Test
    void serialize() {
        var v = new VersionMessage(null, null, Int.parse(0),
                null, null, null,
                null, null, null,
                Hex.parse("0000000000000000").toBytes(), "/programmingbitcoin:0.1/", null, null);
        assertEquals("7f11010000000000000000000000000000000000000000000000000000000000000000000000ffff00000000208d000000000000000000000000000000000000ffff00000000208d0000000000000000182f70726f6772616d6d696e67626974636f696e3a302e312f0000000000", Hex.parse(v.serialize()).toString());
    }
}