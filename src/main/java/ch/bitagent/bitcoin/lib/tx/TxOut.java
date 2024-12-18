package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.script.Script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

/**
 * <p>TxOut class.</p>
 */
public class TxOut {

    private static final Logger log = Logger.getLogger(TxOut.class.getSimpleName());

    private final Int amount;
    private final Script scriptPubkey;

    /**
     * <p>Constructor for TxOut.</p>
     *
     * @param amount       a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param scriptPubkey a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public TxOut(Int amount, Script scriptPubkey) {
        this.amount = amount;
        this.scriptPubkey = scriptPubkey;
        log.fine(this.toString());
    }

    /**
     * Takes a byte stream and parses the txOutput at the start.
     * Returns a TxOut object.
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.lib.tx.TxOut} object
     */
    public static TxOut parse(ByteArrayInputStream stream) {
        var amount = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 8)));
        var scriptPubkey = Script.parse(stream);
        return new TxOut(amount, scriptPubkey);
    }

    /**
     * Returns the byte serialization of the transaction output
     *
     * @return an array of {@link byte} objects
     */
    public byte[] serialize() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.writeBytes(this.getAmount().toBytesLittleEndian(8));
        result.writeBytes(this.getScriptPubkey().serialize());
        return result.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("txout %s:%s", amount.intValue(), scriptPubkey);
    }

    /**
     * <p>Getter for the field <code>amount</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getAmount() {
        return amount;
    }

    /**
     * <p>Getter for the field <code>scriptPubkey</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public Script getScriptPubkey() {
        return scriptPubkey;
    }
}
