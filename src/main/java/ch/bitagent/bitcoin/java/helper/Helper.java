package ch.bitagent.bitcoin.java.helper;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class Helper {

    private static final Logger log = Logger.getLogger(Helper.class.getSimpleName());

    public static final Int SIGHASH_ALL = Int.parse(1);
    public static final Int SIGHASH_NONE = Int.parse(2);
    public static final Int SIGHASH_SINGLE = Int.parse(3);

    public static final Int TWO_WEEKS = Int.parse(60 * 60 * 24 * 14);
    public static final Int MAX_TARGET = Hex.parse("ffff").mul(Int.parse(256).pow(Hex.parse("1d").sub(Int.parse(3))));

    private Helper() {}

    private static byte[] ripemd160(byte[] bytes) {
        return Ripemd160.getHash(bytes);
    }

    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static byte[] sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * sha256 followed by ripemd160
     */
    public static byte[] hash160(byte [] bytes) {
        return ripemd160(sha256(bytes));
    }

    /**
     * two rounds of sha256
     */
    public static byte[] hash256(byte[] bytes) {
        return sha256(sha256(bytes));
    }

    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public static Mac hmacS256Init(byte[] key) {
        try {
            String algorithm = "HmacSHA256";
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            return mac;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static String zfill64(String bytes) {
        return String.format("%64s", bytes).replace(' ', '0');
    }

    public static String maskString(String str, int len) {
        return str.substring(0, len) + ":" + str.substring(str.length() - len);
    }

    public static String btcToSat(double f) {
        return String.format("%8.0f", f * 100000000).trim();
    }

    public static double logWithBase(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * @deprecated temp use only
     */
    @Deprecated(since = "0")
    public static String log(String string) {
        log.warning(String.format("%s (%s) %s", string, string.length()));
        return string;
    }
}
