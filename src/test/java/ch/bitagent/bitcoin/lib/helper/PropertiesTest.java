package ch.bitagent.bitcoin.lib.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTest {

    @Test
    void getBlockstreamMainnetUrl() {
        assertEquals("https://blockstream.info/api", Properties.getBlockstreamMainnetUrl());
    }

    @Test
    void getBlockstreamTestnetUrl() {
        assertEquals("https://blockstream.info/testnet/api", Properties.getBlockstreamTestnetUrl());
    }

    @Test
    void getWallets() {
        assertEquals(2, Properties.getWallets(Properties.WALLET_FILENAME).size());
        assertThrowsExactly(IllegalStateException.class, () -> Properties.getWallets("bla"));

        assertEquals(12, Properties.getWalletMnemonic(Properties.WALLET_FILENAME, 0).split(" ").length);
        assertNotNull(Properties.getWalletPassphrase(Properties.WALLET_FILENAME, 0));

        assertEquals(12, Properties.getWalletMnemonic(Properties.WALLET_FILENAME, 1).split(" ").length);
        assertNull(Properties.getWalletPassphrase(Properties.WALLET_FILENAME, 1));

        assertNull(Properties.getWalletMnemonic(Properties.WALLET_FILENAME, 9));
        assertNull(Properties.getWalletPassphrase(Properties.WALLET_FILENAME, 9));
    }
}