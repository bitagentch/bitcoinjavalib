package ch.bitagent.bitcoin.lib.helper;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * <p>Bech32</p>
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki">BIP-0173</a>
 */
public class Bech32 {

    private Bech32() {}

    private static final Logger log = Logger.getLogger(Bech32.class.getSimpleName());

    /** The Bech32 and Bech32m character set for encoding. */
    private static final String BECH32_ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    private static byte[] getBech32AlphabetIndex(String rawData) {
        var indexArray = new byte[rawData.length()];
        for (int i = 0; i < indexArray.length; i++) {
            var indexChar = rawData.charAt(i);
            var index = (byte) BECH32_ALPHABET.indexOf(indexChar);
            if (index == -1) {
                throw new IllegalArgumentException(String.format("Invalid data index character %s", (byte) indexChar));
            }
            indexArray[i] = index;
        }
        return indexArray;
    }

    private static byte[] getBech32AlphabetValue(byte[] rawData) {
        var valueArray = new byte[rawData.length];
        for (int i = 0; i < valueArray.length; i++) {
            var valueChar = rawData[i];
            var value = BECH32_ALPHABET.getBytes()[valueChar];
            if (value == -1) {
                throw new IllegalArgumentException(String.format("Invalid data value character %s", valueChar));
            }
            valueArray[i] = value;
        }
        return valueArray;
    }

    /**
     * <p>The Bech32 and Bech32m character set for decoding.</p>
     *
     * <a href="https://github.com/bitcoin/bitcoin/blob/master/src/bech32.cpp">bech32.cpp</a>
     */
    private static final byte[] BECH32_ALPHABET_DEC = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            15, -1, 10, 17, 21, 20, 26, 30,  7,  5, -1, -1, -1, -1, -1, -1,
            -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
             1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1,
            -1, 29, -1, 24, 13, 25,  9,  8, 23, -1, 18, 22, 31, 27, 19, -1,
             1,  0,  3, 16, 11, 28, 12, 14,  6,  4,  2, -1, -1, -1, -1, -1
    };

    private static byte[] getBech32AlphabetDecodingValue(String rawData) {
        var valueArray = new byte[rawData.length()];
        for (int i = 0; i < valueArray.length; i++) {
            var valueChar = (byte) rawData.charAt(i);
            var value = BECH32_ALPHABET_DEC[valueChar];
            if (value == -1) {
                throw new IllegalArgumentException(String.format("Invalid decoding data value character %s", valueChar));
            }
            valueArray[i] = value;
        }
        return valueArray;
    }

    private static int polymod(byte[] values) {
        var gen = new int[]{0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};
        var chk = 1;
        for (byte value : values) {
            var b = chk >> 25;
            chk = ((chk & 0x1ffffff) << 5) ^ (value & 0xff);
            for (int i = 0; i < 5; i++) {
                if (((b >> i) & 1) != 0) {
                    chk ^= gen[i];
                }
            }
        }
        return chk;
    }

    private static byte[] hrpExpand(String hrp) {
        var hrpArray = hrp.toCharArray();
        var hrpLength = hrp.length();
        var hrpExpandLength = 1 + hrpLength;
        var hrpExpanded = new byte[hrpLength + hrpExpandLength];
        hrpExpanded[hrpLength] = 0;
        for (int i = 0; i < hrpLength; i++) {
            hrpExpanded[i] = (byte) (hrpArray[i] >> 5);
            hrpExpanded[i + hrpExpandLength] = (byte) (hrpArray[i] & 31);
        }
        return hrpExpanded;
    }

    private static boolean verifyChecksum(String hrp, byte[] bytes) {
        var hrpExpanded = hrpExpand(hrp);
        var polymod = polymod(Bytes.add(hrpExpanded, bytes));
        return polymod == 1;
    }

    private static byte[] createChecksum(String hrp, byte[] bytes) {
        var values = Bytes.add(hrpExpand(hrp), bytes);
        var polymod = polymod(Bytes.add(values, new byte[]{0, 0, 0, 0, 0, 0})) ^ 1;
        var checksum = new byte[6];
        for (int i = 0; i < 6; i++) {
            checksum[i] = (byte) ((polymod >> (5 * (5 - i))) & 31);
        }
        return checksum;
    }

    /**
     * verify
     *
     * @param bech32 .
     * @return .
     */
    public static boolean verify(String bech32) {
        try {
            var bech32Low = bech32.toLowerCase();
            if (bech32Low.length() > 90) {
                throw new IllegalArgumentException(String.format("overall max length (90) exceeded %s", bech32.length()));
            }
            var lastIndex = bech32Low.lastIndexOf("1");
            if (lastIndex == -1) {
                throw new IllegalArgumentException("No separator character 1");
            }
            var hrp = bech32Low.substring(0, lastIndex);
            if (hrp.isEmpty()) {
                throw new IllegalArgumentException("Empty HRP");
            }
            for (int i = 0; i < hrp.length(); i++) {
                var hrpChar = (byte) hrp.charAt(i);
                if (hrpChar < 33 || hrpChar > 126) {
                    throw new IllegalArgumentException(String.format("HRP character out of range [33-126]. %s", hrpChar));
                }
            }
            var rawData = bech32Low.substring(lastIndex + 1);
            if (rawData.length() < 6) {
                throw new IllegalArgumentException(String.format("Too short checksum %s", rawData.length()));
            }
            var verify = verifyChecksum(hrp, getBech32AlphabetIndex(rawData));
            if (!verify) {
                throw new IllegalArgumentException(String.format("Bech32 checksum not valid %s", bech32));
            }
            return true;
        } catch (Exception e) {
            log.fine(e.getMessage());
            return false;
        }
    }

    /**
     * Bech32Data
     */
    public static class Bech32Data {
        private String hrp;
        private byte[] dataBytes;

        /**
         * Getter
         *
         * @return .
         */
        public String getHrp() {
            return hrp;
        }

        /**
         * Setter
         *
         * @param hrp .
         */
        public void setHrp(String hrp) {
            this.hrp = hrp;
        }

        /**
         * Getter
         *
         * @return .
         */
        public byte[] getDataBytes() {
            return dataBytes;
        }

        /**
         * Setter
         *
         * @param dataBytes .
         */
        public void setDataBytes(byte[] dataBytes) {
            this.dataBytes = dataBytes;
        }
    }

    /**
     * decode
     *
     * @param bech32 .
     * @return .
     */
    public static Bech32Data decode(String bech32) {
        if (!verify(bech32)) {
            throw new IllegalArgumentException(String.format("bad bech32 %s", bech32));
        }
        var bech32Low = bech32.toLowerCase();
        var bech32Upp = bech32.toUpperCase();
        if (!bech32Low.equals(bech32) && !bech32Upp.equals(bech32)) {
            throw new IllegalArgumentException(String.format("mixed case bech32 %s", bech32));
        }
        var lastIndex = bech32Low.lastIndexOf("1");
        var hrp = bech32Low.substring(0, lastIndex);
        var rawData = bech32Low.substring(lastIndex + 1);
        var dataBytes = getBech32AlphabetDecodingValue(rawData.substring(0, rawData.length() - 6));
        var bech32Data = new Bech32Data();
        bech32Data.setHrp(hrp);
        bech32Data.setDataBytes(dataBytes);
        return bech32Data;
    }

    /**
     * encode
     *
     * @param hrp .
     * @param bytes .
     * @return .
     */
    public static String encode(String hrp, byte[] bytes) {
        var checksum = createChecksum(hrp, bytes);
        var data = Bytes.add(bytes, checksum);
        var value = Bytes.byteArrayToString(getBech32AlphabetValue(data));
        return String.format("%s1%s", hrp, value);
    }

    /**
     * decodeSegwit
     *
     * @param bech32 .
     * @return .
     */
    public static String decodeSegwit(String bech32) {
        var decoded = Bech32.decode(bech32);
        if (!decoded.getHrp().equals("bc") && !decoded.getHrp().equals("tb")) {
            throw new IllegalArgumentException(String.format("invalid HRP %s", decoded.getHrp()));
        }
        if (decoded.getDataBytes().length == 0) {
            throw new IllegalArgumentException("Empty data section");
        }
        var version = decoded.getDataBytes()[0];
        if (version < 0 || version > 16) {
            throw new IllegalArgumentException(String.format("invalid version %s", version));
        }
        var program5 = Arrays.copyOfRange(decoded.getDataBytes(), 1, decoded.getDataBytes().length);
        var program8 = Bech32.convertBits(program5, 5, 8, false);
        if (program8.length < 2 || program8.length > 40) {
            throw new IllegalArgumentException(String.format("invalid program length %s", program8.length));
        }
        var scriptPubkey = new byte[1 + 1 + program8.length];
        if (version == 0) {
            if (program8.length != 20 && program8.length != 32) {
                throw new IllegalArgumentException(String.format("invalid program length %s for version 0", program8.length));
            }
            scriptPubkey[0] = version;
        } else {
            scriptPubkey[0] = (byte) (version + 0x50);
        }
        scriptPubkey[1] = (byte) program8.length;
        System.arraycopy(program8, 0, scriptPubkey, 2, program8.length);
        return Bytes.byteArrayToHexString(scriptPubkey);
    }

    /**
     * encodeSegwit
     *
     * @param hrp .
     * @param scriptPubkey .
     * @return .
     */
    public static String encodeSegwit(String hrp, String scriptPubkey) {
        var scriptPubkeyBytes = Bytes.hexStringToByteArray(scriptPubkey);
        var version = scriptPubkeyBytes[0];
        var program8 = Arrays.copyOfRange(scriptPubkeyBytes, 2, scriptPubkeyBytes.length);
        var program5 = convertBits(program8, 8, 5, true);
        return encode(hrp, Bytes.add(new byte[]{version}, program5));
    }

    /**
     * <p>decodeNostr</p>
     * <a href="https://github.com/nostr-protocol/nips/blob/master/19.md">NIP-19</a>
     *
     * @param bech32 .
     * @return .
     */
    public static String decodeNostr(String bech32) {
        var decoded = decode(bech32);
        byte[] bytes8 = convertBits(decoded.getDataBytes(), 5, 8, false);
        return Bytes.byteArrayToHexString(bytes8);
    }

    /**
     * <p>encodeNostr</p>
     *
     * @param hrp .
     * @param hexString .
     * @return .
     */
    public static String encodeNostr(String hrp, String hexString) {
        var bytes8 = Bytes.hexStringToByteArray(hexString);
        var bytes5 = convertBits(bytes8, 8, 5, true);
        return encode(hrp, bytes5);
    }

    /**
     * <p>convertBits</p>
     * <a href="https://github.com/bitcoinj/bitcoinj/blob/master/base/src/main/java/org/bitcoinj/base/Bech32.java">Bech32.java</a>
     *
     * @param bytes .
     * @param fromBits .
     * @param toBits .
     * @param pad .
     * @return .
     */
    public static byte[] convertBits(final byte[] bytes, final int fromBits, final int toBits, final boolean pad) {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (byte bite : bytes) {
            int value = bite & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new IllegalArgumentException(String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0)
                out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new IllegalArgumentException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }
}
