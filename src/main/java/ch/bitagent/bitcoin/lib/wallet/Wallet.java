package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wallet {

    private ExtendedKey extendedKey;

    private List<Address> addressList0 = new ArrayList<>();
    private List<Address> addressList1 = new ArrayList<>();

    public Wallet(ExtendedKey extendedKey) {
        if (Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPRV.toBytes()) != 0
                && Arrays.compare(extendedKey.getPrefix(), ExtendedKey.PREFIX_ZPUB.toBytes()) != 0) {
            throw new IllegalArgumentException("Prefix not supported");
        }
        this.extendedKey = extendedKey;

        var extendedKey0 = this.extendedKey.derive(0);
        deriveAddresses(extendedKey0, 0, 0, 19, this.addressList0);

        var extendedKey1 = this.extendedKey.derive(1);
        deriveAddresses(extendedKey1, 1, 0, 9, this.addressList1);
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
            addressi.setChange(change);
            addressi.setAddressIndex(i);
            addressList.add(addressi);
        }
    }

    public static Wallet parse(ExtendedKey extendedKey) {
        return new Wallet(extendedKey);
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

    @Override
    public String toString() {
        var string = new StringBuilder();
        for (Address address : addressList0) {
            string.append("\n");
            string.append(address);
        }
        for (Address address : addressList1) {
            string.append("\n");
            string.append(address);
        }
        return string.toString();
    }
}
