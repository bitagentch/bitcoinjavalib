package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Varint;
import ch.bitagent.bitcoin.lib.network.Message;
import ch.bitagent.bitcoin.lib.script.OpCodeNames;
import ch.bitagent.bitcoin.lib.script.Script;
import ch.bitagent.bitcoin.lib.script.ScriptCmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>Tx class.</p>
 */
public class Tx implements Message {

    private static final Logger log = Logger.getLogger(Tx.class.getSimpleName());

    /**
     * Constant <code>COMMAND="tx"</code>
     */
    public static final String COMMAND = "tx";

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int version;
    private final List<TxIn> txIns;
    private final List<TxOut> txOuts;
    private Int locktime;
    private final Boolean testnet;
    private final Boolean segwit;
    private byte[] _hashPrevouts = null;
    private byte[] _hashSequence = null;
    private byte[] _hashOutputs = null;

    /**
     * <p>Constructor for Tx.</p>
     *
     * @param version  a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param txIns    a {@link java.util.List} object
     * @param txOuts   a {@link java.util.List} object
     * @param locktime a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param testnet  a {@link java.lang.Boolean} object
     * @param segwit   a {@link java.lang.Boolean} object
     */
    public Tx(Int version, List<TxIn> txIns, List<TxOut> txOuts, Int locktime, Boolean testnet, Boolean segwit) {
        this.version = version;
        this.txIns = txIns;
        this.txOuts = txOuts;
        this.locktime = locktime;
        this.testnet = Objects.requireNonNullElse(testnet, false);
        this.segwit = Objects.requireNonNullElse(segwit, false);
    }

    /**
     * Human-readable hexadecimal of the transaction hash
     *
     * @return a {@link java.lang.String} object
     */
    public String id() {
        return Bytes.byteArrayToHexString(this.hash());
    }

    /**
     * Binary hash of the legacy serialization
     *
     * @return an array of {@link byte} objects
     */
    public byte[] hash() {
        return Bytes.changeOrder(Hash.hash256(this.serializeLegacy()));
    }

    /**
     * <p>parse.</p>
     *
     * @param stream  a {@link java.io.ByteArrayInputStream} object
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link ch.bitagent.bitcoin.lib.tx.Tx} object
     */
    public static Tx parse(ByteArrayInputStream stream, Boolean testnet) {
        Bytes.read(stream, 4);
        var segwitMarker = Hex.parse(Bytes.read(stream, 1));
        stream.reset();
        if (segwitMarker.eq(Hex.parse("00"))) {
            return parseSegwit(stream, testnet);
        } else {
            return parseLegacy(stream, testnet);
        }
    }

    /**
     * Takes a byte stream and parses the transaction at the start
     * return a Tx object
     */
    private static Tx parseLegacy(ByteArrayInputStream stream, Boolean testnet) {
        // s.read(n) will return n bytes
        // version is an integer in 4 bytes, little-endian
        var version = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // num_inputs is a varint, use read_varint(s)
        var numInputs = Varint.read(stream).intValue();
        // parse num_inputs number of TxIns
        List<TxIn> txIns = new ArrayList<>();
        for (int i = 0; i < numInputs; i++) {
            txIns.add(TxIn.parse(stream));
        }
        // num_outputs is a varint, use read_varint(s)
        var numOutputs = Varint.read(stream).intValue();
        // parse num_outputs number of TxOuts
        List<TxOut> txOuts = new ArrayList<>();
        for (int i = 0; i < numOutputs; i++) {
            txOuts.add(TxOut.parse(stream));
        }
        // locktime is an integer in 4 bytes, little-endian
        var locktime = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // return an instance of the class (see __init__ for args)
        return new Tx(version, txIns, txOuts, locktime, testnet, false);
    }

    private static Tx parseSegwit(ByteArrayInputStream stream, Boolean testnet) {
        var version = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        var marker = Hex.parse(Bytes.read(stream, 2));
        if (!marker.eq(Hex.parse("0001"))) {
            throw new IllegalArgumentException(String.format("Not a segwit transaction %s", marker));
        }
        var numInputs = Varint.read(stream).intValue();
        var inputs = new ArrayList<TxIn>();
        for (int i = 0; i < numInputs; i++) {
            inputs.add(TxIn.parse(stream));
        }
        var numOutputs = Varint.read(stream).intValue();
        var outputs = new ArrayList<TxOut>();
        for (int i = 0; i < numOutputs; i++) {
            outputs.add(TxOut.parse(stream));
        }
        for (TxIn txIn : inputs) {
            var numItems = Varint.read(stream).intValue();
            List<ScriptCmd> items = new ArrayList<>();
            for (int i = 0; i < numItems; i++) {
                var itemLen = Varint.read(stream).intValue();
                if (itemLen == 0) {
                    items.add(OpCodeNames.OP_0.toScriptCmd());
                } else {
                    items.add(new ScriptCmd(Bytes.read(stream, itemLen)));
                }
            }
            txIn.setWitness(new Script(items));
        }
        var locktime = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        return new Tx(version, inputs, outputs, locktime, testnet, true);
    }

    /**
     * <p>serialize.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] serialize() {
        if (Boolean.TRUE.equals(this.segwit)) {
            return serializeSegwit();
        } else {
            return serializeLegacy();
        }
    }

    /**
     * Returns the byte serialization of the transaction
     */
    private byte[] serializeLegacy() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // serialize version (4 bytes, little endian)
        result.writeBytes(this.version.toBytesLittleEndian(4));
        // encode_varint on the number of inputs
        result.writeBytes(Varint.encode(Int.parse(this.txIns.size())));
        // iterate inputs
        for (TxIn txIn : txIns) {
            // serialize each input
            result.writeBytes(txIn.serialize());
        }
        // encode_varint on the number of outputs
        result.writeBytes(Varint.encode(Int.parse(this.txOuts.size())));
        // iterate outputs
        for (TxOut txOut : txOuts) {
            // serialize each output
            result.writeBytes(txOut.serialize());
        }
        // serialize locktime (4 bytes, little endian)
        result.writeBytes(this.locktime.toBytesLittleEndian(4));
        return result.toByteArray();
    }

    private byte[] serializeSegwit() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.writeBytes(this.version.toBytesLittleEndian(4));
        result.writeBytes(new byte[]{0x00, 0x01});
        result.writeBytes(Varint.encode(Int.parse(this.txIns.size())));
        for (TxIn txIn : txIns) {
            result.writeBytes(txIn.serialize());
        }
        result.writeBytes(Varint.encode(Int.parse(this.txOuts.size())));
        for (TxOut txOut : txOuts) {
            result.writeBytes(txOut.serialize());
        }
        for (TxIn txIn : txIns) {
            result.writeBytes(Int.parse(txIn.getWitness().getCmds().size()).toBytesLittleEndian(1));
            for (ScriptCmd witness : txIn.getWitness().getCmds()) {
                if (witness.isOpCode()) {
                    result.writeBytes(witness.getOpCode().getCode().toBytesLittleEndian(1));
                } else {
                    result.writeBytes(Varint.encode(Int.parse(witness.getElement().length)));
                    result.writeBytes(witness.getElement());
                }
            }
        }
        result.writeBytes(this.locktime.toBytesLittleEndian(4));
        return result.toByteArray();
    }

    /**
     * Returns the fee of this transaction in satoshi
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int fee() {
        log.fine(String.format("txIns: %s, txOuts: %s", txIns.size(), txOuts.size()));
        long start = System.currentTimeMillis();
        // initialize input sum and output sum
        var inputSum = Int.parse(0);
        var outputSum = Int.parse(0);
        // use TxIn.value() to sum up the input amounts
        int txin = 0;
        for (TxIn txIn : txIns) {
            log.info(String.format("txin %s/%s", ++txin, txIns.size()));
            inputSum = inputSum.add(txIn.value(this.testnet));
            if (txin % 100 == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // NOP
                }
            }
        }
        // use TxOut.amount to sum up the output amounts
        for (TxOut txOut : txOuts) {
            outputSum = outputSum.add(txOut.getAmount());
        }
        // fee is input sum - output sum
        var fee = inputSum.sub(outputSum);
        log.fine(String.format("time %sms", System.currentTimeMillis() - start));
        return fee;
    }

    /**
     * Returns the integer representation of the hash that needs to get signed for index input_index
     *
     * @param inputIndex   a int
     * @param redeemScript a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int sigHash(int inputIndex, Script redeemScript) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // start the serialization with version
        // use int_to_little_endian in 4 bytes
        stream.writeBytes(this.version.toBytesLittleEndian(4));
        // add how many inputs there are using encode_varint
        stream.writeBytes(Varint.encode(Int.parse(this.txIns.size())));
        // loop through each input using enumerate, so we have the input index
        for (int i = 0; i < this.txIns.size(); i++) {
            var txIn = this.txIns.get(i);
            Script scriptSig;
            if (i == inputIndex) {
                if (redeemScript != null) {
                    // p2sh
                    scriptSig = redeemScript;
                } else {
                    // if the input index is the one we're signing
                    scriptSig = txIn.scriptPubkey(this.testnet);
                }
            } else {
                // Otherwise, the ScriptSig is empty
                scriptSig = null;
            }
            // add the serialization of the input with the ScriptSig we want
            stream.writeBytes(new TxIn(txIn.getPrevTx(), txIn.getPrevIndex(), scriptSig, txIn.getSequence()).serialize());
        }
        // add how many outputs there are using encode_varint
        stream.writeBytes(Varint.encode(Int.parse(this.txOuts.size())));
        // add the serialization of each output
        for (TxOut txOut : this.txOuts) {
            stream.writeBytes(txOut.serialize());
        }
        // add the locktime using int_to_little_endian in 4 bytes
        stream.writeBytes(this.locktime.toBytesLittleEndian(4));
        // add SIGHASH_ALL using int_to_little_endian in 4 bytes
        stream.writeBytes(Hash.SIGHASH_ALL.toBytesLittleEndian(4));
        // hash256 the serialization
        var h256 = Hash.hash256(stream.toByteArray());
        // convert the result to an integer using int.from_bytes(x, 'big')
        return Hex.parse(h256);
    }

    private byte[] hashPrevouts() {
        if (this._hashPrevouts == null) {
            var allPrevouts = new byte[0];
            var allSequence = new byte[0];
            for (TxIn txIn : this.txIns) {
                allPrevouts = Bytes.add(new byte[][]{allPrevouts, txIn.getPrevTx().toBytesLittleEndian(), txIn.getPrevIndex().toBytesLittleEndian(4)});
                allSequence = Bytes.add(allSequence, txIn.getSequence().toBytesLittleEndian(4));
            }
            this._hashPrevouts = Hash.hash256(allPrevouts);
            this._hashSequence = Hash.hash256(allSequence);
        }
        return this._hashPrevouts;
    }

    private byte[] hashSequence() {
        if (this._hashSequence == null) {
            // this should calculate self._hash_prevouts
            this.hashPrevouts();
        }
        return this._hashSequence;
    }

    private byte[] hashOutputs() {
        if (this._hashOutputs == null) {
            var allOutputs = new byte[0];
            for (TxOut txOut : this.txOuts) {
                allOutputs = Bytes.add(allOutputs, txOut.serialize());
            }
            this._hashOutputs = Hash.hash256(allOutputs);
        }
        return this._hashOutputs;
    }

    /**
     * Returns the integer representation of the hash that needs to get signed for index input_index
     *
     * @param inputIndex    a int
     * @param redeemScript  a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @param witnessScript a {@link ch.bitagent.bitcoin.lib.script.Script} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int sigHashBip143(int inputIndex, Script redeemScript, Script witnessScript) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        var txIn = this.txIns.get(inputIndex);
        // per BIP143 spec
        stream.writeBytes(this.version.toBytesLittleEndian(4));
        stream.writeBytes(this.hashPrevouts());
        stream.writeBytes(this.hashSequence());
        stream.writeBytes(txIn.getPrevTx().toBytesLittleEndian());
        stream.writeBytes(txIn.getPrevIndex().toBytesLittleEndian(4));
        byte[] scriptCode;
        if (witnessScript != null) {
            scriptCode = witnessScript.serialize();
        } else if (redeemScript != null) {
            scriptCode = Script.p2pkhScript(redeemScript.getCmds().get(1).getElement()).serialize();
        } else {
            scriptCode = Script.p2pkhScript(txIn.scriptPubkey(this.testnet).getCmds().get(1).getElement()).serialize();
        }
        stream.writeBytes(scriptCode);
        stream.writeBytes(txIn.value(this.testnet).toBytesLittleEndian(8));
        stream.writeBytes(txIn.getSequence().toBytesLittleEndian(4));
        stream.writeBytes(this.hashOutputs());
        stream.writeBytes(this.locktime.toBytesLittleEndian(4));
        stream.writeBytes(Hash.SIGHASH_ALL.toBytesLittleEndian(4));
        return Hex.parse(Hash.hash256(stream.toByteArray()));
    }

    /**
     * Returns whether the input has a valid signature
     *
     * @param inputIndex a int
     * @return a boolean
     */
    public boolean verifyInput(int inputIndex) {
        // get the relevant input
        var txIn = this.txIns.get(inputIndex);
        // grab the previous ScriptPubKey
        var scriptPubkey = txIn.scriptPubkey(this.testnet);
        Int z = null;
        Script witness = null;
        // check to see if the ScriptPubkey is a p2sh
        if (scriptPubkey.isP2shScriptPubkey()) {
            // the last cmd in a p2sh has to be the RedeemScript to trigger
            var cmd = txIn.getScriptSig().getCmds().get(txIn.getScriptSig().getCmds().size() - 1);
            // parse the RedeemScript
            var rawRedeem = Bytes.add(Int.parse(cmd.getElement().length).toBytesLittleEndian(1), cmd.getElement());
            var redeemScript = Script.parse(new ByteArrayInputStream(rawRedeem));
            // the RedeemScript might be p2wpkh or p2wsh
            if (redeemScript.isP2wpkhScriptPubkey()) {
                z = this.sigHashBip143(inputIndex, redeemScript, null);
                witness = txIn.getWitness();
            } else if (redeemScript.isP2wshScriptPubkey()) {
                cmd = txIn.getWitness().getCmds().get(txIn.getWitness().getCmds().size() - 1);
                var rawWitness = Bytes.add(Varint.encode(Int.parse(cmd.getElement().length)), cmd.getElement());
                var witnessScript = Script.parse(new ByteArrayInputStream(rawWitness));
                z = this.sigHashBip143(inputIndex, null, witnessScript);
                witness = txIn.getWitness();
            } else {
                z = this.sigHash(inputIndex, redeemScript);
                witness = null;
            }
        } else {
            // ScriptPubkey might be a p2wpkh or p2wsh
            if (scriptPubkey.isP2wpkhScriptPubkey()) {
                z = this.sigHashBip143(inputIndex, null, null);
                witness = txIn.getWitness();
            } else if (scriptPubkey.isP2wshScriptPubkey()) {
                var cmd = txIn.getWitness().getCmds().get(txIn.getWitness().getCmds().size() - 1);
                var rawWitness = Bytes.add(Varint.encode(Int.parse(cmd.getElement().length)), cmd.getElement());
                var witnessScript = Script.parse(new ByteArrayInputStream(rawWitness));
                z = this.sigHashBip143(inputIndex, null, witnessScript);
                witness = txIn.getWitness();
            } else {
                z = this.sigHash(inputIndex, null);
                witness = null;
            }
        }
        // combine the current ScriptSig and the previous ScriptPubKey
        var combined = txIn.getScriptSig().add(scriptPubkey);
        // evaluate the combined script
        return combined.evaluate(z, witness);
    }

    /**
     * Verify this transaction
     *
     * @return a boolean
     */
    public boolean verify() {
        // check that we're not creating money
        if (this.fee().lt(Int.parse(0))) {
            return false;
        }
        // check that each input has a valid ScriptSig
        for (int i = 0; i < this.txIns.size(); i++) {
            log.info(String.format("txin %s/%s", i + 1, this.txIns.size()));
            if (!this.verifyInput(i)) {
                log.warning(String.format("TxIn has no valid signature - %s", this.txIns.get(i)));
                return false;
            }
        }
        return true;
    }

    /**
     * Signs the input using the private key
     *
     * @param inputIndex a int
     * @param privateKey a {@link ch.bitagent.bitcoin.lib.ecc.PrivateKey} object
     * @return a boolean
     */
    public boolean signInput(int inputIndex, PrivateKey privateKey) {
        // get the signature hash (z)
        var z = this.sigHash(inputIndex, null);
        // get der signature of z from private key
        var der = privateKey.sign(z, 0).der();
        // append the SIGHASH_ALL to der
        var sig = Bytes.add(der, Hash.SIGHASH_ALL.toBytes(1));
        // calculate the sec
        var sec = privateKey.getPoint().sec(null);
        // initialize a new script with [sig, sec] as the cmds
        var scriptSig = new Script(List.of(new ScriptCmd(sig), new ScriptCmd(sec)));
        // change input's script_sig to new script
        this.txIns.get(inputIndex).setScriptSig(scriptSig);
        // return whether sig is valid using self.verify_input
        return this.verifyInput(inputIndex);
    }

    /**
     * Returns whether this transaction is a coinbase transaction or not
     *
     * @return a boolean
     */
    public boolean isCoinbase() {
        // check that there is exactly 1 input
        if (this.txIns.size() != 1) {
            return false;
        }
        // grab the first input
        var firstInput = this.txIns.get(0);
        // check that first input prev_tx is b'\x00' * 32 bytes
        if (firstInput.getPrevTx().ne(Hex.parse("0000000000000000000000000000000000000000000000000000000000000000"))) {
            return false;
        }
        // check that first input prev_index is 0xffffffff
        if (firstInput.getPrevIndex().ne(Hex.parse("ffffffff"))) {
            return false;
        }
        return true;
    }

    /**
     * Returns the height of the block this coinbase transaction is in
     * Returns None if this transaction is not a coinbase transaction
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int coinbaseHeight() {
        // if this is NOT a coinbase transaction, return None
        if (!this.isCoinbase()) {
            return null;
        }
        // grab the first cmd
        var firstCmd = this.txIns.get(0).getScriptSig().getCmds().get(0);
        // convert the cmd from little endian to int
        return Hex.parse(Bytes.changeOrder(firstCmd.getElement()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("tx %s:%s:\n%s:\n%s:%s",
                this.id(),
                this.version,
                this.txIns.stream().map(TxIn::toString).collect(Collectors.joining("\n")),
                this.txOuts.stream().map(TxOut::toString).collect(Collectors.joining("\n")),
                this.locktime);
    }

    /**
     * <p>Getter for the field <code>txOuts</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<TxOut> getTxOuts() {
        return txOuts;
    }

    /**
     * <p>Getter for the field <code>segwit</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getSegwit() {
        return segwit;
    }

    /**
     * <p>Getter for the field <code>locktime</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getLocktime() {
        return locktime;
    }

    /**
     * <p>Setter for the field <code>locktime</code>.</p>
     *
     * @param locktime a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public void setLocktime(Int locktime) {
        this.locktime = locktime;
    }

    /**
     * <p>Getter for the field <code>txIns</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<TxIn> getTxIns() {
        return txIns;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getVersion() {
        return version;
    }

    /**
     * <p>Getter for the field <code>testnet</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getTestnet() {
        return testnet;
    }
}
