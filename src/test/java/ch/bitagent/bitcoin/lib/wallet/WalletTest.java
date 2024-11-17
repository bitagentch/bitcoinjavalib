package ch.bitagent.bitcoin.lib.wallet;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    private static final Logger log = Logger.getLogger(WalletTest.class.getSimpleName());

    @Test
    void privkey() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        var wallet = Wallet.parse(extendedPrivkey);
        log.info(wallet.toString());
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
    }

    @Test
    void pubkey() {
        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        var wallet = Wallet.parse(extendedPubkey);
        log.info(wallet.toString());
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
    }
}