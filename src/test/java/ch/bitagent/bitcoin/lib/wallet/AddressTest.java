package ch.bitagent.bitcoin.lib.wallet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressTest {

    @Test
    void isInvoiceAddress() {
        assertTrue(Address.isInvoiceAddress("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"));
        assertTrue(Address.isInvoiceAddress("3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy"));
        assertTrue(Address.isInvoiceAddress("bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq"));
        assertTrue(Address.isInvoiceAddress("bc1p5d7rjq7g6rdk2yhzks9smlaqtedr4dekq08ge8ztwac72sfr9rusxg3297"));
    }

    @Test
    void isP2pkhAddress() {
        assertTrue(Address.isP2pkhAddress("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem"));
        assertTrue(Address.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i"));
        assertTrue(Address.isP2pkhAddress("1Q1pE5vPGEEMqRcVRMbtBK842Y6Pzo6nK9"));

        assertFalse(Address.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j"));
        assertFalse(Address.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62X"));
        assertFalse(Address.isP2pkhAddress("1ANNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i"));
    }

    @Test
    void isP2shAddress() {
        assertTrue(Address.isP2shAddress("3P14159f73E4gFr7JterCCQh9QjiTjiZrG"));
        assertTrue(Address.isP2shAddress("39RF6JqABiHdYHkfChV6USGMe6Nsr66Gzw"));
        assertTrue(Address.isP2shAddress("38Kp8nfN4oZ7M97TTdidPygorZQxSw172d"));

        assertFalse(Address.isP2shAddress("38Kp8nfN4oZ7M97TTdidPygorZQxSw172e"));
    }

    @Test
    void isBech32Address() {
        assertTrue(Address.isBech32Address("bc1qc7slrfxkknqcq2jevvvkdgvrt8080852dfjewde450xdlk4ugp7szw5tk9"));
        assertTrue(Address.isBech32Address("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"));
    }

    @Test
    void isNotInvoiceAddressLength() {
        assertTrue(Address.isNotInvoiceAddressLength(null));
        assertTrue(Address.isNotInvoiceAddressLength("1234567890123456789012345"));
        assertTrue(Address.isNotInvoiceAddressLength("123456789012345678901234567890123456"));

        assertFalse(Address.isNotInvoiceAddressLength("12345678901234567890123456"));
        assertFalse(Address.isNotInvoiceAddressLength("12345678901234567890123456789012345"));
    }
}