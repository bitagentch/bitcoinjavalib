package ch.bitagent.bitcoin.lib.helper;

import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * <p>Helper class.</p>
 */
public class Helper {

    private static final Logger log = Logger.getLogger(Helper.class.getSimpleName());

    private Helper() {
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
     * @param base  a double
     * @return a double
     */
    public static double logWithBase(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * <p>log.</p>
     *
     * @param string a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     * @deprecated temp use only
     */
    @Deprecated(since = "0")
    public static String log(String string) {
        log.warning(String.format("%s (%s) %s", string, string.length()));
        return string;
    }
}
