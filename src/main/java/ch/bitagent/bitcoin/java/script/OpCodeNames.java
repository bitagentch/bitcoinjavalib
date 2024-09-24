package ch.bitagent.bitcoin.java.script;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;

public enum OpCodeNames {

    OP_0(Hex.parse("00"), "OP_0"),
    OP_81(Hex.parse("51"), "OP_1"),
    OP_82(Hex.parse("52"), "OP_2"),
    OP_86(Hex.parse("56"), "OP_6"),
    OP_91_11(Hex.parse("5b"), "OP_11"),
    OP_99_IF(Hex.parse("63"), "OP_IF"),
    OP_100_NOTIF(Hex.parse("64"), "OP_NOTIF"),
    OP_103_ELSE(Hex.parse("67"), "OP_ELSE"),
    OP_105_VERIFY(Hex.parse("69"), "OP_VERIFY"),
    OP_106_RETURN(Hex.parse("6a"), "OP_RETURN"),
    OP_107_TOALTSTACK(Hex.parse("6b"), "OP_TOALTSTACK"),
    OP_108_FROMALTSTACK(Hex.parse("6c"), "OP_FROMALTSTACK"),
    OP_109_2DROP(Hex.parse("6d"), "OP_2DROP"),
    OP_110_2DUP(Hex.parse("6e"), "OP_2DUP"),
    OP_118_DUP(Hex.parse("76"), "OP_DUP"),
    OP_124_SWAP(Hex.parse("7c"), "OP_SWAP"),
    OP_135_EQUAL(Hex.parse("87"), "OP_EQUAL"),
    OP_136_EQUALVERIFY(Hex.parse("88"), "OP_EQUALVERIFY"),
    OP_142_8E(Hex.parse("8e"), "OP_8E"),
    OP_145_NOT(Hex.parse("91"), "OP_NOT"),
    OP_147_ADD(Hex.parse("93"), "OP_ADD"),
    OP_167_SHA1(Hex.parse("a7"), "OP_SHA1"),
    OP_169_HASH160(Hex.parse("a9"), "OP_HASH160"),
    OP_170_HASH256(Hex.parse("aa"), "OP_HASH256"),
    OP_172_CHECKSIG(Hex.parse("ac"), "OP_CHECKSIG"),
    OP_173_CHECKSIGVERIFY(Hex.parse("ad"), "OP_CHECKSIGVERIFY"),
    OP_174_CHECKMULTISIG(Hex.parse("ae"), "OP_CHECKMULTISIG"),
    OP_175_CHECKMULTISIGVERIFY(Hex.parse("af"), "OP_CHECKMULTISIGVERIFY"),
    OP_184_B8(Hex.parse("b8"), "OP_B8"),
    OP_190_BE(Hex.parse("be"), "OP_BE"),
    OP_232_E8(Hex.parse("e8"), "OP_E8"),
    OP_250_FA(Hex.parse("fa"), "OP_FA"),
    OP_254_FE(Hex.parse("fe"), "OP_FE");

    private final Int code;

    private final String codeName;

    OpCodeNames(Int code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    public static OpCodeNames findByCode(Int code) {
        for (OpCodeNames value : values()) {
            if (value.code.eq(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("op_code not implemented yet! - %s/%s", code.intValue(), code.toString()));
    }

    public ScriptCmd toScriptCmd() {
        return new ScriptCmd(this);
    }

    public Int getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }
}
