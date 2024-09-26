package ch.bitagent.bitcoin.lib.script;

import ch.bitagent.bitcoin.lib.ecc.Int;

import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>OpCodeFunctions class.</p>
 */
public class OpCodeFunctions {

    private static final Logger log = Logger.getLogger(OpCodeFunctions.class.getSimpleName());

    private OpCodeFunctions() {}

    /**
     * <p>op.</p>
     *
     * @param opCode a {@link ch.bitagent.bitcoin.lib.script.OpCodeNames} object
     * @param stack a {@link java.util.Deque} object
     * @param altstack a {@link java.util.Deque} object
     * @param cmds a {@link java.util.List} object
     * @param z a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a boolean
     */
    public static boolean op(OpCodeNames opCode, Deque<byte[]> stack, Deque<byte[]> altstack, List<ScriptCmd> cmds, Int z) {
        if (Set.of(OpCodeNames.OP_99_IF.getCode(), OpCodeNames.OP_100_NOTIF.getCode()).contains(opCode.getCode())) {
            // op_if/op_notif require the cmds array
            if (cmds.isEmpty()) {
                log.warning(String.format("bad op: %s - missing cmds", opCode));
                return false;
            }
        } else if (Set.of(OpCodeNames.OP_107_TOALTSTACK.getCode(), OpCodeNames.OP_108_FROMALTSTACK.getCode()).contains(opCode.getCode())) {
            // op_toaltstack/op_fromaltstack require the altstack
            if (altstack.isEmpty()) {
                log.warning(String.format("bad op: %s - missing altstack", opCode));
                return false;
            }
        } else if (Set.of(OpCodeNames.OP_172_CHECKSIG.getCode(), OpCodeNames.OP_173_CHECKSIGVERIFY.getCode(), OpCodeNames.OP_174_CHECKMULTISIG.getCode(), OpCodeNames.OP_175_CHECKMULTISIGVERIFY.getCode()).contains(opCode.getCode())) {
            // these are signing operations, they need a sig_hash # to check against
            if (z == null) {
                log.warning(String.format("bad op: %s - missing z/sighash", opCode));
                return false;
            }
        }

        var opResult = false;
        switch (opCode) {
            case OP_0:
                opResult = Op.op0(stack);
                break;
            case OP_81:
                opResult = Op.op1(stack);
                break;
            case OP_82:
                opResult = Op.op2(stack);
                break;
            case OP_86:
                opResult = Op.op6(stack);
                break;
            case OP_105_VERIFY:
                opResult = Op.op105Verify(stack);
                break;
            case OP_110_2DUP:
                opResult = Op.op1102Dup(stack);
                break;
            case OP_118_DUP:
                opResult = Op.op118Dup(stack);
                break;
            case OP_124_SWAP:
                opResult = Op.op124Swap(stack);
                break;
            case OP_135_EQUAL:
                opResult = Op.op135Equal(stack);
                break;
            case OP_136_EQUALVERIFY:
                opResult = Op.op136EqualVerify(stack);
                break;
            case OP_145_NOT:
                opResult = Op.op145Not(stack);
                break;
            case OP_147_ADD:
                opResult = Op.op147Add(stack);
                break;
            case OP_167_SHA1:
                opResult = Op.op167Sha1(stack);
                break;
            case OP_169_HASH160:
                opResult = Op.op169Hash160(stack);
                break;
            case OP_170_HASH256:
                opResult = Op.op170Hash256(stack);
                break;
            case OP_172_CHECKSIG:
                opResult = Op.op172Checksig(stack, z);
                break;
            case OP_174_CHECKMULTISIG:
                opResult = Op.op174Checkmultisig(stack, z);
                break;
            default:
                log.severe(String.format("opcode %s not implemented.", opCode));
        }
        return opResult;
    }
}
