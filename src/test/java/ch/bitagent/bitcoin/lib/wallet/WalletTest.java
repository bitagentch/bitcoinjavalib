package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.network.Electrum;
import ch.bitagent.bitcoin.lib.tx.Tx;
import ch.bitagent.bitcoin.lib.tx.TxIn;
import ch.bitagent.bitcoin.lib.tx.TxOut;
import ch.bitagent.bitcoin.lib.tx.Utxo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private static final Logger log = Logger.getLogger(WalletTest.class.getSimpleName());

    @Test
    void createMnemonic() {
        var sentence = Wallet.createMnemonic(128);
        log.info(sentence);
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
    void mnemonicSentenceSignVerify() {
        var mnemonicSentence = "dove labor word syrup speed panther flash episode forest dice measure ankle";
        var wallet = Wallet.parse(mnemonicSentence, null);
        assertEquals(20, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
        var address = wallet.getAddressList0().get(0).getAddressString();
        var message = "Test";
        assertEquals("bc1qnp48lrju5sjwajt6hvwp932vlq28s7nrvnuxwg", address);
        var signature = wallet.signMessage(address, message);
        assertEquals("H2XnmPZT5aifKvmCQhOQta0OE4Sz/66COfZf9BZ/2ZJOOfOCJGX/VeAXS+dQpbtuDqhJ+jVczeUs9/nF4F6fj7s=", signature);
        assertTrue(wallet.verifyMessage(address, signature, message));
    }

    @Test
    void testWallet() {
        var mnemonicSentence = "crowd surround reason item word jacket neither shoot find endorse gain snow";
        var wallet = Wallet.parse(mnemonicSentence, null);
        var address = wallet.getAddressList0().get(0).getAddressString();
        log.info(address);
        var message = "I confirm my iban and my bitcoin wallet. [test]";
        var signature = wallet.signMessage(address, message);
        log.info(signature);
        assertTrue(wallet.verifyMessage(address, signature, message));
    }

    @Disabled(value = "manual test")
    @Test
    void
    createSegwitTx() {
        var mnemonicSentence = "";
        var wallet = Wallet.parse(mnemonicSentence, null);
        wallet.history();
        assertFalse(wallet.getUtxoList().isEmpty());

        long inTxAmount = 0L;
        List<TxIn> inTxList = new ArrayList<>();
        for (Utxo utxo : wallet.getUtxoList()) {
            log.info(utxo.toString());
            if (utxo.getHeight() > 0) {
                inTxList.add(new TxIn(utxo));
                inTxAmount += utxo.getValue();
            }
        }
        var spendAddress = wallet.nextReceiveAddress();
        var spendTxOut = new TxOut(Int.parse(inTxAmount), spendAddress.scriptPubkey());

        var version = 2;
        var electrum = new Electrum();
        var heigth = electrum.height();
        var tx = new Tx(Int.parse(version), inTxList, List.of(spendTxOut), Int.parse(heigth), false, true);
        for (int i = 0; i < inTxList.size(); i++) {
            assertTrue(tx.signInput(i, wallet.getPrivateKeyForChangeIndex(inTxList.get(i).getUtxo().getChangeIndex())));
        }
        log.info(String.format("size %sB/%swu/%svB", tx.sizeBytes(), tx.sizeWeightUnits(), tx.sizeVirtualBytes()));

        var feeEstimate = electrum.estimateFee(1);
        var feeAmount = tx.sizeVirtualBytes() * feeEstimate;
        spendTxOut = new TxOut(Int.parse(inTxAmount - feeAmount), spendAddress.scriptPubkey());
        tx = new Tx(Int.parse(version), inTxList, List.of(spendTxOut), Int.parse(heigth), false, true);
        for (int i = 0; i < inTxList.size(); i++) {
            assertTrue(tx.signInput(i, wallet.getPrivateKeyForChangeIndex(inTxList.get(i).getUtxo().getChangeIndex())));
        }
        log.info(String.format("fee %s/%s/%s", feeEstimate, feeAmount, tx.fee()));
        assertTrue(feeAmount > 0);
        assertEquals(feeAmount, tx.fee().intValue());
        log.info(tx.toString());

        log.info(String.format("broadcast transaction%n%s", tx.hexString()));
        fail();
        var txId = electrum.broadcastTransaction(tx.hexString());
        log.info(txId);
        assertEquals(tx.id(), txId);
    }
}