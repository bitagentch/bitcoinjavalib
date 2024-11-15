package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MnemonicSentenceTest {

    @Test
    void generateEntropyToMnemonic() {
        var entropy = MnemonicSentence.generateEntropy(128);
        assertEquals(16, entropy.length);
        var mnemonic = MnemonicSentence.entropyToMnemonic(entropy);
        assertEquals(12, mnemonic.split(" ").length);

        entropy = MnemonicSentence.generateEntropy(160);
        assertEquals(20, entropy.length);
        mnemonic = MnemonicSentence.entropyToMnemonic(entropy);
        assertEquals(15, mnemonic.split(" ").length);

        entropy = MnemonicSentence.generateEntropy(192);
        assertEquals(24, entropy.length);
        mnemonic = MnemonicSentence.entropyToMnemonic(entropy);
        assertEquals(18, mnemonic.split(" ").length);

        entropy = MnemonicSentence.generateEntropy(224);
        assertEquals(28, entropy.length);
        mnemonic = MnemonicSentence.entropyToMnemonic(entropy);
        assertEquals(21, mnemonic.split(" ").length);

        entropy = MnemonicSentence.generateEntropy(256);
        assertEquals(32, entropy.length);
        mnemonic = MnemonicSentence.entropyToMnemonic(entropy);
        assertEquals(24, mnemonic.split(" ").length);
    }

    @Test
    void bip39_vector_english() {
        var entropy = "00000000000000000000000000000000";
        var mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        var passphrase = "TREZOR";
        var seed = "c55257c360c07c72029aebc1b53c05ed0362ada38ead3e3e9efa3708e53495531f09a6987599d18264c1e1c92f2cf141630c7a3c4ab7c81b2f001698e7463b04";
        var xprv = "xprv9s21ZrQH143K3h3fDYiay8mocZ3afhfULfb5GX8kCBdno77K4HiA15Tg23wpbeF1pLfs1c5SPmYHrEpTuuRhxMwvKDwqdKiGJS9XFKzUsAF";
        bip39_vector_assert(mnemonic, entropy, seed, passphrase, xprv);

        entropy = "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f";
        mnemonic = "legal winner thank year wave sausage worth useful legal winner thank yellow";
        seed = "2e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6fa457fe1296106559a3c80937a1c1069be3a3a5bd381ee6260e8d9739fce1f607";
        xprv = "xprv9s21ZrQH143K2gA81bYFHqU68xz1cX2APaSq5tt6MFSLeXnCKV1RVUJt9FWNTbrrryem4ZckN8k4Ls1H6nwdvDTvnV7zEXs2HgPezuVccsq";
        bip39_vector_assert(mnemonic, entropy, seed, passphrase, xprv);

        entropy = "80808080808080808080808080808080";
        mnemonic = "letter advice cage absurd amount doctor acoustic avoid letter advice cage above";
        seed = "d71de856f81a8acc65e6fc851a38d4d7ec216fd0796d0a6827a3ad6ed5511a30fa280f12eb2e47ed2ac03b5c462a0358d18d69fe4f985ec81778c1b370b652a8";
        xprv = "xprv9s21ZrQH143K2shfP28KM3nr5Ap1SXjz8gc2rAqqMEynmjt6o1qboCDpxckqXavCwdnYds6yBHZGKHv7ef2eTXy461PXUjBFQg6PrwY4Gzq";
        bip39_vector_assert(mnemonic, entropy, seed, passphrase, xprv);

        entropy = "ffffffffffffffffffffffffffffffff";
        mnemonic = "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong";
        seed = "ac27495480225222079d7be181583751e86f571027b0497b5b5d11218e0a8a13332572917f0f8e5a589620c6f15b11c61dee327651a14c34e18231052e48c069";
        xprv = "xprv9s21ZrQH143K2V4oox4M8Zmhi2Fjx5XK4Lf7GKRvPSgydU3mjZuKGCTg7UPiBUD7ydVPvSLtg9hjp7MQTYsW67rZHAXeccqYqrsx8LcXnyd";
        bip39_vector_assert(mnemonic, entropy, seed, passphrase, xprv);
    }

    private static void bip39_vector_assert(String mnemonic, String entropy, String seed, String passphrase, String xprv) {
        assertEquals(mnemonic, MnemonicSentence.entropyToMnemonic(Bytes.hexStringToByteArray(entropy)));
        assertEquals(entropy, Bytes.byteArrayToHexString(MnemonicSentence.mnemonicToEntropy(mnemonic)));
        assertEquals(seed, Bytes.byteArrayToHexString(MnemonicSentence.mnemonicToSeed(mnemonic, passphrase)));
        assertEquals(xprv, MnemonicSentence.seedToExtendedPrivateKey(Bytes.hexStringToByteArray(seed)));
    }
}