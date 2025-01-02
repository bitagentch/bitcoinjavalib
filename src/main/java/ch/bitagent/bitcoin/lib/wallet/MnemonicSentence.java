package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.helper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * <p>MnemonicSentence</p>
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki">BIP-0039</a>
 */
public class MnemonicSentence {

    private static final Logger log = Logger.getLogger(MnemonicSentence.class.getSimpleName());

    private static final String RESOURCE_ENGLISH = "/wallet/english.txt";

    private static final int PBKDF2_ROUNDS = 2048;
    private static final int DERIVED_KEY_LENGTH = 64;

    private MnemonicSentence() {
    }

    /**
     * Create a new entopy using a random generated number.
     *
     * @param entropyStrength 128, 160, 192, 224 or 256 bits
     * @return .
     */
    public static byte[] generateEntropy(int entropyStrength) {
        if (entropyStrength != 128 && entropyStrength != 160 && entropyStrength != 192 && entropyStrength != 224 && entropyStrength != 256) {
            throw new IllegalArgumentException("Invalid entropy strength. Allowed values are 128, 160, 192, 224 or 256 bits.");
        }
        var entropyLength = entropyStrength / 8;
        return Bytes.randomBytes(entropyLength);
    }

    /**
     * entropyToMnemonic
     *
     * @param entropy .
     * @return mnemonicSentence
     */
    public static String entropyToMnemonic(byte[] entropy) {
        if (entropy.length != 16 && entropy.length != 20 && entropy.length != 24 && entropy.length != 28 && entropy.length != 32) {
            throw new IllegalArgumentException(String.format("Entropy length should be one of the following: 16, 20, 24, 28 or 32, but it is not %s.", entropy.length));
        }

        var entropyBin = Hex.parse(entropy).toBin();
        var entropyBinZ = Helper.zfill(entropy.length * 8, entropyBin);
        var concatenatedBin = entropyBinZ + createEntropyChecksum(entropy);
        var wordlist = Helper.loadWordlist(RESOURCE_ENGLISH);

        var mnemonicList = new ArrayList<String>();
        for (int i = 0; i < concatenatedBin.length() / 11; i++) {
            String group11Bin = concatenatedBin.substring(i * 11, (i + 1) * 11);
            int wordIndex = Integer.parseInt(group11Bin, 2);
            mnemonicList.add(wordlist.get(wordIndex));
        }
        return String.join(" ", mnemonicList);
    }

    /**
     * mnemonicToEntropy
     *
     * @param mnemonicSentence .
     * @return .
     */
    public static byte[] mnemonicToEntropy(String mnemonicSentence) {
        var mnemonicArray = getMnemonicArray(mnemonicSentence);

        int concatenatedBinLength = mnemonicArray.length * 11;
        boolean[] concatenatedBin = new boolean[concatenatedBinLength];
        var wordlist = Helper.loadWordlist(RESOURCE_ENGLISH);

        for (int i = 0; i < mnemonicArray.length; i++) {
            int wordIndex = getWordIndex(wordlist, mnemonicArray[i]);
            for (int j = 0; j < 11; ++j) {
                concatenatedBin[(i * 11) + j] = (wordIndex & (1 << (10 - j))) != 0;
            }
        }

        int checksumBinLength = concatenatedBinLength / 33;
        int entropyBinLength = concatenatedBinLength - checksumBinLength;
        var entropy = new byte[entropyBinLength / 8];
        for (int i = 0; i < entropy.length; i++) {
            for (int j = 0; j < 8; ++j) {
                if (concatenatedBin[(i * 8) + j]) {
                    entropy[i] |= (byte) (1 << (7 - j));
                }
            }
        }

        var checksumBin = Arrays.copyOfRange(concatenatedBin, entropyBinLength, concatenatedBinLength);
        if (!verifyEntropyChecksum(entropy, Helper.boolArrayToString(checksumBin))) {
            throw new IllegalArgumentException("Failed checksum.");
        }

        return entropy;
    }

    private static String createEntropyChecksum(byte[] entropy) {
        var checksum = Hash.sha256(entropy);
        var checksumBin = Hex.parse(checksum).toBin();
        return Helper.zfill(256, checksumBin).substring(0, entropy.length * 8 / 32);
    }

    private static boolean verifyEntropyChecksum(byte[] entropy, String checksum) {
        var checksumCheck = createEntropyChecksum(entropy);
        return checksumCheck.equals(checksum);
    }

    public static byte[] mnemonicToSeed(String mnemonicSentence, String passphrase) {
        var mnemonicArray = getMnemonicArray(mnemonicSentence);

        var wordlist = Helper.loadWordlist(RESOURCE_ENGLISH);
        for (int i = 0; i < mnemonicArray.length; i++) {
            getWordIndex(wordlist, mnemonicArray[i]);
        }

        if (passphrase == null) {
            passphrase = "";
        }
        passphrase = "mnemonic" + passphrase;

        return Pbkdf2.derive(String.join(" ", mnemonicArray), passphrase, PBKDF2_ROUNDS, DERIVED_KEY_LENGTH);
    }

    private static String[] getMnemonicArray(String mnemonicSentence) {
        var mnemonicArray = mnemonicSentence.split(" ");
        if (mnemonicArray.length != 12 && mnemonicArray.length != 15 && mnemonicArray.length != 18 && mnemonicArray.length != 21 && mnemonicArray.length != 24) {
            throw new IllegalArgumentException(String.format("Number of words must be one of the following: 12, 15, 18, 21 or 24, but it is not (%s).", mnemonicArray.length));
        }
        return mnemonicArray;
    }

    private static int getWordIndex(ArrayList<String> wordlist, String mnemonicWord) {
        int wordIndex = Collections.binarySearch(wordlist, mnemonicWord);
        if (wordIndex < 0) {
            throw new IllegalArgumentException(String.format("Unable to find '%s' in word list.", mnemonicWord));
        }
        return wordIndex;
    }

    public static boolean isWordValid(String mnemonicWord) {
        try {
            var wordlist = Helper.loadWordlist(RESOURCE_ENGLISH);
            getWordIndex(wordlist, mnemonicWord);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSentenceValid(String mnemonicSentence) {
        try {
            var mnemonicArray = getMnemonicArray(mnemonicSentence);
            var wordlist = Helper.loadWordlist(RESOURCE_ENGLISH);
            for (int i = 0; i < mnemonicArray.length; i++) {
                getWordIndex(wordlist, mnemonicArray[i]);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String seedToExtendedKey(byte[] seed, Int xkeyPrefix) {
        if (seed.length != DERIVED_KEY_LENGTH) {
            log.warning(String.format("Provided seed should have length of %s, not %s.", DERIVED_KEY_LENGTH, seed.length));
        }

        var bitcoinSeedMac = Hash.hmacS512Init("Bitcoin seed".getBytes());
        bitcoinSeedMac.update(seed);
        var bitcoinSeed = bitcoinSeedMac.doFinal();

        var xkey = xkeyPrefix.toBytes(); // Version
        xkey = Bytes.add(xkey, Bytes.initFill(9, (byte) 0x00)); // Depth, parent fingerprint and child number
        xkey = Bytes.add(xkey, Arrays.copyOfRange(bitcoinSeed, 32, bitcoinSeed.length)); // Chain code
        var masterKey = Bytes.add(new byte[]{0x00}, Arrays.copyOfRange(bitcoinSeed, 0, 32));
        if (ExtendedKey.isKeyPrivate(xkeyPrefix.toBytes())) {
            xkey = Bytes.add(xkey, masterKey); // Master key
        } else {
            var neutralMasterKey = PrivateKey.parse(masterKey).getPoint().sec(true);
            xkey = Bytes.add(xkey, neutralMasterKey); // Neutral master key
        }
        var hashedXprv = Hash.hash256(xkey);
        xkey = Bytes.add(xkey, Arrays.copyOfRange(hashedXprv, 0, 4)); // Checksum

        return Base58.encode(xkey);
    }
}
