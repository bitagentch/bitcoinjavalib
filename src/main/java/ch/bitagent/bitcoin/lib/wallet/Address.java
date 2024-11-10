package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bech32;

/**
 * AddressUtil
 */
public class Address {

    private Address() {}

    /**
     * <p>Is it an invoice address?</p>
     * <a href="https://en.bitcoin.it/wiki/Invoice_address">Invoice address</a>
     *
     * @param address .
     * @return .
     */
    public static boolean isInvoiceAddress(String address) {
        if (isP2pkhAddress(address)) {
            return true;
        } else if (isP2shAddress(address)) {
            return true;
        } else {
            return isBech32Address(address);
        }
    }

    /**
     * <p>Is it a pay-to-pubkey-hash address?</p>
     * <a href="https://en.bitcoin.it/wiki/Transaction#Pay-to-PubkeyHash">Pay-to-PubkeyHash</a>
     *
     * @param address .
     * @return .
     */
    public static boolean isP2pkhAddress(String address) {
        if (isNotInvoiceAddressLength(address)) {
            return false;
        }
        try {
            if (address.startsWith("1")) {
                Base58.decodeAddress(address);
                return true;
            }
        } catch (Exception e) {
            // NOP
        }
        return false;
    }

    /**
     * <p>Is it a pay-to-script-hash address?</p>
     * <a href="https://en.bitcoin.it/wiki/Pay_to_script_hash">Pay to script hash</a>
     *
     * @param address .
     * @return .
     */
    public static boolean isP2shAddress(String address) {
        if (isNotInvoiceAddressLength(address)) {
            return false;
        }
        try {
            if (address.startsWith("3")) {
                Base58.decodeAddress(address);
                return true;
            }
        } catch (Exception e) {
            // NOP
        }
        return false;
    }

    /**
     * <p>Is it a bech32 address?</p>
     * <a href="https://en.bitcoin.it/wiki/Bech32">Bech32</a>
     *
     * @param address .
     * @return .
     */
    public static boolean isBech32Address(String address) {
        if (isNotInvoiceAddressLength(address)) {
            return false;
        }
        try {
            if (address.startsWith("bc")) {
                Bech32.decodeSegwit(address);
                return true;
            }
        } catch (Exception e) {
            // NOP
        }
        return false;
    }

    static boolean isNotInvoiceAddressLength(String address) {
        if (address == null) {
            return true;
        } else if (address.startsWith("1") || address.startsWith("3")) {
            if (address.length() < 26) {
                return true;
            } else {
                return address.length() > 35;
            }
        } else if (address.startsWith("bc")) {
            if (address.length() < 14) {
                return true;
            } else {
                return address.length() > 74;
            }
        } else {
            return true;
        }
    }
}
