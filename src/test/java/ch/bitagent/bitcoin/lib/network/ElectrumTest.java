package ch.bitagent.bitcoin.lib.network;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElectrumTest {

    @Test
    void pingDefault() {
        var electrum = new Electrum();
        electrum.defaultSocket();
        var json = electrum.ping().get(0);
        assertEquals("bitcoinjavalib", json.getString("id"));
        assertEquals("2.0", json.getString("jsonrpc"));
        assertTrue(json.isNull("result"));
    }

    @Test
    void pingAll() {
        var electrum = new Electrum();
        electrum.allSockets();
        var ping = electrum.ping();
        assertEquals(electrum.getSockets().size(), ping.size());
        for (JSONObject pong : ping) {
            assertEquals("bitcoinjavalib", pong.getString("id"));
            assertEquals("2.0", pong.getString("jsonrpc"));
            assertTrue(pong.isNull("result"));
        }
    }

    @Test
    void features() {
        var electrum = new Electrum();
        electrum.allSockets();
        var features = electrum.features();
        assertEquals(electrum.getSockets().size(), features.size());
        for (JSONObject feature : features) {
            assertEquals("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f", feature.getString("genesis_hash"));
            assertEquals("sha256", feature.getString("hash_function"));
            assertEquals("1.4", feature.getString("protocol_min"));
            assertTrue(feature.isNull("pruning"));
        }
    }

    @Test
    void version() {
        var electrum = new Electrum();
        electrum.allSockets();
        var versions = electrum.versions();
        assertEquals(electrum.getSockets().size(), versions.size());
    }

    @Test
    void peers() {
        var electrum = new Electrum();
        var peers = electrum.peers();
        assertFalse(peers.isEmpty());
    }

    @Test
    void height() {
        var electrum = new Electrum();
        var height = electrum.height();
        assertNotNull(height);
    }

    @Test
    void invalidScripthash() {
        var electrum = new Electrum();
        assertNull(electrum.getHistory("bla"));
        assertNull(electrum.getBalance("bla"));
        assertNull(electrum.getMempool("bla"));
        assertNull(electrum.listUnspent("bla"));
    }

    @Test
    void estimateFee() {
        var electrum = new Electrum();
        var fee = electrum.estimateFee(0);
        assertNull(fee);
        fee = electrum.estimateFee(1);
        assertTrue(fee > 0);
    }

    @Test
    void invalidTxHash() {
        var electrum = new Electrum();
        var tx = electrum.getTransaction("bla");
        assertNull(tx);
        var txVerbose = electrum.getTransactionVerbose("bla");
        assertNull(txVerbose);
    }

    @Test
    void invalidTransaction() {
        var electrum = new Electrum();
        var txHash = electrum.broadcastTransaction("bla");
        assertNull(txHash);
    }
}