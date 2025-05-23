package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.script.OpCodeNames;
import ch.bitagent.bitcoin.lib.script.Script;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Chapter6Test {

    @Test
    void example1() {
        var z = Hex.parse("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        var sec = Hex.parse("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var sig = Hex.parse("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601");
        var scriptPubkey = new Script(List.of(sec.toScriptCmd(), OpCodeNames.OP_172_CHECKSIG.toScriptCmd()));
        var scriptSig = new Script(List.of(sig.toScriptCmd()));
        var combinedScript = scriptSig.add(scriptPubkey);
        assertTrue(combinedScript.evaluate(z, null));
    }

    @Test
    void exercise3() {
        var scriptPubkey = new Script(List.of(OpCodeNames.OP_118_DUP.toScriptCmd(), OpCodeNames.OP_118_DUP.toScriptCmd(), OpCodeNames.OP_147_ADD.toScriptCmd(), OpCodeNames.OP_147_ADD.toScriptCmd(), OpCodeNames.OP_86_6.toScriptCmd(), OpCodeNames.OP_135_EQUAL.toScriptCmd()));
        var scriptSig = new Script(List.of(OpCodeNames.OP_82_2.toScriptCmd()));
        var combinedScript = scriptSig.add(scriptPubkey);
        assertTrue(combinedScript.evaluate(Hex.parse("00"), null));
    }

    @Test
    void exercise4() {
        var scriptPubkey = new Script(List.of(OpCodeNames.OP_110_2DUP.toScriptCmd(), OpCodeNames.OP_135_EQUAL.toScriptCmd(), OpCodeNames.OP_145_NOT.toScriptCmd(), OpCodeNames.OP_105_VERIFY.toScriptCmd(), OpCodeNames.OP_167_SHA1.toScriptCmd(), OpCodeNames.OP_124_SWAP.toScriptCmd(), OpCodeNames.OP_167_SHA1.toScriptCmd(), OpCodeNames.OP_135_EQUAL.toScriptCmd()));
        var c1 = Hex.parse("255044462d312e330a25e2e3cfd30a0a0a312030206f626a0a3c3c2f57696474682032203020522f4865696768742033203020522f547970652034203020522f537562747970652035203020522f46696c7465722036203020522f436f6c6f7253706163652037203020522f4c656e6774682038203020522f42697473506572436f6d706f6e656e7420383e3e0a73747265616d0affd8fffe00245348412d3120697320646561642121212121852fec092339759c39b1a1c63c4c97e1fffe017f46dc93a6b67e013b029aaa1db2560b45ca67d688c7f84b8c4c791fe02b3df614f86db1690901c56b45c1530afedfb76038e972722fe7ad728f0e4904e046c230570fe9d41398abe12ef5bc942be33542a4802d98b5d70f2a332ec37fac3514e74ddc0f2cc1a874cd0c78305a21566461309789606bd0bf3f98cda8044629a1");
        var c2 = Hex.parse("255044462d312e330a25e2e3cfd30a0a0a312030206f626a0a3c3c2f57696474682032203020522f4865696768742033203020522f547970652034203020522f537562747970652035203020522f46696c7465722036203020522f436f6c6f7253706163652037203020522f4c656e6774682038203020522f42697473506572436f6d706f6e656e7420383e3e0a73747265616d0affd8fffe00245348412d3120697320646561642121212121852fec092339759c39b1a1c63c4c97e1fffe017346dc9166b67e118f029ab621b2560ff9ca67cca8c7f85ba84c79030c2b3de218f86db3a90901d5df45c14f26fedfb3dc38e96ac22fe7bd728f0e45bce046d23c570feb141398bb552ef5a0a82be331fea48037b8b5d71f0e332edf93ac3500eb4ddc0decc1a864790c782c76215660dd309791d06bd0af3f98cda4bc4629b1");
        var scriptSig = new Script(List.of(c1.toScriptCmd(), c2.toScriptCmd()));
        var combinedScript = scriptSig.add(scriptPubkey);
        assertTrue(combinedScript.evaluate(Hex.parse("00"), null));
    }
}
