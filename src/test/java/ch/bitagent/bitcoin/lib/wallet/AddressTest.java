package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.network.Electrum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void isInvoiceAddress() {
        assertTrue(Address.parse("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2").isInvoiceAddress());
        assertTrue(Address.parse("3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy").isInvoiceAddress());
        assertTrue(Address.parse("bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq").isInvoiceAddress());
        assertTrue(Address.parse("bc1p5d7rjq7g6rdk2yhzks9smlaqtedr4dekq08ge8ztwac72sfr9rusxg3297").isInvoiceAddress());
    }

    @Test
    void isP2pkhAddress() {
        assertTrue(Address.parse("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem").isP2pkhAddress());
        assertTrue(Address.parse("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i").isP2pkhAddress());
        assertTrue(Address.parse("1Q1pE5vPGEEMqRcVRMbtBK842Y6Pzo6nK9").isP2pkhAddress());

        assertFalse(Address.parse("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j").isP2pkhAddress());
        assertFalse(Address.parse("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62X").isP2pkhAddress());
        assertFalse(Address.parse("1ANNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i").isP2pkhAddress());
    }

    @Test
    void isP2shAddress() {
        assertTrue(Address.parse("3P14159f73E4gFr7JterCCQh9QjiTjiZrG").isP2shAddress());
        assertTrue(Address.parse("39RF6JqABiHdYHkfChV6USGMe6Nsr66Gzw").isP2shAddress());
        assertTrue(Address.parse("38Kp8nfN4oZ7M97TTdidPygorZQxSw172d").isP2shAddress());

        assertFalse(Address.parse("38Kp8nfN4oZ7M97TTdidPygorZQxSw172e").isP2shAddress());
    }

    @Test
    void isBech32Address() {
        assertTrue(Address.parse("bc1qc7slrfxkknqcq2jevvvkdgvrt8080852dfjewde450xdlk4ugp7szw5tk9").isBech32Address());
        assertTrue(Address.parse("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4").isBech32Address());
    }

    @Test
    void isNotInvoiceAddressLength() {
        assertTrue(Address.parse(null).isNotInvoiceAddressLength());
        assertTrue(Address.parse("1234567890123456789012345").isNotInvoiceAddressLength());
        assertTrue(Address.parse("123456789012345678901234567890123456").isNotInvoiceAddressLength());

        assertFalse(Address.parse("12345678901234567890123456").isNotInvoiceAddressLength());
        assertFalse(Address.parse("12345678901234567890123456789012345").isNotInvoiceAddressLength());
    }

    @Test
    void electrumScripthashP2pkh() {
        var address = Address.parse("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        assertTrue(address.isP2pkhAddress());
        var scripthash = address.electrumScripthash();
        assertEquals("8b01df4e368ea28f8dc0423bcf7a4923e3a12d307c875e47a0cfbf90b5c39161", scripthash);
        var electrum = new Electrum();
        var history = electrum.getHistory(scripthash);
        assertFalse(history.isEmpty());
        var balance = electrum.getBalance(scripthash);
        assertTrue(balance.getLong("unconfirmed") >= 0);
        assertTrue(balance.getLong("confirmed") > 0);
        var mempool = electrum.getMempool(scripthash);
        assertFalse(mempool.isEmpty());
        var unspentList = electrum.listUnspent(scripthash);
        assertFalse(unspentList.isEmpty());
    }

    @Test
    void electrumScripthashBech32() {
        var address = Address.parse("bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu");
        assertTrue(address.isBech32Address());
        var scripthash = address.electrumScripthash();
        var electrum = new Electrum();
        var history = electrum.getHistory(scripthash);
        assertEquals(120, history.length());
        var balance = electrum.getBalance(scripthash);
        assertEquals(0L, balance.getLong("unconfirmed"));
        assertEquals(0L, balance.getLong("confirmed"));
        var mempool = electrum.getMempool(scripthash);
        assertTrue(mempool.isEmpty());
        var unspentList = electrum.listUnspent(scripthash);
        assertTrue(unspentList.isEmpty());
    }
}