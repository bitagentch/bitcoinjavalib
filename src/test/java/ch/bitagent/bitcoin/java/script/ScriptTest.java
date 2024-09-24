package ch.bitagent.bitcoin.java.script;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.helper.Base58;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScriptTest {

    @Test
    void parse() {
        var streamHex = Hex.parse("6a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937");
        var scriptPubkey = new ByteArrayInputStream(streamHex.toBytes());
        var script = Script.parse(scriptPubkey);
        var want = Hex.parse("304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a71601");
        assertEquals(want, Hex.parse(script.getCmds().get(0).getElement()));
        want = Hex.parse("035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937");
        assertEquals(want, Hex.parse(script.getCmds().get(1).getElement()));
    }

    @Test
    void serialize() {
        var want = Hex.parse("6a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937");
        var scriptPubkey = new ByteArrayInputStream(want.toBytes());
        var script = Script.parse(scriptPubkey);
        assertEquals(want, Hex.parse(script.serialize()));
    }

    @Test
    void address() {
        var address1 = "1BenRpVUFK65JFWcQSuHnJKzc4M8ZP8Eqa";
        var h160 = Base58.decode(address1);
        var p2pkhScriptPubkey = Script.p2pkhScript(h160);
        assertEquals(p2pkhScriptPubkey.address(null), address1);
        var address2 = "mrAjisaT4LXL5MzE81sfcDYKU3wqWSvf9q";
        assertEquals(p2pkhScriptPubkey.address(true), address2);
        var address3 = "3CLoMMyuoDQTPRD3XYZtCvgvkadrAdvdXh";
        h160 = Base58.decode(address3);
        var p2shScriptPubkey = Script.p2shScript(h160);
        assertEquals(p2shScriptPubkey.address(null), address3);
        var address4 = "2N3u1R6uwQfuobCqbCgBkpsgBxvr1tZpe7B";
        assertEquals(p2shScriptPubkey.address(true), address4);
    }

    @Test
    void p2pk() {
        var pubkey = Hex.parse("0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03F999b8643f656b412a3");
        var scriptPubkey = new Script(List.of(pubkey.toScriptCmd(), OpCodeNames.OP_172_CHECKSIG.toScriptCmd()));

        var sig = Hex.parse("304402204e45e16932b8af514961a1d3a1a25fdf3f4F7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901");
        var scriptSig = new Script(List.of(sig.toScriptCmd()));

        var combinedScript = scriptSig.add(scriptPubkey);
        assertFalse(combinedScript.evaluate(Hex.parse("00"), null));
    }

    @Test
    void p2pkh() {
        var hash = Hex.parse("a802fc56c704ce87c42d7c92eb75e7896bdc41ae");
        var scriptPubkey = new Script(List.of(OpCodeNames.OP_118_DUP.toScriptCmd(), OpCodeNames.OP_169_HASH160.toScriptCmd(), hash.toScriptCmd(), OpCodeNames.OP_136_EQUALVERIFY.toScriptCmd(), OpCodeNames.OP_172_CHECKSIG.toScriptCmd()));

        var sig = Hex.parse("3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01");
        var pubkey = Hex.parse("0349fc4e631e3624a545de3f89F5d8684c7b8138bd94bdd531d2e213bf016b278a");
        var scriptSig = new Script(List.of(sig.toScriptCmd(), pubkey.toScriptCmd()));

        var combinedScript = scriptSig.add(scriptPubkey);
        assertFalse(combinedScript.evaluate(Hex.parse("00"), null));
    }
}