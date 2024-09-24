package ch.bitagent.bitcoin.java.script;

import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;

public class ScriptCmd {

    private OpCodeNames opCode;

    private byte[] element;

    public String getElementAsHexString() {
        return Bytes.byteArrayToHexString(this.element);
    }

    public ScriptCmd(OpCodeNames opCode) {
        this.opCode = opCode;
    }

    public boolean isOpCode() {
        return this.opCode != null;
    }

    public ScriptCmd(byte[] element) {
        this.element = element;
    }

    public ScriptCmd(Int element) {
        this.element = element.toBytes();
    }

    public boolean isElement() {
        return this.element != null;
    }

    public OpCodeNames getOpCode() {
        return opCode;
    }

    public byte[] getElement() {
        return element;
    }
}
