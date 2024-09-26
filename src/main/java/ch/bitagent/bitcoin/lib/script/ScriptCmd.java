package ch.bitagent.bitcoin.lib.script;

import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;

/**
 * <p>ScriptCmd class.</p>
 */
public class ScriptCmd {

    private OpCodeNames opCode;

    private byte[] element;

    /**
     * <p>getElementAsHexString.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getElementAsHexString() {
        return Bytes.byteArrayToHexString(this.element);
    }

    /**
     * <p>Constructor for ScriptCmd.</p>
     *
     * @param opCode a {@link ch.bitagent.bitcoin.lib.script.OpCodeNames} object
     */
    public ScriptCmd(OpCodeNames opCode) {
        this.opCode = opCode;
    }

    /**
     * <p>isOpCode.</p>
     *
     * @return a boolean
     */
    public boolean isOpCode() {
        return this.opCode != null;
    }

    /**
     * <p>Constructor for ScriptCmd.</p>
     *
     * @param element an array of {@link byte} objects
     */
    public ScriptCmd(byte[] element) {
        this.element = element;
    }

    /**
     * <p>Constructor for ScriptCmd.</p>
     *
     * @param element a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public ScriptCmd(Int element) {
        this.element = element.toBytes();
    }

    /**
     * <p>isElement.</p>
     *
     * @return a boolean
     */
    public boolean isElement() {
        return this.element != null;
    }

    /**
     * <p>Getter for the field <code>opCode</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.script.OpCodeNames} object
     */
    public OpCodeNames getOpCode() {
        return opCode;
    }

    /**
     * <p>Getter for the field <code>element</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getElement() {
        return element;
    }
}
