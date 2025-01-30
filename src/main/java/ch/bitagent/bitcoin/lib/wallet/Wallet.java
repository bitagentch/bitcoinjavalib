package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import ch.bitagent.bitcoin.lib.network.Electrum;
import ch.bitagent.bitcoin.lib.tx.Utxo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Wallet {

    private static final Logger log = Logger.getLogger(Wallet.class.getSimpleName());

    public static final int PURPOSE_NATIVE_SEGWIT = 84;
    public static final int COIN_TYPE_BITCOIN = 0;

    private final ExtendedKey extendedKey;
    private final int gapLimit;

    private final List<Address> addressList0 = new ArrayList<>();
    private final List<Address> addressList1 = new ArrayList<>();
    private final List<Utxo> utxoList = new ArrayList<>();

    public static String createMnemonic(int entropyStrength) {
        var entropy = MnemonicSentence.generateEntropy(entropyStrength);
        return MnemonicSentence.entropyToMnemonic(entropy);
    }

    public Wallet(ExtendedKey extendedKey, int gapLimit) {
        if (Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPRV.toBytes()) != 0
                && Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPUB.toBytes()) != 0) {
            throw new IllegalArgumentException("Prefix not supported");
        }
        if (gapLimit < 1) {
            throw new IllegalArgumentException("Minimal gap limit is 1");
        }
        this.extendedKey = extendedKey;
        this.gapLimit = gapLimit;
        this.addressList0.addAll(deriveAddresses(extendedKey, 0, 0, gapLimit - 1));
        this.addressList1.addAll(deriveAddresses(extendedKey, 1, 0, gapLimit - 1));
    }

    public static Wallet parse(ExtendedKey extendedKey, int gapLimit) {
        return new Wallet(extendedKey, gapLimit);
    }

    public static Wallet parse(String mnemonicSentence, String passphrase, int purpose, int coinType, int account, int gapLimit) {
        if (purpose != PURPOSE_NATIVE_SEGWIT) {
            throw new IllegalArgumentException("Purpose not supported!");
        }
        if (coinType != COIN_TYPE_BITCOIN) {
            throw new IllegalArgumentException("Coin type not supported!");
        }
        if (account < 0) {
            throw new IllegalArgumentException("Account not supported!");
        }
        var seed = MnemonicSentence.mnemonicToSeed(mnemonicSentence, passphrase);
        var zprv = MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_ZPRV);
        var m = ExtendedKey.parse(zprv);
        var m84h0h0h = m.derive(purpose, true, false)
                .derive(coinType, true, false)
                .derive(account, true, false);
        return new Wallet(m84h0h0h, gapLimit);
    }

    public static List<Address> deriveAddresses(ExtendedKey extendedKey, int change, int indexFrom, int indexTo) {
        log.fine(String.format("derive %s/%s/%s", change, indexFrom, indexTo));
        var extendedKeyChange = extendedKey.derive(change);
        List<Address> addressList = new ArrayList<>();
        for (int i = indexFrom; i <= indexTo; i++) {
            var extendedKeyi = extendedKeyChange.derive(i);
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
        return addressList;
    }

    public void history(int change) {
        var electrum = new Electrum();
        int gap = 0;
        var addressList = getAddressList0();
        if (change == 0) {
            // NOP
        } else if (change == 1) {
            addressList = getAddressList1();
        } else {
            throw new IllegalArgumentException("Invalid change");
        }
        for (int i = 0; i < addressList.size(); i++) {
            var address = addressList.get(i);
            var scripthash = address.electrumScripthash();
            var history = electrum.getHistory(scripthash);
            if (history.isEmpty()) {
                gap++;
            }
            if (i == addressList.size() - 1 && gap < gapLimit) {
                int indexFrom = i + 1;
                int indexTo = i + gapLimit - gap;
                var derviedAddresses = deriveAddresses(this.extendedKey, change, indexFrom, indexTo);
                addressList.addAll(derviedAddresses);
            }
            if (history.isEmpty()) {
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
                    for (int j = 0; j < unspent.length(); j++) {
                        utxoList.add(new Utxo(unspent.getJSONObject(j), address.getChangeIndex()));
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
        for (Utxo utxo : utxoList) {
            string.append("\n");
            string.append(utxo);
        }
        return string.toString();
    }
}
