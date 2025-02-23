package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.network.Electrum;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class TxTest {

    private static final Logger log = Logger.getLogger(TxTest.class.getSimpleName());

    @Test
    void fetch() {
        Tx tx = TxFetcher.fetch("184d3393cea44574a7b521575878a5485fc3c18e4920808235c8f58264c1dc48", true, null);
        String txString = tx.hexString();
        log.fine(txString);
        assertEquals("0100000001e047a4dfa9980e1533ef990f25ccd387922b2f9b8ed00df064684ff33b3fe52e000000006a473044022007fefcd11b9b715b45ecfe02eba011d785a9364c08af60297d6aa8a4ccc95c3702201cac5e121d07545275b510264c625d9cdaf9e4b58652aad0226a47c96729dc270121021f955d36390a38361530fb3724a835f4f504049492224a028fb0ab8c063511a7ffffffff02c0441105000000001976a914d23541bd04c58a1265e78be912e63b2557fb439088aca0860100000000001976a91456d95dc3f2414a210efb7188d287bff487df96c688ac00000000", txString);
    }

    @Test
    void parseVersion() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, true);
        assertEquals(Int.parse(1), tx.getVersion());
    }

    @Test
    void parseInputs() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, true);
        assertEquals(1, tx.getTxIns().size());
        var want = Hex.parse("d1c789a9c60383bf715f3f6ad9d14b91fe55f3deb369fe5d9280cb1a01793f81");
        assertEquals(want, tx.getTxIns().get(0).getPrevTx());
        assertEquals(Int.parse(0), tx.getTxIns().get(0).getPrevIndex());
        want = Hex.parse("6b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a");
        assertEquals(want, Hex.parse(tx.getTxIns().get(0).getScriptSig().serialize()));
        assertEquals(Hex.parse("fffffffe"), tx.getTxIns().get(0).getSequence());
    }

    @Test
    void parseOutputs() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, true);
        assertEquals(2, tx.getTxOuts().size());
        var want = Int.parse(32454049);
        assertEquals(want, tx.getTxOuts().get(0).getAmount());
        want = Hex.parse("1976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac");
        assertEquals(want, Hex.parse(tx.getTxOuts().get(0).getScriptPubkey().serialize()));
        want = Int.parse(10011545);
        assertEquals(want, tx.getTxOuts().get(1).getAmount());
        want = Hex.parse("1976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac");
        assertEquals(want, Hex.parse(tx.getTxOuts().get(1).getScriptPubkey().serialize()));
    }

    @Test
    void parseLocktime() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, true);
        assertEquals(Int.parse(410393), tx.getLocktime());
    }

    @Test
    void serialize() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, null);
        assertEquals(rawTx, tx.hexString());
    }

    @Test
    void inputValue() {
        var txHash = Hex.parse("d1c789a9c60383bf715f3f6ad9d14b91fe55f3deb369fe5d9280cb1a01793f81");
        var index = Int.parse(0);
        var want = Int.parse(42505594);
        var txIn = new TxIn(txHash, index, null, null);
        assertEquals(want, txIn.value(null, null));
    }

    @Test
    void inputPubkey() {
        var txHash = Hex.parse("d1c789a9c60383bf715f3f6ad9d14b91fe55f3deb369fe5d9280cb1a01793f81");
        var index = Int.parse(0);
        var txIn = new TxIn(txHash, index, null, null);
        var want = Bytes.hexStringToByteArray("1976a914a802fc56c704ce87c42d7c92eb75e7896bdc41ae88ac");
        assertArrayEquals(want, txIn.scriptPubkey(null, null).serialize());
    }

    @Test
    void fee() {
        var rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        var tx = Tx.parse(rawTx, false);
        assertEquals(Int.parse(40000), tx.fee(null));

        rawTx = "010000000456919960ac691763688d3d3bcea9ad6ecaf875df5339e148a1fc61c6ed7a069e010000006a47304402204585bcdef85e6b1c6af5c2669d4830ff86e42dd205c0e089bc2a821657e951c002201024a10366077f87d6bce1f7100ad8cfa8a064b39d4e8fe4ea13a7b71aa8180f012102f0da57e85eec2934a82a585ea337ce2f4998b50ae699dd79f5880e253dafafb7feffffffeb8f51f4038dc17e6313cf831d4f02281c2a468bde0fafd37f1bf882729e7fd3000000006a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937feffffff567bf40595119d1bb8a3037c356efd56170b64cbcc160fb028fa10704b45d775000000006a47304402204c7c7818424c7f7911da6cddc59655a70af1cb5eaf17c69dadbfc74ffa0b662f02207599e08bc8023693ad4e9527dc42c34210f7a7d1d1ddfc8492b654a11e7620a0012102158b46fbdff65d0172b7989aec8850aa0dae49abfb84c81ae6e5b251a58ace5cfeffffffd63a5e6c16e620f86f375925b21cabaf736c779f88fd04dcad51d26690f7f345010000006a47304402200633ea0d3314bea0d95b3cd8dadb2ef79ea8331ffe1e61f762c0f6daea0fabde022029f23b3e9c30f080446150b23852028751635dcee2be669c2a1686a4b5edf304012103ffd6f4a67e94aba353a00882e563ff2722eb4cff0ad6006e86ee20dfe7520d55feffffff0251430f00000000001976a914ab0c0b2e98b1ab6dbf67d4750b0a56244948a87988ac005a6202000000001976a9143c82d7df364eb6c75be8c80df2b3eda8db57397088ac46430600";
        tx = Tx.parse(rawTx, false);
        assertEquals(Int.parse(140500), tx.fee(null));
    }

    @Test
    void sigHash() {
        var txId = "452c629d67e41baec3ac6f04fe744b4b9617f8f859c63b3002f8684e7a4fee03";
        var tx = TxFetcher.fetch(txId, false, null);
        var want = Hex.parse("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6");
        assertEquals(want, tx.sigHash(0, null, null));
        var electrum = new Electrum();
        assertNotNull(electrum.getTransaction(txId));
        assertNotNull(electrum.getTransactionVerbose(txId));
        assertNull(electrum.broadcastTransaction(tx.hexString()));
    }

    @Test
    void verifyP2pkh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("452c629d67e41baec3ac6f04fe744b4b9617f8f859c63b3002f8684e7a4fee03", false, cache);
        assertTrue(tx.verify(cache));
        tx = TxFetcher.fetch("5418099cc755cb9dd3ebc6cf1a7888ad53a1a3beb5a025bce89eb1bf7f1650a2", true, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void verifyP2sh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("46df1a9484d0a81d03ce0ee543ab6e1a23ed06175c104a178268fad381216c2b", false, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void testVerifyP2wpkh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("d869f854e1f8788bcff294cc83b280942a8c728de71eb709a2c29d10bfe21b7c", true, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void testVerifyP2shP2wpkh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("c586389e5e4b3acb9d6c8be1c19ae8ab2795397633176f5a6442a261bbdefc3a", false, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void testVerifyP2wsh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("78457666f82c28aa37b74b506745a7c7684dc7842a52a457b09f09446721e11c", true, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void testVerifyP2shP2wsh() {
        Map<String, String> cache = new HashMap<>();
        var tx = TxFetcher.fetch("954f43dbb30ad8024981c07d1f5eb6c9fd461e2cf1760dd1283f052af746fc88", true, cache);
        assertTrue(tx.verify(cache));
    }

    @Test
    void signInput() {
        var privateKey = new PrivateKey(Int.parse(8675309));
        var tx = Tx.parse("010000000199a24308080ab26e6fb65c4eccfadf76749bb5bfa8cb08f291320b3c21e56f0d0d00000000ffffffff02408af701000000001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac80969800000000001976a914507b27411ccf7f16f10297de6cef3f291623eddf88ac00000000", true);
        assertTrue(tx.signInput(0, privateKey, null));

        var want = "010000000199a24308080ab26e6fb65c4eccfadf76749bb5bfa8cb08f291320b3c21e56f0d0d0000006b4830450221008ed46aa2cf12d6d81065bfabe903670165b538f65ee9a3385e6327d80c66d3b502203124f804410527497329ec4715e18558082d489b218677bd029e7fa306a72236012103935581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff02408af701000000001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac80969800000000001976a914507b27411ccf7f16f10297de6cef3f291623eddf88ac00000000";
        assertEquals(want, tx.hexString());
    }

    @Test()
    void isCoinbase() {
        var rawTx = "01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00ffffffff01faf20b58000000001976a914338c84849423992471bffb1a54a8d9b1d69dc28a88ac00000000";
        var tx = Tx.parse(rawTx, null);
        assertTrue(tx.isCoinbase());
    }

    @Test
    void coinbaseHeight() {
        var rawTx = "01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00ffffffff01faf20b58000000001976a914338c84849423992471bffb1a54a8d9b1d69dc28a88ac00000000";
        var tx = Tx.parse(rawTx, null);
        assertEquals(Int.parse(465879), tx.coinbaseHeight());

        rawTx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        tx = Tx.parse(rawTx, null);
        assertNull(tx.coinbaseHeight());
    }
}