package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.script.Script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * <p>TxIn class.</p>
 */
public class TxIn {

    private static final Logger log = Logger.getLogger(TxIn.class.getSimpleName());

    public static final Int SEQUENCE_DEF = Hex.parse("ffffffff");
    public static final Int SEQUENCE_RBF = Hex.parse("fffffffd");

    private final Utxo utxo;
    private final Int prevTx;
    private final Int prevIndex;
    private Script scriptSig;
    private final Int sequence;
    private Script witness;

    /**
     * <p>Constructor for TxIn.</p>
     *
     * @param prevTx    a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param prevIndex a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param scriptSig a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @param sequence  a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public TxIn(Int prevTx, Int prevIndex, Script scriptSig, Int sequence) {
        this.utxo = null;
        this.prevTx = prevTx;
        this.prevIndex = prevIndex;
        this.scriptSig = Objects.requireNonNullElse(scriptSig, new Script(null));
        this.sequence = Objects.requireNonNullElse(sequence, SEQUENCE_DEF);
        log.fine(this.toString());
    }

    public TxIn(Utxo utxo) {
        this.utxo = utxo;
        this.prevTx = Hex.parse(utxo.getTxHash());
        this.prevIndex = Int.parse(utxo.getTxPos());
        this.scriptSig = new Script(null);
        this.sequence = SEQUENCE_RBF;
        log.fine(this.toString());
    }

    /**
     * Takes a byte stream and parses the txInput at the start.
     * Returns a TxIn object.
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.lib.tx.TxIn} object
     */
    public static TxIn parse(ByteArrayInputStream stream) {
        var prevTx = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 32)));
        var prevIndex = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        var scriptSig = Script.parse(stream);
        var sequence = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        return new TxIn(prevTx, prevIndex, scriptSig, sequence);
    }

    /**
     * Returns the byte serialization of the transaction input
     *
     * @return an array of {@link byte} objects
     */
    public byte[] serialize() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.writeBytes(this.prevTx.toBytesLittleEndian(32));
        result.writeBytes(this.prevIndex.toBytesLittleEndian(4));
        result.writeBytes(this.scriptSig.serialize());
        result.writeBytes(this.sequence.toBytesLittleEndian(4));
        return result.toByteArray();
    }

    private Tx fetchPrevTx(Boolean testnet) {
        return TxFetcher.fetch(this.prevTx.toHex().toString(), testnet, false);
    }

    /**
     * Get the outpoint value by looking up the tx hash
     * Returns the amount in satoshi
     *
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int value(Boolean testnet) {
        // use self.fetch_tx to get the transaction
        var tx = this.fetchPrevTx(testnet);
        // get the output at self.prev_index
        // return the amount property
        return tx.getTxOuts().get(this.prevIndex.intValue()).getAmount();
    }

    /**
     * Get the ScriptPubKey by looking up the tx hash
     * Returns a Script object
     *
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public Script scriptPubkey(Boolean testnet) {
        // use self.fetch_tx to get the transaction
        var tx = this.fetchPrevTx(testnet);
        // get the output at self.prev_index
        // return the script_pubkey property
        return tx.getTxOuts().get(this.prevIndex.intValue()).getScriptPubkey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("txin %s:%s:%s:%s:%s", prevTx, prevIndex.intValue(), scriptSig, sequence, witness != null ? witness : "");
    }

    /**
     * <p>Getter for the field <code>prevTx</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getPrevTx() {
        return prevTx;
    }

    /**
     * <p>Getter for the field <code>prevIndex</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getPrevIndex() {
        return prevIndex;
    }

    /**
     * <p>Getter for the field <code>scriptSig</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public Script getScriptSig() {
        return scriptSig;
    }

    /**
     * <p>Setter for the field <code>scriptSig</code>.</p>
     *
     * @param scriptSig a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public void setScriptSig(Script scriptSig) {
        this.scriptSig = scriptSig;
    }

    /**
     * <p>Getter for the field <code>sequence</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getSequence() {
        return sequence;
    }

    /**
     * <p>Getter for the field <code>witness</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public Script getWitness() {
        return witness;
    }

    /**
     * <p>Setter for the field <code>witness</code>.</p>
     *
     * @param witness a {@link ch.bitagent.bitcoin.lib.script.Script} object
     */
    public void setWitness(Script witness) {
        this.witness = witness;
    }

    public Utxo getUtxo() {
        return utxo;
    }
}
