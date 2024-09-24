package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetHeadersMessageTest {

    @Test
    void serialize() {
        var blockHex = Hex.parse("0000000000000000001237f46acddf58578a37e213d2a6edc4884a2fcad05ba3");
        var gh = new GetHeadersMessage(null, null, blockHex.toBytes(), null);
        assertEquals("7f11010001a35bd0ca2f4a88c4eda6d213e2378a5758dfcd6af437120000000000000000000000000000000000000000000000000000000000000000000000000000000000", Hex.parse(gh.serialize()).toString());
    }
}