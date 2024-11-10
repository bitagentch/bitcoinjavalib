package ch.bitagent.bitcoin.lib.helper;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * <p>Base58 class.</p>
 */
public class Base58 {

    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private Base58() {}

    /**
     * <p>encode.</p>
     *
     * @param s an array of {@link byte} objects
     * @return a {@link java.lang.String} object
     */
    public static String encode(byte[] s) {
        int count = 0;
        for (byte c : s) {
            if (c == 0) {
                count += 1;
            } else {
                break;
            }
        }
        Int num = Hex.parse(s);
        Int zero = Int.parse(0);
        String prefix = "1".repeat(count);
        StringBuilder result = new StringBuilder();
        while (num.gt(zero)) {
            BigInteger[] numMod = num.bigInt().divideAndRemainder(BigInteger.valueOf(58));
            num = Int.parse(numMod[0]);
            result.insert(0, BASE58_ALPHABET.charAt(numMod[1].intValue()));
        }
        return prefix + result;
    }

    /**
     * <p>encodeChecksum.</p>
     *
     * @param b an array of {@link byte} objects
     * @return a {@link java.lang.String} object
     */
    public static String encodeChecksum(byte[] b) {
        return Base58.encode(Bytes.add(b, Arrays.copyOfRange(Helper.hash256(b), 0, 4)));
    }

    /**
     * <p>decodeAddress</p>
     *
     * @param address .
     * @return .
     */
    public static byte[] decodeAddress(String address) {
        var num = decode(address);
        var combined = num.toBytes(25);
        compareChecksum(combined);
        return Arrays.copyOfRange(combined, 1, combined.length - 4);
    }

    /**
     * <p>decodeExtendedKey</p>
     *
     * @param extendedKey .
     * @return .
     */
    public static byte[] decodeExtendedKey(String extendedKey) {
        var num = decode(extendedKey);
        var combined = num.toBytes(82);
        compareChecksum(combined);
        return Arrays.copyOfRange(combined, 0, combined.length - 4);
    }

    /**
     * decodeWif
     *
     * @param wif .
     * @param compressed .
     * @return .
     */
    public static byte[] decodeWif(String wif, boolean compressed) {
        var num = decode(wif);
        var combined = num.toBytes(compressed ? 38 : 37);
        compareChecksum(combined);
        return Arrays.copyOfRange(combined, 0, combined.length - 4);
    }

    private static Int decode(String s) {
        var fiveEight = Int.parse(58);
        var num = Int.parse(0);
        for (int i = 0; i < s.length(); i++) {
            var c = s.charAt(i);
            num = num.mul(fiveEight);
            num = num.add(Int.parse(BASE58_ALPHABET.indexOf(c)));
        }
        return num;
    }

    private static void compareChecksum(byte[] combined) {
        var checksum = Arrays.copyOfRange(combined, combined.length - 4, combined.length);
        var hashInput = Arrays.copyOfRange(combined, 0, combined.length - 4);
        var hash = Helper.hash256(hashInput);
        var hashChecksum = Arrays.copyOfRange(hash, 0, 4);
        if (Arrays.compare(hashChecksum, checksum) != 0) {
            throw new IllegalStateException(String.format("bad checksum %h %h", checksum, hashChecksum));
        }
    }

    /**
     * <p>h160toP2pkhAddress.</p>
     *
     * @param h160 an array of {@link byte} objects
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link java.lang.String} object
     */
    public static String h160toP2pkhAddress(byte[] h160, Boolean testnet) {
        testnet = Objects.requireNonNullElse(testnet, false);
        byte[] prefix;
        if (Boolean.TRUE.equals(testnet)) {
            prefix = new byte[]{0x6f};
        } else {
            prefix = new byte[]{0x00};
        }
        return encodeChecksum(Bytes.add(prefix, h160));
    }

    /**
     * <p>h160toP2shAddress.</p>
     *
     * @param h160 an array of {@link byte} objects
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link java.lang.String} object
     */
    public static String h160toP2shAddress(byte[] h160, Boolean testnet) {
        testnet = Objects.requireNonNullElse(testnet, false);
        byte[] prefix;
        if (Boolean.TRUE.equals(testnet)) {
            prefix = new byte[]{(byte) 0xc4};
        } else {
            prefix = new byte[]{0x05};
        }
        return encodeChecksum(Bytes.add(prefix, h160));
    }
}
