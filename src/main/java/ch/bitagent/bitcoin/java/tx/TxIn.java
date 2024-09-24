package ch.bitagent.bitcoin.java.tx;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.script.Script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class TxIn {

    private static final Logger log = Logger.getLogger(TxIn.class.getSimpleName());

    private final Int prevTx;
    private final Int prevIndex;
    private Script scriptSig;
    private Int sequence;
    private Script witness;

    public TxIn(Int prevTx, Int prevIndex, Script scriptSig, Int sequence) {
        this.prevTx = prevTx;
        this.prevIndex = prevIndex;
        this.scriptSig = Objects.requireNonNullElse(scriptSig, new Script(null));
        this.sequence = Objects.requireNonNullElse(sequence, Hex.parse("ffffffff"));
        log.fine(this.toString());
    }

    /**
     * Takes a byte stream and parses the txInput at the start.
     * Returns a TxIn object.
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
     */
    public Script scriptPubkey(Boolean testnet) {
        // use self.fetch_tx to get the transaction
        var tx = this.fetchPrevTx(testnet);
        // get the output at self.prev_index
        // return the script_pubkey property
        return tx.getTxOuts().get(this.prevIndex.intValue()).getScriptPubkey();
    }

    @Override
    public String toString() {
        return String.format("txin %s:%s:%s:%s", prevTx, prevIndex, scriptSig, sequence);
    }

    public Int getPrevTx() {
        return prevTx;
    }

    public Int getPrevIndex() {
        return prevIndex;
    }

    public Script getScriptSig() {
        return scriptSig;
    }

    public void setScriptSig(Script scriptSig) {
        this.scriptSig = scriptSig;
    }

    public Int getSequence() {
        return sequence;
    }

    public Script getWitness() {
        return witness;
    }

    public void setWitness(Script witness) {
        this.witness = witness;
    }
}
