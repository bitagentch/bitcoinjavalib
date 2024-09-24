package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkEnvelopeTest {

    @Test
    void parse() {
        var msg = Hex.parse("f9beb4d976657261636b000000000000000000005df6e0e2");
        var stream = new ByteArrayInputStream(msg.toBytes());
        var envelope = NetworkEnvelope.parse(stream, null);
        assertTrue(envelope.isCommand(VerAckMessage.COMMAND));
        assertArrayEquals("".getBytes(), envelope.getPayload());
        msg = Hex.parse("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        stream = new ByteArrayInputStream(msg.toBytes());
        envelope = NetworkEnvelope.parse(stream, null);
        assertTrue(envelope.isCommand(VersionMessage.COMMAND));
        assertArrayEquals(Arrays.copyOfRange(msg.toBytes(), 24, msg.toBytes().length), envelope.getPayload());
    }

    @Test
    void serialize() {
        var msg = Hex.parse("f9beb4d976657261636b000000000000000000005df6e0e2");
        var stream = new ByteArrayInputStream(msg.toBytes());
        var envelope = NetworkEnvelope.parse(stream, null);
        assertArrayEquals(msg.toBytes(), envelope.serialize());
        msg = Hex.parse("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        stream = new ByteArrayInputStream(msg.toBytes());
        envelope = NetworkEnvelope.parse(stream, null);
        assertArrayEquals(msg.toBytes(), envelope.serialize());
    }
}