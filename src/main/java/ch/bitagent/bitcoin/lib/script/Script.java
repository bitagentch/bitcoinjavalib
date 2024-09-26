package ch.bitagent.bitcoin.lib.script;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Helper;
import ch.bitagent.bitcoin.lib.helper.Varint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p>Script class.</p>
 */
public class Script {

    private static final Logger log = Logger.getLogger(Script.class.getSimpleName());

    private List<ScriptCmd> cmds = new ArrayList<>();

    /**
     * Takes a hash160 and returns the p2pkh ScriptPubKey
     *
     * @param h160 an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public static Script p2pkhScript(byte[] h160) {
        return new Script(List.of(OpCodeNames.OP_118_DUP.toScriptCmd(), OpCodeNames.OP_169_HASH160.toScriptCmd(), new ScriptCmd(h160), OpCodeNames.OP_136_EQUALVERIFY.toScriptCmd(), OpCodeNames.OP_172_CHECKSIG.toScriptCmd()));
    }

    /**
     * Takes a hash160 and returns the p2sh ScriptPubKey
     *
     * @param h160 an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public static Script p2shScript(byte[] h160) {
        return new Script(List.of(OpCodeNames.OP_169_HASH160.toScriptCmd(), new ScriptCmd(h160), OpCodeNames.OP_135_EQUAL.toScriptCmd()));
    }

    /**
     * Takes a hash160 and returns the p2wpkh ScriptPubKey
     *
     * @param h160 an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public static Script p2wpkhScript(byte[] h160) {
        return new Script(List.of(OpCodeNames.OP_0.toScriptCmd(), new ScriptCmd(h160)));
    }

    /**
     * <p>Constructor for Script.</p>
     *
     * @param cmds a {@link java.util.List} object
     */
    public Script(List<ScriptCmd> cmds) {
        if (cmds != null && !cmds.isEmpty()) {
            this.cmds = cmds;
        }
    }

    /**
     * <p>add.</p>
     *
     * @param other a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public Script add(Script other) {
        var cmdsAdd = new ArrayList<ScriptCmd>();
        cmdsAdd.addAll(this.cmds);
        cmdsAdd.addAll(other.cmds);
        return new Script(cmdsAdd);
    }

    /**
     * <p>parse.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public static Script parse(ByteArrayInputStream stream) {
        // get the length of the entire field
        Int length = Varint.read(stream);
        // initialize the cmds array
        var cmds = new ArrayList<ScriptCmd>();
        // initialize the number of bytes we've read to 0
        var count = 0;
        // loop until we've read length bytes
        while (count < length.intValue()) {
            // get the current byte
            // convert the current byte to an integer
            var currentByte = Hex.parse(Bytes.read(stream, 1));
            // increment the bytes we've read
            count++;
            // if the current byte is between 1 and 75 inclusive
            if (currentByte.ge(Int.parse(1)) && currentByte.le(Int.parse(75))) {
                // we have a cmd set n to be the current byte
                var n = currentByte.intValue();
                // add the next n bytes as an cmd
                cmds.add(new ScriptCmd(Bytes.read(stream, n)));
                // increase the count by n
                count += n;
            } else if (currentByte.eq(Int.parse(76))) {
                // op_pushdata1
                var dataLength = Hex.parse(Bytes.changeOrder(Bytes.read(stream,1)));
                cmds.add(new ScriptCmd(Bytes.read(stream, dataLength.intValue())));
                count += dataLength.intValue() + 1;
            } else if (currentByte.eq(Int.parse(77))) {
                // op_pushdata2
                var dataLength = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 2)));
                cmds.add(new ScriptCmd(Bytes.read(stream, dataLength.intValue())));
                count += dataLength.intValue() + 2;
            } else {
                // we have an opcode. set the current byte to op_code
                // add the op_code to the list of cmds
                cmds.add(OpCodeNames.findByCode(currentByte).toScriptCmd());
            }
        }
        if (count != length.intValue()) {
            String error = String.format("parsing script failed. count %s, length %s", count, length);
            log.severe(error);
            throw new IllegalStateException(error);
        }
        return new Script(cmds);
    }

    private byte[] rawSerialize() {
        // initialize what we'll send back
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // go through each cmd
        for (ScriptCmd cmd : cmds) {
            if (cmd.isOpCode()) {
                // if the cmd is an integer, it's an opcode
                // turn the cmd into a single byte integer using int_to_little_endian
                result.writeBytes(cmd.getOpCode().getCode().toBytesLittleEndian(1));
            } else if (cmd.isElement()) {
                // otherwise, this is an element
                // get the length in bytes
                var length = cmd.getElement().length;
                // for large lengths, we have to use a pushdata opcode
                if (length < 75) {
                    // turn the length into a single byte integer
                    result.writeBytes(Int.parse(length).toBytesLittleEndian(1));
                } else if (length > 75 && length < 0x100) {
                    // 76 is pushdata1
                    result.writeBytes(Int.parse(76).toBytesLittleEndian(1));
                    result.writeBytes(Int.parse(length).toBytesLittleEndian(1));
                } else if (length >= 0x100 && length <= 520) {
                    // 77 is pushdata2
                    result.writeBytes(Int.parse(77).toBytesLittleEndian(1));
                    result.writeBytes(Int.parse(length).toBytesLittleEndian(2));
                } else {
                    String error = String.format("too long cmd. length %s", length);
                    log.severe(error);
                    throw new IllegalStateException(error);
                }
                result.writeBytes(cmd.getElement());
            } else {
                throw new IllegalStateException();
            }
        }
        return result.toByteArray();
    }

    /**
     * <p>serialize.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] serialize() {
        var result = this.rawSerialize();
        var total = Int.parse(result.length);
        var totalVarint = Varint.encode(total);
        return Bytes.add(totalVarint, result);
    }

    /**
     * <p>evaluate.</p>
     *
     * @param z a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param witness a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @return a boolean
     */
    public boolean evaluate(Int z, Script witness) {
        // create a copy as we may need to add to this list if we have a RedeemScript
        var _cmds = new ArrayList<>(this.cmds);
        var stack = new ArrayDeque<byte[]>();
        var altstack = new ArrayDeque<byte[]>();
        while (!_cmds.isEmpty()) {
            var cmd = _cmds.remove(0);
            if (cmd.isOpCode()) {
                // if the cmd is an integer, it's an opcode
                var opResult = OpCodeFunctions.op(cmd.getOpCode(), stack, altstack, _cmds, z);
                if (!opResult) {
                    log.warning(String.format("bad op: %s", cmd.getOpCode()));
                    return false;
                }
            } else if (cmd.isElement()) {
                // otherwise, this is an element
                // add the cmd to the stack
                stack.push(cmd.getElement());
                if (new Script(_cmds).isP2shScriptPubkey()) {
                    // p2sh
                    // we execute the next three opcodes
                    _cmds.remove(0);
                    var h160 = _cmds.remove(0);
                    _cmds.remove(0);
                    if (!Op.op169Hash160(stack)) {
                        return false;
                    }
                    stack.push(h160.getElement());
                    if (!Op.op135Equal(stack)) {
                        return false;
                    }
                    // final result should be a 1
                    if (!Op.op105Verify(stack)) {
                        log.severe("bad p2sh h160");
                        return false;
                    }
                    // hashes match! now add the RedeemScript
                    var redeemScript = Bytes.add(Varint.encode(Int.parse(cmd.getElement().length)), cmd.getElement());
                    var stream = new ByteArrayInputStream(redeemScript);
                    _cmds.addAll(Script.parse(stream).cmds);
                } else if (isP2wpkhStack(stack)) {
                    // p2wpkh
                    var h160 = stack.pop();
                    stack.pop();
                    _cmds.addAll(witness.getCmds());
                    _cmds.addAll(Script.p2pkhScript(h160).getCmds());
                } else if (isP2wshStack(stack)) {
                    // p2wsh
                    var s256 = stack.pop();
                    stack.pop();
                    var witnessScript = witness.getCmds().remove((witness.getCmds().size() - 1));
                    _cmds.addAll(witness.getCmds());
                    var witnessScriptSha256 = Helper.sha256(witnessScript.getElement());
                    if (!Arrays.equals(s256, witnessScriptSha256)) {
                        log.severe(String.format("bad sha256 %s vs %s", Hex.parse(s256), Hex.parse(witnessScriptSha256)));
                        return false;
                    }
                    var stream = new ByteArrayInputStream(Bytes.add(Varint.encode(Int.parse(witnessScript.getElement().length)), witnessScript.getElement()));
                    var witnessScriptCmds = Script.parse(stream).getCmds();
                    _cmds.addAll(witnessScriptCmds);
                } else {
                    // continue
                }
            } else {
                throw new IllegalStateException();
            }
        }
        if (stack.isEmpty()) {
            log.warning("stack is empty.");
            return false;
        }
        if (Arrays.equals(stack.pop(), new byte[0])) {
            log.warning("stack is zero.");
            return false;
        }
        return true;
    }

    /**
     * Returns whether this follows the OP_DUP OP_HASH160 20 byte hash OP_EQUALVERIFY OP_CHECKSIG pattern.
     *
     * @return a boolean
     */
    public boolean isP2pkhScriptPubkey() {
        return cmds.size() == 5
                && OpCodeNames.OP_118_DUP.equals(cmds.get(0).getOpCode())
                && OpCodeNames.OP_169_HASH160.equals(cmds.get(1).getOpCode())
                && cmds.get(2).isElement() && cmds.get(2).getElement().length == 20
                && OpCodeNames.OP_136_EQUALVERIFY.equals(cmds.get(3).getOpCode())
                && OpCodeNames.OP_172_CHECKSIG.equals(cmds.get(4).getOpCode());
    }

    /**
     * Returns whether this follows the OP_HASH160 20 byte hash OP_EQUAL pattern.
     *
     * @return a boolean
     */
    public boolean isP2shScriptPubkey() {
        return cmds.size() == 3
                && OpCodeNames.OP_169_HASH160.equals(cmds.get(0).getOpCode())
                && cmds.get(1).isElement() && cmds.get(1).getElement().length == 20
                && OpCodeNames.OP_135_EQUAL.equals(cmds.get(2).getOpCode());
    }

    /**
     * <p>isP2wpkhScriptPubkey.</p>
     *
     * @return a boolean
     */
    public boolean isP2wpkhScriptPubkey() {
        return cmds.size() == 2
                && OpCodeNames.OP_0.equals(cmds.get(0).getOpCode())
                && cmds.get(1).isElement() && cmds.get(1).getElement().length == 20;
    }

    /**
     * <p>isP2wpkhStack.</p>
     *
     * @param stack a {@link java.util.Deque} object
     * @return a boolean
     */
    public boolean isP2wpkhStack(Deque<byte[]> stack) {
        if (stack.size() == 2) {
            var stack1 = stack.pop();
            var stack0 = stack.pop();
            stack.push(stack0);
            stack.push(stack1);
            if (OpCodeNames.OP_0.getCode().equals(Hex.parse(stack0)) && stack1.length == 20) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>isP2wshScriptPubkey.</p>
     *
     * @return a boolean
     */
    public boolean isP2wshScriptPubkey() {
        return cmds.size() == 2
                && OpCodeNames.OP_0.equals(cmds.get(0).getOpCode())
                && cmds.get(1).isElement() && cmds.get(1).getElement().length == 32;
    }

    /**
     * <p>isP2wshStack.</p>
     *
     * @param stack a {@link java.util.Deque} object
     * @return a boolean
     */
    public boolean isP2wshStack(Deque<byte[]> stack) {
        if (stack.size() == 2) {
            var stack1 = stack.pop();
            var stack0 = stack.pop();
            stack.push(stack0);
            stack.push(stack1);
            if (OpCodeNames.OP_0.getCode().equals(Hex.parse(stack0)) && stack1.length == 32) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the address corresponding to the script
     *
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link java.lang.String} object
     */
    public String address(Boolean testnet) {
        testnet = Objects.requireNonNullElse(testnet, false);
        if (this.isP2pkhScriptPubkey()) { // p2pkh
            // hash160 is the 3rd cmd
            var h160 = this.getCmds().get(2).getElement();
            // convert to p2pkh address using h160_to_p2pkh_address (remember testnet)
            return Base58.h160toP2pkhAddress(h160, testnet);
        } else if (this.isP2shScriptPubkey()) { // p2sh
            // hash160 is the 2nd cmd
            var h160 = this.getCmds().get(1).getElement();
            // convert to p2sh address using h160_to_p2sh_address (remember testnet)
            return Base58.h160toP2shAddress(h160, testnet);
        } else {
            String error = String.format("Unknown ScriptPubKey %s", this);
            log.severe(error);
            throw new IllegalStateException(error);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        List<String> result = new ArrayList<>();
        for (ScriptCmd cmd : cmds) {
            if (cmd.isOpCode()) {
                // if the cmd is an integer, it's an opcode
                result.add(cmd.getOpCode().getCodeName());
            } else if (cmd.isElement()) {
                // otherwise, this is an element
                result.add(cmd.getElementAsHexString());
            } else {
                throw new IllegalStateException();
            }
        }
        return String.join(" ", result);
    }

    /**
     * <p>Getter for the field <code>cmds</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<ScriptCmd> getCmds() {
        return cmds;
    }
}
