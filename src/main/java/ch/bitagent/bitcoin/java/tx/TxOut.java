package ch.bitagent.bitcoin.java.tx;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.script.Script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public class TxOut {

    private static final Logger log = Logger.getLogger(TxOut.class.getSimpleName());

    private final Int amount;
    private final Script scriptPubkey;

    public TxOut(Int amount, Script scriptPubkey) {
        this.amount = amount;
        this.scriptPubkey = scriptPubkey;
        log.fine(this.toString());
    }

    /**
     * Takes a byte stream and parses the txOutput at the start.
     * Returns a TxOut object.
     */
    public static TxOut parse(ByteArrayInputStream stream) {
        var amount = Hex.parse(Bytes.changeOrder(Bytes.read(stream,8)));
        var scriptPubkey = Script.parse(stream);
        return new TxOut(amount, scriptPubkey);
    }

    /**
     * Returns the byte serialization of the transaction output
     */
    public byte[] serialize() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.writeBytes(this.getAmount().toBytesLittleEndian(8));
        result.writeBytes(this.getScriptPubkey().serialize());
        return result.toByteArray();
    }

    @Override
    public String toString() {
        return String.format("txout %s:%s", amount, scriptPubkey);
    }

    public Int getAmount() {
        return amount;
    }

    public Script getScriptPubkey() {
        return scriptPubkey;
    }
}
