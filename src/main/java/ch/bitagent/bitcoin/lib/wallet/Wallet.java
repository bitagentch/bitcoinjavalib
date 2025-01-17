package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import ch.bitagent.bitcoin.lib.network.Electrum;
import ch.bitagent.bitcoin.lib.tx.Utxo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wallet {

    private final ExtendedKey extendedKey;

    private final List<Address> addressList0 = new ArrayList<>();
    private final List<Address> addressList1 = new ArrayList<>();
    private final List<Utxo> utxoList = new ArrayList<>();

    public static String createMnemonic(int entropyStrength) {
        var entropy = MnemonicSentence.generateEntropy(entropyStrength);
        return MnemonicSentence.entropyToMnemonic(entropy);
    }

    public Wallet(ExtendedKey extendedKey) {
        if (Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPRV.toBytes()) != 0
                && Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPUB.toBytes()) != 0) {
            throw new IllegalArgumentException("Prefix not supported");
        }
        this.extendedKey = extendedKey;

        var extendedKey0 = this.extendedKey.derive(0);
        deriveAddresses(extendedKey0, 0, 0, 9, this.addressList0);

        var extendedKey1 = this.extendedKey.derive(1);
        deriveAddresses(extendedKey1, 1, 0, 9, this.addressList1);
    }

    public static Wallet parse(ExtendedKey extendedKey) {
        return new Wallet(extendedKey);
    }

    public static Wallet parse(String mnemonicSentence, String passphrase) {
        var seed = MnemonicSentence.mnemonicToSeed(mnemonicSentence, passphrase);
        var zprv = MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_ZPRV);
        var m = ExtendedKey.parse(zprv);
        var m84h0h0h = m.derive(84, true, false)
                .derive(0, true, false)
                .derive(0, true, false);
        return new Wallet(m84h0h0h);
    }

    private void deriveAddresses(ExtendedKey extendedKey, int change, int indexFrom, int indexTo, List<Address> addressList) {
        for (int i = indexFrom; i <= indexTo; i++) {
            var extendedKeyi = extendedKey.derive(i);
            S256Point publicKeyi;
            if (ExtendedKey.isKeyPrivate(extendedKeyi.getPrefix())) {
                publicKeyi = PrivateKey.parse(extendedKeyi.getKey()).getPoint();
            } else {
                publicKeyi = S256Point.parse(extendedKeyi.getKey());
            }
            var addressi = Address.parse(publicKeyi.addressBech32P2wpkh(false));
            addressi.setChangeIndex(new AddressChangeIndex(change, i));
            addressList.add(addressi);
        }
    }

    public void history() {
        var electrum = new Electrum();
        List<Address> addressList = new ArrayList<>();
        addressList.addAll(addressList0);
        addressList.addAll(addressList1);
        for (Address address : addressList) {
            var scripthash = address.electrumScripthash();
            var history = electrum.getHistory(scripthash);
            if (history == null) {
                continue;
            }
            address.setHistoryCount(history.length());
            if (!history.isEmpty()) {
                var balance = electrum.getBalance(scripthash);
                if (balance == null) {
                    continue;
                }
                address.setUnconfirmed(balance.getLong("unconfirmed"));
                address.setConfirmed(balance.getLong("confirmed"));
                if (address.getUnconfirmed() > 0 || address.getConfirmed() > 0) {
                    var unspent = electrum.listUnspent(address.electrumScripthash());
                    for (int i = 0; i < unspent.length(); i++) {
                        utxoList.add(new Utxo(unspent.getJSONObject(i), address.getChangeIndex()));
                    }
                }
            }
        }
    }

    public Address nextReceiveAddress() {
        for (Address address : addressList0) {
            if (address.getHistoryCount() == 0) {
                return address;
            }
        }
        return null;
    }

    public Address nextChangeAddress() {
        for (Address address : addressList1) {
            if (address.getHistoryCount() == 0) {
                return address;
            }
        }
        return null;
    }

    public PrivateKey getPrivateKeyForChangeIndex(AddressChangeIndex changeIndex) {
        if (ExtendedKey.isKeyPrivate(this.extendedKey.getPrefix())) {
            return PrivateKey.parse(this.extendedKey.derive(changeIndex.getChange()).derive(changeIndex.getIndex()).getKey());
        } else {
            throw new IllegalArgumentException("Private key is not available");
        }
    }

    public String signMessage(String address, String message) {
        if (ExtendedKey.isKeyPrivate(this.extendedKey.getPrefix())) {
            for (int i = 0; i < addressList0.size(); i++) {
                if (addressList0.get(i).getAddressString().equals(address)) {
                    var extendedKey0i = this.extendedKey.derive(0).derive(i);
                    var privateKey0i = PrivateKey.parse(extendedKey0i.getKey());
                    return Message.sign(privateKey0i, message, Address.BECH32, true);
                }
            }
            throw new IllegalArgumentException("Address not found");
        } else {
            throw new IllegalArgumentException("Sign message with a public key not possible");
        }
    }

    public boolean verifyMessage(String address, String signature, String message) {
        for (int i = 0; i < addressList0.size(); i++) {
            if (addressList0.get(i).getAddressString().equals(address)) {
                var extendedKey0i = this.extendedKey.derive(0).derive(i);
                S256Point publicKey0i;
                if (ExtendedKey.isKeyPrivate(this.extendedKey.getPrefix())) {
                    publicKey0i = PrivateKey.parse(extendedKey0i.getKey()).getPoint();
                } else {
                    publicKey0i = S256Point.parse(extendedKey0i.getKey());
                }
                return Message.verify(publicKey0i, signature, message, true);
            }
        }
        throw new IllegalArgumentException("Address not found");
    }

    public ExtendedKey getExtendedKey() {
        return extendedKey;
    }

    public List<Address> getAddressList0() {
        return addressList0;
    }

    public List<Address> getAddressList1() {
        return addressList1;
    }

    public List<Utxo> getUtxoList() {
        return utxoList;
    }

    @Override
    public String toString() {
        var string = new StringBuilder();
        List<Address> addressList = new ArrayList<>();
        addressList.addAll(addressList0);
        addressList.addAll(addressList1);
        for (Address address : addressList) {
            string.append("\n");
            string.append(address);
        }
        return string.toString();
    }
}
