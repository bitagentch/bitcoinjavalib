package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.block.Block;
import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Properties;
import ch.bitagent.bitcoin.lib.network.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class Chapter10Test {

    private static final Logger log = Logger.getLogger(Chapter10Test.class.getSimpleName());

    @Test
    void exercise2() {
        var messageHex = Hex.parse("f9beb4d976657261636b000000000000000000005df6e0e2");
        var stream = new ByteArrayInputStream(messageHex.toBytes());
        var envelope = NetworkEnvelope.parse(stream, null);
        assertTrue(envelope.isCommand(VerAckMessage.COMMAND));
        assertArrayEquals("".getBytes(), envelope.getPayload());
    }

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void example1() {
        var host = Properties.getBitcoinP2pHost();
        var port = Properties.getBitcoinP2pPort();
        var testnet = Properties.getBitcoinP2pTestnet();
        try (var socket = new Socket(host, port)) {
            log.fine(socket.toString());
            var socketWriter = new DataOutputStream(socket.getOutputStream());
            var socketReader = new DataInputStream(socket.getInputStream());
            var versionMessage = new VersionMessage();
            var networkEnvelope = new NetworkEnvelope(versionMessage.getCommand(), versionMessage.serialize(), testnet);
            log.fine(networkEnvelope.toString());
            socketWriter.write(networkEnvelope.serialize());
            var response = new ByteArrayOutputStream();
            response.write(socketReader.read());
            var available = socketReader.available();
            while (available > 0) {
                response.write(socketReader.readNBytes(available));
                available = socketReader.available();
            }
            var newMessage = NetworkEnvelope.parse(new ByteArrayInputStream(response.toByteArray()), testnet);
            log.fine(String.format("newMessage '%s'", newMessage));
            assertEquals(testnet, newMessage.getTestnet());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void example2() {
        var node = new SimpleNode(Properties.getBitcoinP2pHost(), Properties.getBitcoinP2pPort(), Properties.getBitcoinP2pTestnet(), false);
        var handshake = node.handshake();
        if (handshake != null) {
            var handshakeAnswer = SendCompactMessage.parse(new ByteArrayInputStream(handshake));
            log.fine(handshakeAnswer.toString());
        }
        var genesis = Block.parse(new ByteArrayInputStream(Block.GENESIS_BLOCK.toBytes()));
        var getheaders = new GetHeadersMessage(null, null, genesis.hash(), null);
        node.send(getheaders);
        var payload = node.waitFor(Set.of(HeadersMessage.COMMAND));
        if (payload == null) {
            Assertions.fail("no headers");
        }
        node.close();
    }

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void example3() {
        var previous = Block.parse(new ByteArrayInputStream(Block.GENESIS_BLOCK.toBytes()));
        if (Properties.getBitcoinP2pTestnet()) {
            previous = Block.parse(new ByteArrayInputStream(Block.TESTNET_GENESIS_BLOCK.toBytes()));
        }
        var firstEpochTimestamp = previous.getTimestamp();
        var count = 1;
        var expectedBits = Block.LOWEST_BITS.toBytes();
        var node = new SimpleNode(Properties.getBitcoinP2pHost(), Properties.getBitcoinP2pPort(), Properties.getBitcoinP2pTestnet(), false);
        node.handshake();
        for (int i = 0; i < 19; i++) {
            var getheaders = new GetHeadersMessage(null, null, previous.hash(), null);
            node.send(getheaders);
            var headers = node.waitFor(Set.of(HeadersMessage.COMMAND));
            if (headers == null) {
                Assertions.fail("no headers");
            }
            var headersMessage = HeadersMessage.parse(new ByteArrayInputStream(headers.getPayload()));
            for (Block block: headersMessage.getBlocks()) {
                if (!block.checkPow()) {
                    throw new IllegalStateException(String.format("bad PoW at block %s", count));
                }
                if (!Arrays.equals(block.getPrevBlock(), previous.hash())) {
                    throw new IllegalStateException(String.format("discontinuous block at %s", count));
                }
                if (Boolean.FALSE.equals(Properties.getBitcoinP2pTestnet())) {
                    if (count % 2016 == 0) {
                        var timeDiff = previous.getTimestamp().sub(firstEpochTimestamp);
                        expectedBits = Bytes.calculateNewBits(previous.getBits(), timeDiff);
                        log.fine(Hex.parse(expectedBits).toString());
                        firstEpochTimestamp = block.getTimestamp();
                    }
                    if (!Arrays.equals(block.getBits(), expectedBits)) {
                        throw new IllegalStateException(String.format("bad bits at block %s", count));
                    }
                }
                previous = block;
                count++;
            }
        }
        node.close();
    }
}
