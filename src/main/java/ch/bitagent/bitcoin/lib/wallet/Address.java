package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bech32;
import ch.bitagent.bitcoin.lib.helper.Bytes;

/**
 * Address
 */
public class Address {

    private final String addressString;

    public Address(String address) {
        this.addressString = address;
    }

    public static Address parse(String address) {
        return new Address(address);
    }

    /**
     * <p>Is it an invoice address?</p>
     * <a href="https://en.bitcoin.it/wiki/Invoice_address">Invoice address</a>
     *
     * @return .
     */
    public boolean isInvoiceAddress() {
        if (isP2pkhAddress()) {
            return true;
        } else if (isP2shAddress()) {
            return true;
        } else {
            return isBech32Address();
        }
    }

    /**
     * <p>Is it a pay-to-pubkey-hash address?</p>
     * <a href="https://en.bitcoin.it/wiki/Transaction#Pay-to-PubkeyHash">Pay-to-PubkeyHash</a>
     *
     * @return .
     */
    public boolean isP2pkhAddress() {
        if (isNotInvoiceAddressLength()) {
            return false;
        }
        try {
            if (addressString.startsWith("1")) {
                Base58.decodeAddress(addressString);
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
     * @return .
     */
    public boolean isP2shAddress() {
        if (isNotInvoiceAddressLength()) {
            return false;
        }
        try {
            if (addressString.startsWith("3")) {
                Base58.decodeAddress(addressString);
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
     * @return .
     */
    public boolean isBech32Address() {
        if (isNotInvoiceAddressLength()) {
            return false;
        }
        try {
            if (addressString.startsWith("bc")) {
                Bech32.decodeSegwit(addressString);
                return true;
            }
        } catch (Exception e) {
            // NOP
        }
        return false;
    }

    boolean isNotInvoiceAddressLength() {
        if (addressString == null) {
            return true;
        } else if (addressString.startsWith("1") || addressString.startsWith("3")) {
            if (addressString.length() < 26) {
                return true;
            } else {
                return addressString.length() > 35;
            }
        } else if (addressString.startsWith("bc")) {
            if (addressString.length() < 14) {
                return true;
            } else {
                return addressString.length() > 74;
            }
        } else {
            return true;
        }
    }

    public String address() {
        return addressString;
    }

    public byte[] hash160() {
        if (this.isP2pkhAddress() || this.isP2shAddress()) {
            return Base58.decodeAddress(this.addressString);
        } else if (this.isBech32Address()) {
            var scriptPubkey = Bech32.decodeSegwit(this.addressString);
            return Bytes.hexStringToByteArray(scriptPubkey.substring(4));
        } else {
            throw new IllegalStateException();
        }
    }
}