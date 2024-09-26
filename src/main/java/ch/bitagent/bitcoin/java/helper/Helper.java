package ch.bitagent.bitcoin.java.helper;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * <p>Helper class.</p>
 */
public class Helper {

    private static final Logger log = Logger.getLogger(Helper.class.getSimpleName());

    /** Constant <code>SIGHASH_ALL</code> */
    public static final Int SIGHASH_ALL = Int.parse(1);
    /** Constant <code>SIGHASH_NONE</code> */
    public static final Int SIGHASH_NONE = Int.parse(2);
    /** Constant <code>SIGHASH_SINGLE</code> */
    public static final Int SIGHASH_SINGLE = Int.parse(3);

    /** Constant <code>TWO_WEEKS</code> */
    public static final Int TWO_WEEKS = Int.parse(60 * 60 * 24 * 14);
    /** Constant <code>MAX_TARGET</code> */
    public static final Int MAX_TARGET = Hex.parse("ffff").mul(Int.parse(256).pow(Hex.parse("1d").sub(Int.parse(3))));

    private Helper() {}

    private static byte[] ripemd160(byte[] bytes) {
        return Ripemd160.getHash(bytes);
    }

    /**
     * <p>sha1.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * <p>sha256.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
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
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] hash160(byte [] bytes) {
        return ripemd160(sha256(bytes));
    }

    /**
     * two rounds of sha256
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] hash256(byte[] bytes) {
        return sha256(sha256(bytes));
    }

    /**
     * <p>randomBytes.</p>
     *
     * @param length a int
     * @return an array of {@link byte} objects
     */
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * <p>hmacS256Init.</p>
     *
     * @param key an array of {@link byte} objects
     * @return a {@link javax.crypto.Mac} object
     */
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

    /**
     * <p>zfill64.</p>
     *
     * @param bytes a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String zfill64(String bytes) {
        return String.format("%64s", bytes).replace(' ', '0');
    }

    /**
     * <p>maskString.</p>
     *
     * @param str a {@link java.lang.String} object
     * @param len a int
     * @return a {@link java.lang.String} object
     */
    public static String maskString(String str, int len) {
        return str.substring(0, len) + ":" + str.substring(str.length() - len);
    }

    /**
     * <p>btcToSat.</p>
     *
     * @param f a double
     * @return a {@link java.lang.String} object
     */
    public static String btcToSat(double f) {
        return String.format("%8.0f", f * 100000000).trim();
    }

    /**
     * <p>logWithBase.</p>
     *
     * @param value a double
     * @param base a double
     * @return a double
     */
    public static double logWithBase(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * <p>log.</p>
     *
     * @deprecated temp use only
     * @param string a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    @Deprecated(since = "0")
    public static String log(String string) {
        log.warning(String.format("%s (%s) %s", string, string.length()));
        return string;
    }
}
