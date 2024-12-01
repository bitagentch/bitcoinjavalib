package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bech32;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.script.Script;

import java.util.Objects;

/**
 * Address
 */
public class Address {

    public static final String P2PKH = "p2pkh";
    public static final String P2SH = "p2sh";
    public static final String BECH32 = "bech32";

    private final String addressString;

    private int change = -1;
    private int addressIndex = -1;
    private int historyCount = 0;
    private long balance = 0l;

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

    /**
     * https://electrumx-spesmilo.readthedocs.io/en/latest/protocol-basics.html#script-hashes
     */
    public String electrumScripthash() {
        if (this.isP2pkhAddress()) {
            var hash160 = Base58.decodeAddress(this.addressString);
            var script = Script.p2pkhScriptPubkey(hash160);
            var hash = Hash.sha256(Bytes.hexStringToByteArray(script.toHex()));
            return Bytes.byteArrayToHexString(Bytes.changeOrder(hash));
        } else if (this.isP2shAddress()) {
            var hash160 = Base58.decodeAddress(this.addressString);
            var script = Script.p2shScriptPubkey(hash160);
            var hash = Hash.sha256(Bytes.hexStringToByteArray(script.toHex()));
            return Bytes.byteArrayToHexString(Bytes.changeOrder(hash));
        } else if (this.isBech32Address()) {
            var scriptPubkey = Bech32.decodeSegwit(this.addressString);
            var hash = Hash.sha256(Bytes.hexStringToByteArray(scriptPubkey));
            return Bytes.byteArrayToHexString(Bytes.changeOrder(hash));
        } else {
            throw new IllegalStateException();
        }
    }

    public String getAddressString() {
        return addressString;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public int getAddressIndex() {
        return addressIndex;
    }

    public void setAddressIndex(int addressIndex) {
        this.addressIndex = addressIndex;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(int historyCount) {
        this.historyCount = historyCount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressString, address.addressString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(addressString);
    }

    @Override
    public String toString() {
        if (change >= 0 && addressIndex >= 0) {
            return String.format("/%s/%s/%s/%s/%s", change, addressIndex, addressString, historyCount, balance);
        } else {
            return addressString;
        }
    }
}
