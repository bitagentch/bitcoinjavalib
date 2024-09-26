package ch.bitagent.bitcoin.lib.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesTest {

    @Test
    void getBlockstreamMainnetUrl() {
        assertEquals("https://blockstream.info/api", Properties.getBlockstreamMainnetUrl());
    }

    @Test
    void getBlockstreamTestnetUrl() {
        assertEquals("https://blockstream.info/testnet/api", Properties.getBlockstreamTestnetUrl());
    }
}