package ch.bitagent.bitcoin.lib.script;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;

/**
 * <p>OpCodeNames class.</p>
 */
public enum OpCodeNames {

    /** op 0 */
    OP_0(Hex.parse("00"), "OP_0"),
    /** op 20 */
    OP_20_PUSHBYTES_20(Hex.parse("14"), "OP_PUSHBYTES_20"),
    /** op 81 */
    OP_81(Hex.parse("51"), "OP_1"),
    /** op 82 */
    OP_82(Hex.parse("52"), "OP_2"),
    /** op 86 */
    OP_86(Hex.parse("56"), "OP_6"),
    /** op 91 */
    OP_91_11(Hex.parse("5b"), "OP_11"),
    /** op 99 */
    OP_99_IF(Hex.parse("63"), "OP_IF"),
    /** op 100 */
    OP_100_NOTIF(Hex.parse("64"), "OP_NOTIF"),
    /** op 103 */
    OP_103_ELSE(Hex.parse("67"), "OP_ELSE"),
    /** op 105 */
    OP_105_VERIFY(Hex.parse("69"), "OP_VERIFY"),
    /** op 106 */
    OP_106_RETURN(Hex.parse("6a"), "OP_RETURN"),
    /** op 107 */
    OP_107_TOALTSTACK(Hex.parse("6b"), "OP_TOALTSTACK"),
    /** op 108 */
    OP_108_FROMALTSTACK(Hex.parse("6c"), "OP_FROMALTSTACK"),
    /** op 109 */
    OP_109_2DROP(Hex.parse("6d"), "OP_2DROP"),
    /** op 110 */
    OP_110_2DUP(Hex.parse("6e"), "OP_2DUP"),
    /** op 118 */
    OP_118_DUP(Hex.parse("76"), "OP_DUP"),
    /** op 124 */
    OP_124_SWAP(Hex.parse("7c"), "OP_SWAP"),
    /** op 135 */
    OP_135_EQUAL(Hex.parse("87"), "OP_EQUAL"),
    /** op 136 */
    OP_136_EQUALVERIFY(Hex.parse("88"), "OP_EQUALVERIFY"),
    /** op 142 */
    OP_142_8E(Hex.parse("8e"), "OP_8E"),
    /** op 145 */
    OP_145_NOT(Hex.parse("91"), "OP_NOT"),
    /** op 147 */
    OP_147_ADD(Hex.parse("93"), "OP_ADD"),
    /** op 167 */
    OP_167_SHA1(Hex.parse("a7"), "OP_SHA1"),
    /** op 169 */
    OP_169_HASH160(Hex.parse("a9"), "OP_HASH160"),
    /** op 170 */
    OP_170_HASH256(Hex.parse("aa"), "OP_HASH256"),
    /** op 172 */
    OP_172_CHECKSIG(Hex.parse("ac"), "OP_CHECKSIG"),
    /** op 173 */
    OP_173_CHECKSIGVERIFY(Hex.parse("ad"), "OP_CHECKSIGVERIFY"),
    /** op 174 */
    OP_174_CHECKMULTISIG(Hex.parse("ae"), "OP_CHECKMULTISIG"),
    /** op 175 */
    OP_175_CHECKMULTISIGVERIFY(Hex.parse("af"), "OP_CHECKMULTISIGVERIFY"),
    /** op 184 */
    OP_184_B8(Hex.parse("b8"), "OP_B8"),
    /** op 190 */
    OP_190_BE(Hex.parse("be"), "OP_BE"),
    /** op 232 */
    OP_232_E8(Hex.parse("e8"), "OP_E8"),
    /** op 250 */
    OP_250_FA(Hex.parse("fa"), "OP_FA"),
    /** op 254 */
    OP_254_FE(Hex.parse("fe"), "OP_FE");

    private final Int code;

    private final String codeName;

    OpCodeNames(Int code, String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    /**
     * <p>findByCode.</p>
     *
     * @param code a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.script.OpCodeNames} object
     */
    public static OpCodeNames findByCode(Int code) {
        for (OpCodeNames value : values()) {
            if (value.code.eq(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("op_code not implemented yet! - %s/%s", code.intValue(), code.toString()));
    }

    /**
     * <p>toScriptCmd.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.script.ScriptCmd} object
     */
    public ScriptCmd toScriptCmd() {
        return new ScriptCmd(this);
    }

    /**
     * <p>Getter for the field <code>code</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getCode() {
        return code;
    }

    /**
     * <p>Getter for the field <code>codeName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCodeName() {
        return codeName;
    }
}
