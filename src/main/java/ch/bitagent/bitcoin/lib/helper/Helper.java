package ch.bitagent.bitcoin.lib.helper;

import ch.bitagent.bitcoin.lib.wallet.MnemonicSentence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * <p>Helper class.</p>
 */
public class Helper {

    private static final Logger log = Logger.getLogger(Helper.class.getSimpleName());

    private Helper() {
    }

    /**
     * <p>zfill</p>
     *
     * @param length .
     * @param bytes .
     * @return .
     */
    public static String zfill(int length, String bytes) {
        return String.format("%" + length + "s", bytes).replace(' ', '0');
    }

    /**
     * boolArrayToString
     *
     * @param boolArray .
     * @return .
     */
    public static String boolArrayToString(boolean[] boolArray) {
        var string = new StringBuilder();
        for (boolean bool : boolArray) {
            string.append(bool ? "1" : "0");
        }
        return string.toString();
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
     * loadWordList
     *
     * @param resource .
     * @return .
     */
    public static ArrayList<String> loadWordlist(String resource) {
        var wordlist = new ArrayList<String>();
        try {
            var inputStream = MnemonicSentence.class.getResourceAsStream(resource);
            if (inputStream == null) {
                throw new IllegalStateException("File not found.");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    wordlist.add(line);
                }
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return wordlist;
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
