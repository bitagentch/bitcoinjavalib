package ch.bitagent.bitcoin.lib.helper;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Bytes class.</p>
 */
public class Bytes {

    private static final Logger log = Logger.getLogger(Bytes.class.getSimpleName());

    /**
     * Constant <code>TWO_WEEKS</code>
     */
    public static final Int TWO_WEEKS = Int.parse(60 * 60 * 24 * 14);

    /**
     * Constant <code>MAX_TARGET</code>
     */
    private static final Int MAX_TARGET = Hex.parse("ffff").mul(Int.parse(256).pow(Hex.parse("1d").sub(Int.parse(3))));

    private Bytes() {
    }

    /**
     * Strip leading
     *
     * @param bytes an array of {@link byte} objects
     * @param bite  a byte
     * @return an array of {@link byte} objects
     */
    public static byte[] lstrip(byte[] bytes, byte bite) {
        int i = 0;
        while (bytes[i] == bite) {
            i++;
        }
        return Arrays.copyOfRange(bytes, i, bytes.length);
    }

    /**
     * Strip trailing
     *
     * @param bytes an array of {@link byte} objects
     * @param bite  a byte
     * @return an array of {@link byte} objects
     */
    public static byte[] strip(byte[] bytes, byte bite) {
        var setyb = Bytes.changeOrder(bytes);
        int i = 0;
        while (setyb[i] == bite) {
            i++;
        }
        return Arrays.copyOfRange(bytes, 0, bytes.length - i);
    }

    /**
     * <p>initFill.</p>
     *
     * @param length a int
     * @param filler a byte
     * @return an array of {@link byte} objects
     */
    public static byte[] initFill(int length, byte filler) {
        var init = new byte[length];
        Arrays.fill(init, filler);
        return init;
    }

    /**
     * <p>add.</p>
     *
     * @param a an array of {@link byte} objects
     * @param b an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] add(byte[] a, byte[] b) {
        var z = new byte[a.length + b.length];
        System.arraycopy(a, 0, z, 0, a.length);
        System.arraycopy(b, 0, z, a.length, b.length);
        return z;
    }

    /**
     * <p>add.</p>
     *
     * @param bytesArray an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] add(byte[][] bytesArray) {
        int lengthAdded = 0;
        for (byte[] bytes : bytesArray) {
            lengthAdded += bytes.length;
        }
        var bytesAdded = new byte[lengthAdded];
        int destPos = 0;
        for (byte[] bytes : bytesArray) {
            System.arraycopy(bytes, 0, bytesAdded, destPos, bytes.length);
            destPos += bytes.length;
        }
        return bytesAdded;
    }

    /**
     * Change the order from big to little endian and vice versa
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] changeOrder(byte[] bytes) {
        byte[] destBytes = Arrays.copyOf(bytes, bytes.length);
        for (int i = 0, j = destBytes.length - 1; i < j; i++, j--) {
            byte b = destBytes[i];
            destBytes[i] = destBytes[j];
            destBytes[j] = b;
        }
        return destBytes;
    }

    /**
     * <p>read.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @param len    a int
     * @return an array of {@link byte} objects
     */
    public static byte[] read(ByteArrayInputStream stream, int len) {
        try {
            return stream.readNBytes(len);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * <p>hexStringToByteArray.</p>
     *
     * @param s a {@link java.lang.String} object
     * @return an array of {@link byte} objects
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes();

    /**
     * <p>byteArrayToHexString.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return a {@link java.lang.String} object
     */
    public static String byteArrayToHexString(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * <p>byteArrayToString.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return a {@link java.lang.String} object
     */
    public static String byteArrayToString(byte[] bytes) {
        return new String(bytes);
    }

    /**
     * Turns bits into a target (large 256-bit integer)
     *
     * @param bits an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public static Int bitsToTarget(byte[] bits) {
        var bitsLen = bits.length;
        // last byte is exponent
        var exponent = Hex.parse(Arrays.copyOfRange(bits, bitsLen - 1, bitsLen));
        // the first three bytes are the coefficient in little endian
        var coefficient = Hex.parse(Bytes.changeOrder(Arrays.copyOfRange(bits, 0, bitsLen - 1)));
        // the formula is: coefficient * 256**(exponent-3)
        return coefficient.mul(Int.parse(256).pow(exponent.sub(Int.parse(3))));
    }

    /**
     * Turns a target integer back into bits, which is 4 bytes
     *
     * @param target a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return an array of {@link byte} objects
     */
    public static byte[] targetToBits(Int target) {
        var rawBytes = target.toBytes(32);
        // get rid of leading 0's
        rawBytes = Bytes.lstrip(rawBytes, (byte) 0x00);
        int exponent;
        byte[] coefficient;
        if (((rawBytes[0] >> 8) & 1) == 1) {
            // if the first bit is 1, we have to start with 00
            exponent = rawBytes.length + 1;
            coefficient = Bytes.add(new byte[]{0x00}, Arrays.copyOfRange(rawBytes, 0, 2));
        } else {
            // otherwise, we can show the first 3 bytes
            // exponent is the number of digits in base-256
            exponent = rawBytes.length;
            // coefficient is the first 3 digits of the base-256 number
            coefficient = Arrays.copyOfRange(rawBytes, 0, 3);
        }
        // we've truncated the number after the first 3 digits of base-256
        return Bytes.add(Bytes.changeOrder(coefficient), new byte[]{(byte) exponent});
    }

    /**
     * Calculates the new bits given a 2016-block time differential and the previous bits
     *
     * @param previousBits     an array of {@link byte} objects
     * @param timeDifferential a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return an array of {@link byte} objects
     */
    public static byte[] calculateNewBits(byte[] previousBits, Int timeDifferential) {
        // if the time differential is greater than 8 weeks, set to 8 weeks
        if (timeDifferential.gt(TWO_WEEKS.mul(Int.parse(4)))) {
            timeDifferential = TWO_WEEKS.mul(Int.parse(4));
        }
        // if the time differential is less than half a week, set to half a week
        if (timeDifferential.lt(TWO_WEEKS.div(Int.parse(4)))) {
            timeDifferential = TWO_WEEKS.div(Int.parse(4));
        }
        // the new target is the previous target * time differential / two weeks
        var newTarget = bitsToTarget(previousBits).mul(timeDifferential).div(TWO_WEEKS);
        // if the new target is bigger than MAX_TARGET, set to MAX_TARGET
        if (newTarget.gt(MAX_TARGET)) {
            newTarget = MAX_TARGET;
        }
        // convert the new target to bits
        return targetToBits(newTarget);
    }

    /**
     * <p>bitFieldToBytes.</p>
     *
     * @param bitField an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] bitFieldToBytes(byte[] bitField) {
        if (bitField.length % 8 != 0) {
            throw new IllegalArgumentException("bit_field does not have a length that is divisible by 8");
        }
        var result = new byte[bitField.length / 8];
        for (int i = 0; i < bitField.length; i++) {
            var byteIndex = i / 8;
            var bitIndex = i % 8;
            if (bitField[i] == 1) {
                result[byteIndex] |= (byte) (1 << bitIndex);
            }
        }
        return result;
    }

    /**
     * <p>bytesToBitField.</p>
     *
     * @param someBytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] bytesToBitField(byte[] someBytes) {
        List<Byte> flagBits = new ArrayList<>();
        // iterate over each byte of flags
        for (byte bite : someBytes) {
            // iterate over each bit, right-to-left
            for (int i = 0; i < 8; i++) {
                // add the current bit (byte & 1)
                flagBits.add((byte) (bite & 1));
                // rightshift the byte 1
                bite >>= 1;
            }
        }
        var bitField = new byte[flagBits.size()];
        for (int i = 0; i < flagBits.size(); i++) {
            bitField[i] = flagBits.get(i);
        }
        return bitField;
    }

    /**
     * <p>xor.</p>
     *
     * @param b0 a byte array
     * @param b1 a byte array
     * @return an byte array
     */
    public static byte[] xor(byte[] b0, byte[] b1) {
        if (b0.length != b1.length) {
            return new byte[0];
        }
        byte[] ret = new byte[b0.length];
        int i = 0;
        for (byte b : b0) {
            ret[i] = (byte) (b ^ b1[i]);
            i++;
        }
        return ret;
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
     * <p>log.</p>
     *
     * @param bytes an array of {@link byte} objects
     * @return an array of {@link byte} objects
     * @deprecated temp use only
     */
    @Deprecated(since = "0")
    public static byte[] log(byte[] bytes) {
        log.warning(String.format("%s (%s)", byteArrayToHexString(bytes), bytes.length));
        return bytes;
    }
}
