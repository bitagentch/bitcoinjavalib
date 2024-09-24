package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.helper.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleNodeTest {

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void handshake() {
        var node = new SimpleNode(Properties.getBitcoinP2pHost(), Properties.getBitcoinP2pPort(), Properties.getBitcoinP2pTestnet(), false);
        assertNotNull(node.handshake());
        node.close();
    }
}