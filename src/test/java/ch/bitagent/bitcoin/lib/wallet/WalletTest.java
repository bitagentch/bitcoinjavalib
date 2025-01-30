package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Properties;
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

    private static final String WALLET_DEV_FILENAME = "walletdev.properties";

    @Test
    void createMnemonic() {
        var sentence = Wallet.createMnemonic(128);
        log.info(sentence);
        assertEquals(12, sentence.split(" ").length);
    }

    @Test
    void privkey() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        var wallet = Wallet.parse(extendedPrivkey, 3);
        assertEquals(3, wallet.getAddressList0().size());
        assertEquals(3, wallet.getAddressList1().size());
        wallet.history(0);
        wallet.history(1);
        log.info(wallet.toString());
    }

    @Test
    void pubkey() {
        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        var wallet = Wallet.parse(extendedPubkey, 10);
        assertEquals(10, wallet.getAddressList0().size());
        assertEquals(10, wallet.getAddressList1().size());
    }

    @Test
    void mnemonicSentenceSignVerify() {
        var mnemonicSentence = Properties.getWalletMnemonic(Properties.WALLET_FILENAME, 1);
        var wallet = Wallet.parse(mnemonicSentence, null, Wallet.PURPOSE_NATIVE_SEGWIT, Wallet.COIN_TYPE_BITCOIN, 0, 10);
        assertEquals(10, wallet.getAddressList0().size());
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
        var mnemonicSentence = Properties.getWalletMnemonic(Properties.WALLET_FILENAME, 1);
        var wallet = Wallet.parse(mnemonicSentence, null, Wallet.PURPOSE_NATIVE_SEGWIT, Wallet.COIN_TYPE_BITCOIN, 0, 10);
        var address = wallet.getAddressList0().get(0).getAddressString();
        log.info(address);
        var message = "I confirm my iban and my bitcoin wallet. [test]";
        var signature = wallet.signMessage(address, message);
        log.info(signature);
        assertTrue(wallet.verifyMessage(address, signature, message));
    }

    @Disabled(value = "manual")
    @Test
    void
    createSegwitTx() {
        var mnemonicSentence = Properties.getWalletMnemonic(WALLET_DEV_FILENAME, 0);
        var wallet = Wallet.parse(mnemonicSentence, null, Wallet.PURPOSE_NATIVE_SEGWIT, Wallet.COIN_TYPE_BITCOIN, 0, 3);
        wallet.history(0);
        wallet.history(1);
        log.info(wallet.toString());
        assertFalse(wallet.getUtxoList().isEmpty());

        long utxoAmount = 0L;
        List<TxIn> utxoList = new ArrayList<>();
        for (Utxo utxo : wallet.getUtxoList()) {
            log.info(utxo.toString());
            if (utxo.getHeight() > 0) {
                utxoList.add(new TxIn(utxo));
                utxoAmount += utxo.getValue();
            }
        }
        assertTrue(utxoAmount > 0);

        var spendAddress = wallet.nextReceiveAddress();
        var spendTxOut = new TxOut(Int.parse(utxoAmount), spendAddress.scriptPubkey());

        var version = 2;
        var electrum = new Electrum();
        var heigth = electrum.height();
        var tx = new Tx(Int.parse(version), utxoList, List.of(spendTxOut), Int.parse(heigth), false, true);
        for (int i = 0; i < utxoList.size(); i++) {
            assertTrue(tx.signInput(i, wallet.getPrivateKeyForChangeIndex(utxoList.get(i).getUtxo().getChangeIndex())));
        }
        log.info(String.format("size %sB/%swu/%svB", tx.sizeBytes(), tx.sizeWeightUnits(), tx.sizeVirtualBytes()));

        var satsVB = electrum.estimateFee(1);
        var feeAmount = tx.sizeVirtualBytes() * satsVB;
        spendTxOut = new TxOut(Int.parse(utxoAmount - feeAmount), spendAddress.scriptPubkey());
        tx = new Tx(Int.parse(version), utxoList, List.of(spendTxOut), Int.parse(heigth), false, true);
        for (int i = 0; i < utxoList.size(); i++) {
            assertTrue(tx.signInput(i, wallet.getPrivateKeyForChangeIndex(utxoList.get(i).getUtxo().getChangeIndex())));
        }
        log.info(String.format("fee %ssatsVB/%ssats", satsVB, feeAmount));
        assertTrue(feeAmount > 0);
        assertEquals(feeAmount, tx.fee().intValue());
        log.info(tx.toString());

        log.info(String.format("broadcast transaction%n%s", tx.hexString()));
    }

    @Disabled(value = "manual")
    @Test
    void
    broadcastTransaction() {
        var electrum = new Electrum();
        var txId = electrum.broadcastTransaction("");
        log.info(txId);
        assertNotNull(txId);
    }
}