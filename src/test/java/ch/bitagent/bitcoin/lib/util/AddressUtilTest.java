package ch.bitagent.bitcoin.lib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressUtilTest {

    @Test
    void isInvoiceAddress() {
        assertTrue(AddressUtil.isInvoiceAddress("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"));
        assertTrue(AddressUtil.isInvoiceAddress("3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy"));
        assertTrue(AddressUtil.isInvoiceAddress("bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq"));
    }

    @Test
    void isP2pkhAddress() {
        assertTrue(AddressUtil.isP2pkhAddress("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem"));
        assertTrue(AddressUtil.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i"));
        assertTrue(AddressUtil.isP2pkhAddress("1Q1pE5vPGEEMqRcVRMbtBK842Y6Pzo6nK9"));

        assertFalse(AddressUtil.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62j"));
        assertFalse(AddressUtil.isP2pkhAddress("1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62X"));
        assertFalse(AddressUtil.isP2pkhAddress("1ANNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i"));
    }

    @Test
    void isP2shAddress() {
        assertTrue(AddressUtil.isP2shAddress("3P14159f73E4gFr7JterCCQh9QjiTjiZrG"));
        assertTrue(AddressUtil.isP2shAddress("39RF6JqABiHdYHkfChV6USGMe6Nsr66Gzw"));
        assertTrue(AddressUtil.isP2shAddress("38Kp8nfN4oZ7M97TTdidPygorZQxSw172d"));

        assertFalse(AddressUtil.isP2shAddress("38Kp8nfN4oZ7M97TTdidPygorZQxSw172e"));
    }

    @Test
    void isSegwitAddress() {
        assertTrue(AddressUtil.isSegwitAddress("bc1qc7slrfxkknqcq2jevvvkdgvrt8080852dfjewde450xdlk4ugp7szw5tk9"));
        assertTrue(AddressUtil.isSegwitAddress("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"));
    }

    @Test
    void isNotInvoiceAddressLength() {
        assertTrue(AddressUtil.isNotInvoiceAddressLength(null));
        assertTrue(AddressUtil.isNotInvoiceAddressLength("1234567890123456789012345"));
        assertTrue(AddressUtil.isNotInvoiceAddressLength("123456789012345678901234567890123456"));

        assertFalse(AddressUtil.isNotInvoiceAddressLength("12345678901234567890123456"));
        assertFalse(AddressUtil.isNotInvoiceAddressLength("12345678901234567890123456789012345"));
    }
}