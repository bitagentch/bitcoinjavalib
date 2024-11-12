package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Hash;

import java.util.Arrays;

/**
 * <p>ExtendedKey</p>
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0084.mediawiki">BIP-0084</a>
 */
public class ExtendedKey {

    // BIP-32
    public static final Int PREFIX_XPRV = Hex.parse("0488ADE4");
    public static final Int PREFIX_XPUB = Hex.parse("0488B21E");

    // BIP-84
    public static final Int PREFIX_ZPRV = Hex.parse("04b2430c");
    public static final Int PREFIX_ZPUB = Hex.parse("04b24746");

    private static final Int HEX_00000000 = Hex.parse("00000000");
    private static final Int HARDENED_INDEX = Hex.parse("80000000");

    private final byte[] prefix;
    private final int depth;
    private final byte[] fingerprint;
    private final byte[] childNumber;
    private final byte[] chainCode;
    private final byte[] key;

    /**
     * ExtendedKey
     *
     * @param extendedKey .
     */
    public ExtendedKey(String extendedKey) {
        var decoded = Base58.decodeExtendedKey(extendedKey);
        if (decoded.length != 78) {
            throw new IllegalArgumentException("Invalid extendend key length");
        }
        this.prefix = Arrays.copyOfRange(decoded, 0, 4);
        this.isKeyPrivate();
        this.depth = decoded[4];
        this.fingerprint = Arrays.copyOfRange(decoded, 5, 9);
        this.childNumber = Arrays.copyOfRange(decoded, 9, 13);
        if (depth == 0) {
            if (!Arrays.equals(HEX_00000000.toBytes(), this.fingerprint)) {
                throw new IllegalArgumentException("Invalid fingerprint");
            }
            if (!Arrays.equals(HEX_00000000.toBytes(), this.childNumber)) {
                throw new IllegalArgumentException("Invalid child number");
            }
        }
        this.chainCode = Arrays.copyOfRange(decoded, 13, 45);
        this.key = Arrays.copyOfRange(decoded, 45, decoded.length);
    }

    /**
     * parse
     *
     * @param extendedKey .
     * @return .
     */
    public static ExtendedKey parse(String extendedKey) {
        return new ExtendedKey(extendedKey);
    }

    private ExtendedKey(byte[] prefix, int depth, byte[] fingerprint, byte[] childNumber, byte[] chainCode, byte[] key) {
        this.prefix = prefix;
        if (this.prefix.length != 4) {
            throw new IllegalArgumentException("invalid prefix");
        }
        this.isKeyPrivate();
        this.depth = depth;
        this.fingerprint = fingerprint;
        if (this.fingerprint.length != 4) {
            throw new IllegalArgumentException("invalid fingerprint");
        }
        this.childNumber = childNumber;
        if (this.childNumber.length != 4) {
            throw new IllegalArgumentException("invalid child number");
        }
        this.chainCode = chainCode;
        if (chainCode.length != 32) {
            throw new IllegalArgumentException("invalid chain code length");
        }
        this.key = key;
        if (this.key.length != 33) {
            throw new IllegalArgumentException("invalid key length");
        }
    }

    public boolean isKeyPrivate() {
        if (Arrays.equals(PREFIX_XPRV.toBytes(), this.prefix)) {
            return true;
        } else if (Arrays.equals(PREFIX_XPUB.toBytes(), this.prefix)) {
            return false;
        } else if (Arrays.equals(PREFIX_ZPRV.toBytes(), this.prefix)) {
            return true;
        } else if (Arrays.equals(PREFIX_ZPUB.toBytes(), this.prefix)) {
            return false;
        } else {
            throw new IllegalArgumentException(String.format("Unknown prefix %s", Hex.parse(this.prefix)));
        }
    }

    public ExtendedKey derive(int index) {
        return this.derive(index, false, false);
    }

    public ExtendedKey
    derive(int index, boolean harden, boolean neutral) {
        var indexInt = Int.parse(index);
        if (harden) {
            indexInt = indexInt.add(HARDENED_INDEX);
        }
        if (this.isKeyPrivate()) {
            var derivedDepth = this.depth + 1;
            var privateKey = PrivateKey.parse(this.key);
            var publicKeyPoint = privateKey.getPoint();
            var derivedFingerprint = Arrays.copyOfRange(publicKeyPoint.hash160(true), 0, 4);
            var derivedChildNumber = indexInt.toBytes(4);
            var hmac = Hash.hmacS512Init(this.chainCode);
            if (indexInt.ge(HARDENED_INDEX)) {
                hmac.update(privateKey.getSecret().toBytes(33));
            } else {
                hmac.update(publicKeyPoint.sec(true));
            }
            hmac.update(indexInt.toBytes(4));
            var i = hmac.doFinal();
            var derivedChainCode = Arrays.copyOfRange(i, 32, i.length);
            var iLeft = Arrays.copyOfRange(i, 0, 32);
            var iLeftHex = Hex.parse(iLeft);
            var derivedPrivateKey = iLeftHex.add(privateKey.getSecret()).mod(S256Point.N).toBytes(33);
            if (iLeftHex.ge(S256Point.N) || Hex.parse(derivedPrivateKey).eq(Int.parse(0))) {
                return derive(index + 1, harden, neutral);
            }
            if (neutral) {
                var neutralPublicKey = PrivateKey.parse(derivedPrivateKey).getPoint().sec(true);
                return new ExtendedKey(this.getPrefixNeutral(), derivedDepth, derivedFingerprint, derivedChildNumber, derivedChainCode, neutralPublicKey);
            } else {
                return new ExtendedKey(this.prefix, derivedDepth, derivedFingerprint, derivedChildNumber, derivedChainCode, derivedPrivateKey);
            }
        } else {
            if (indexInt.ge(HARDENED_INDEX)) {
                throw new IllegalArgumentException("hardening only for private keys");
            }
            if (neutral) {
                throw new IllegalArgumentException("neutralizing only for private keys");
            }
            var derivedDepth = this.depth + 1;
            var publicKey = S256Point.parse(this.key);
            var derivedFingerprint = Arrays.copyOfRange(publicKey.hash160(true), 0, 4);
            var derivedChildNumber = indexInt.toBytes(4);
            var hmac = Hash.hmacS512Init(this.chainCode);
            var publicKeySec = publicKey.sec(true);
            hmac.update(publicKeySec);
            hmac.update(indexInt.toBytes(4));
            var i = hmac.doFinal();
            var derivedChainCode = Arrays.copyOfRange(i, 32, i.length);
            var iLeft = Arrays.copyOfRange(i, 0, 32);
            var iLeftPoint = PrivateKey.parse(iLeft).getPoint();
            var derivedPublicKey = iLeftPoint.add(publicKey);
            if (Hex.parse(iLeft).ge(S256Point.N) || (derivedPublicKey.getX() == null && derivedPublicKey.getY() == null)) {
                return derive(index + 1, harden, neutral);
            }
            var derivedPublicKeySec = derivedPublicKey.sec(true);
            return new ExtendedKey(this.prefix, derivedDepth, derivedFingerprint, derivedChildNumber, derivedChainCode, derivedPublicKeySec);
        }
    }

    private byte[] getPrefixNeutral() {
        if (Arrays.equals(PREFIX_XPRV.toBytes(), this.prefix)) {
            return PREFIX_XPUB.toBytes();
        } else if (Arrays.equals(PREFIX_ZPRV.toBytes(), this.prefix)) {
            return PREFIX_ZPUB.toBytes();
        } else {
            return this.prefix;
        }
    }

    public byte[] getPrefix() {
        return prefix;
    }

    public int getDepth() {
        return depth;
    }

    public byte[] getFingerprint() {
        return fingerprint;
    }

    public byte[] getChildNumber() {
        return childNumber;
    }

    public byte[] getChainCode() {
        return chainCode;
    }

    public byte[] getKey() {
        return key;
    }
}
