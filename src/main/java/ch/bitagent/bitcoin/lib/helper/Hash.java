package ch.bitagent.bitcoin.lib.helper;

import ch.bitagent.bitcoin.lib.ecc.Int;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Hash {

    private static final Logger log = Logger.getLogger(Hash.class.getSimpleName());

    /**
     * Constant <code>SIGHASH_ALL</code>
     */
    public static final Int SIGHASH_ALL = Int.parse(1);
    /**
     * Constant <code>SIGHASH_NONE</code>
     */
    public static final Int SIGHASH_NONE = Int.parse(2);
    /**
     * Constant <code>SIGHASH_SINGLE</code>
     */
    public static final Int SIGHASH_SINGLE = Int.parse(3);

    private Hash() {
    }

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
    public static byte[] hash160(byte[] bytes) {
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
     * <p>hmacS512Init.</p>
     *
     * @param key an array of {@link byte} objects
     * @return a {@link javax.crypto.Mac} object
     */
    public static Mac hmacS512Init(byte[] key) {
        try {
            String algorithm = "HmacSHA512";
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            return mac;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }
}
