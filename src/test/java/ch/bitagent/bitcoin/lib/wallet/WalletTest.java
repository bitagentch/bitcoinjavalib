package ch.bitagent.bitcoin.lib.wallet;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    private static final Logger log = Logger.getLogger(WalletTest.class.getSimpleName());

    @Test
    void createMnemonic() {
        var sentence = Wallet.createMnemonic(128);
        assertEquals(12, sentence.split(" ").length);
    }

    @Test
    void privkey() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        var wallet = Wallet.parse(extendedPrivkey);
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
        wallet.history();
        log.info(wallet.toString());
    }

    @Test
    void pubkey() {
        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        var wallet = Wallet.parse(extendedPubkey);
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
    }

    @Test
    void mnemonicSentence() {
        var mnemonicSentence = "dove labor word syrup speed panther flash episode forest dice measure ankle";
        var wallet = Wallet.parse(mnemonicSentence, null);
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
    }
}