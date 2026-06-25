package ch.bitagent.bitcoin.lib.network;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ElectrumTest {

    private static final Logger log = Logger.getLogger(ElectrumTest.class.getSimpleName());

    @Test
    void pingDefault() {
        assertNotNull(new Electrum());
    }

    @Test
    void pingDefaultSilent() {
        assertNotNull(new Electrum(true));
    }

    @Test
    void pingAll() {
        var electrum = new Electrum();
        var sockets = electrum.getSockets();
        var electrumTestnet = new Electrum(true, false);
        sockets.addAll(electrumTestnet.getSockets());
        for (String socket : sockets) {
            assertTrue(electrum.ping(socket));
        }
    }

    @Test
    void features() {
        var electrum = new Electrum();
        var features = electrum.features();
        assertEquals(electrum.getSockets().size(), features.size());
        var electrumTestnet = new Electrum(true, false);
        var featuresTestnet = electrumTestnet.features();
        assertEquals(electrumTestnet.getSockets().size(), featuresTestnet.size());
        features.addAll(featuresTestnet);
        for (JSONObject feature : features) {
            log.info(feature.toString(2));
            var result = feature.getJSONObject("1result");
            var genesisHash = result.getString("genesis_hash");
            assertTrue("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f".equals(genesisHash)
                    || "000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943".equals(genesisHash));
            assertEquals("sha256", result.getString("hash_function"));
            assertEquals("1.4", result.getString("protocol_min"));
            assertTrue(result.isNull("pruning"));
        }
    }

    @Test
    void version() {
        var electrum = new Electrum();
        var versions = electrum.versions();
        assertEquals(electrum.getSockets().size(), versions.size());
    }

    @Test
    void peers() {
        var electrum = new Electrum();
        var peers = electrum.peers();
        assertFalse(peers.isEmpty());
        for (Object peer : peers) {
            log.fine(peer.toString());
        }
    }

    @Test
    void headers() {
        var electrum = new Electrum();
        var headers = electrum.headers();
        var height = headers.getInt("height");
        assertTrue(height > 0);
        var timestamp = headers.getInt("timestamp");
        assertTrue(timestamp > 0);
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
        estimateFeeForNumber(electrum, 1);
        estimateFeeForNumber(electrum, 2);
        estimateFeeForNumber(electrum, 3);
        estimateFeeForNumber(electrum, 4);
        estimateFeeForNumber(electrum, 5);
        estimateFeeForNumber(electrum, 6);
    }

    private static void estimateFeeForNumber(Electrum electrum, int number) {
        Long fee = electrum.estimateFee(number);
        log.info(String.format("fee number %s -> %s sats/vB", number, fee));
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