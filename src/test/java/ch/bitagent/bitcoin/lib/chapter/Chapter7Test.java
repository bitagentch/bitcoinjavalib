package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.ecc.*;
import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Helper;
import ch.bitagent.bitcoin.lib.script.Script;
import ch.bitagent.bitcoin.lib.script.ScriptCmd;
import ch.bitagent.bitcoin.lib.tx.Tx;
import ch.bitagent.bitcoin.lib.tx.TxFetcher;
import ch.bitagent.bitcoin.lib.tx.TxIn;
import ch.bitagent.bitcoin.lib.tx.TxOut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Chapter7Test {

    private static final Logger log = Logger.getLogger(Chapter7Test.class.getSimpleName());

    @Test
    void example1() {
        var rawTx = Hex.parse("0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600");
        var transaction = Tx.parse(new ByteArrayInputStream(rawTx.toBytes()), false);
        assertTrue(transaction.fee().ge(Int.parse(0)));
    }

    @Test
    void example2() {
        var sec = Hex.parse("0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a");
        var der = Hex.parse("3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed");
        var z = Hex.parse("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6");
        var point = S256Point.parse(sec.toBytes());
        var signature = Signature.parse(der.toBytes());
        assertTrue(point.verify(z, signature));
    }

    @Test
    void example3() {
        var modifiedTx = Hex.parse("0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000001976a914a802fc56c704ce87c42d7c92eb75e7896bdc41ae88acfeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac1943060001000000");
        var h256 = Helper.hash256(modifiedTx.toBytes());
        var z = Hex.parse(h256);
        assertEquals("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6", z.toString());
    }

    @Test
    void example4() {
        var sec = Hex.parse("0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a");
        var der = Hex.parse("3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed");
        var z = Hex.parse("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6");
        var point = S256Point.parse(sec.toBytes());
        var signature = Signature.parse(der.toBytes());
        assertTrue(point.verify(z, signature));
    }

    @DisabledIfSystemProperty(named = "disabled", matches = "true", disabledReason = "long running, see [^1] page 135")
    @Test
    void verifyBiggestTransaction() {
        var tx = TxFetcher.fetch("bb41a757f405890fb0f5856228e23b715702d714d59bf2b1feb70d8b2b4e3e08", false, false);
        assertTrue(tx.verify());
    }

    @Test
    void example5() {
        var prevTx = Hex.parse("0d6fe5213c0b3291f208cba8bfb59b7476dffacc4e5cb66f6eb20a080843a299");
        var prevIndex = Int.parse(13);
        var txIn = new TxIn(prevTx, prevIndex, null, null);
        var changeAmount = Int.parse(Helper.btcToSat(0.33));
        var changeH160 = Base58.decode("mzx5YhAH9kNHtcN481u6WkjeHjYtVeKVh2");
        var changeScript = Script.p2pkhScript(changeH160);
        var changeOutput = new TxOut(changeAmount, changeScript);
        var targetAmount = Int.parse(Helper.btcToSat(0.1));
        var target_h160 = Base58.decode("mnrVtF8DWjMu839VW3rBfgYaAfKk8983Xf");
        var targetScript = Script.p2pkhScript(target_h160);
        var targetOutput = new TxOut(targetAmount, targetScript);
        var tx = new Tx(Int.parse(1), List.of(txIn), List.of(changeOutput, targetOutput), Int.parse(0), true, null);
        log.fine(String.format("tx %s", tx));
        assertEquals("cd30a8da777d28ef0e61efe68a9f7c559c1d3e5bcd7b265c850ccb4068598d11", tx.id());
        assertEquals(Int.parse(1), tx.getVersion());
        assertEquals("[txin 0d6fe5213c0b3291f208cba8bfb59b7476dffacc4e5cb66f6eb20a080843a299:13::ffffffff]", tx.getTxIns().toString());
        assertEquals("[txout 33000000:OP_DUP OP_HASH160 d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f OP_EQUALVERIFY OP_CHECKSIG, txout 10000000:OP_DUP OP_HASH160 507b27411ccf7f16f10297de6cef3f291623eddf OP_EQUALVERIFY OP_CHECKSIG]", tx.getTxOuts().toString());
        assertEquals(Int.parse(0), tx.getLocktime());
    }

    @Test
    void example6() {
        // see example1
        var rawTx = Hex.parse("0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600");
        var transaction = Tx.parse(new ByteArrayInputStream(rawTx.toBytes()), false);
        var z = transaction.sigHash(0, null);
        var privateKey = new PrivateKey(Int.parse(8675309));
        var der = privateKey.sign(z).der();
        var sig = new ScriptCmd(Bytes.add(der, Helper.SIGHASH_ALL.toBytes(1)));
        var sec = new ScriptCmd(privateKey.getPoint().sec(null));
        var scriptSig = new Script(List.of(sig, sec));
        transaction.getTxIns().get(0).setScriptSig(scriptSig);
        var tser = Hex.parse(transaction.serialize());
        assertEquals("0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006a47304402207db2402a3311a3b845b038885e3dd889c08126a8570f26a844e3e4049c482a11022010178cdca4129eacbeab7c44648bf5ac1f9cac217cd609d216ec2ebc8d242c0a012103935581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67feffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600", tser.toString());
    }

    /**
     * https://en.bitcoin.it/wiki/Testnet
     * https://blockstream.info/testnet/address/mn81594PzKZa9K3Jyy1ushpuEzrnTnxhVg
     */
    @Test
    void example7() {
        var secret =  Hex.parse(Bytes.changeOrder(Helper.hash256("Jimmy Song secret".getBytes())));
        var privateKey = new PrivateKey(secret);
        var testnetAddress = privateKey.getPoint().address(null, true);
        log.fine(String.format("testnet address %s", testnetAddress));
        assertEquals("mn81594PzKZa9K3Jyy1ushpuEzrnTnxhVg", testnetAddress);
    }

    /**
     * https://blockstream.info/testnet/tx/push
     */
    @Test
    void exercise4() {
        var prevTx = Hex.parse("75a1c4bc671f55f626dda1074c7725991e6f68b8fcefcfca7b64405ca3b45f1c");
        var prevIndex = Int.parse(1);
        var targetAddress = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        var targetAmount = 0.01;
        var changeAddress = "n2Bdt8wJdsthLUpUh6zwZez8AzjWmfe6fi";
        var changeAmount = 0.009;
        var secret = Int.parse(8675309);
        var priv = new PrivateKey(secret);
        var txIns = new ArrayList<TxIn>();
        txIns.add(new TxIn(prevTx, prevIndex, null, null));
        var txOuts = new ArrayList<TxOut>();
        var h160 = Base58.decode(targetAddress);
        var scriptPubkey = Script.p2pkhScript(h160);
        var targetSatoshis = Int.parse(Helper.btcToSat(targetAmount));
        txOuts.add(new TxOut(targetSatoshis, scriptPubkey));
        h160 = Base58.decode(changeAddress);
        scriptPubkey = Script.p2pkhScript(h160);
        var changeSatoshis = Int.parse(Helper.btcToSat(changeAmount));
        txOuts.add(new TxOut(changeSatoshis, scriptPubkey));
        var tx = new Tx(Int.parse(1), txIns, txOuts, Int.parse(0), true, null);
        assertTrue(tx.signInput(0, priv));

        String want = "01000000011c5fb4a35c40647bcacfeffcb8686f1e9925774c07a1dd26f6551f67bcc4a175010000006b4830450221009bd8eef2adadce92d61ae58fdd87c7337ef9f6bdc6e99222e1ba92a48ce9e07302206fd765e299fcf24bb6482d8f8ee269dd3fe0c944fe34d168cf4436b0b0891890012103935581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff0240420f00000000001976a914ad346f8eb57dee9a37981716e498120ae80e44f788aca0bb0d00000000001976a914e2b35e4ac771118a46d0c731e1dd7efcd7d9b6a388ac00000000";
        Tx txWant = Tx.parse(new ByteArrayInputStream(Hex.parse(want).toBytes()), true);
        assertEquals(txWant.getVersion(), tx.getVersion());
        assertEquals(txWant.getTxIns().size(), tx.getTxIns().size());
        for (int i = 0; i < txWant.getTxIns().size(); i++) {
            TxIn txInWant = txWant.getTxIns().get(i);
            TxIn txIn = tx.getTxIns().get(i);
            assertEquals(txInWant.getPrevTx(), txIn.getPrevTx());
            assertEquals(txInWant.getPrevIndex(), txIn.getPrevIndex());
            assertEquals(txInWant.getScriptSig().toString(), txIn.getScriptSig().toString());
            assertEquals(txInWant.getSequence(), txIn.getSequence());
        }
        assertEquals(txWant.getTxOuts().size(), tx.getTxOuts().size());
        for (int i = 0; i < txWant.getTxOuts().size(); i++) {
            TxOut txOutWant = txWant.getTxOuts().get(i);
            TxOut txOut = tx.getTxOuts().get(i);
            assertEquals(txOutWant.getAmount(), txOut.getAmount());
            assertEquals(txOutWant.getScriptPubkey().toString(), txOut.getScriptPubkey().toString());
        }
        assertEquals(txWant.getLocktime(), tx.getLocktime());
        assertEquals(txWant.getTestnet(), tx.getTestnet());
        assertEquals(want, Bytes.byteArrayToHexString(tx.serialize()));
    }
}
