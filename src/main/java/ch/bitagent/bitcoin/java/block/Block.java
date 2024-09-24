package ch.bitagent.bitcoin.java.block;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;
import ch.bitagent.bitcoin.java.helper.Merkle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Block {

    public static final Int GENESIS_BLOCK = Hex.parse("0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c");
    public static final Int TESTNET_GENESIS_BLOCK = Hex.parse("0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4adae5494dffff001d1aa4ae18");
    public static final Int LOWEST_BITS = Hex.parse("ffff001d");

    private final Int version;
    private final byte[] prevBlock;
    private final byte[] merkleRoot;
    private final Int timestamp;
    private final byte[] bits;
    private final byte[] nonce;
    private List<byte[]> txHashes;

    public Block(Int version, byte[] prevBlock, byte[] merkleRoot, Int timestamp, byte[] bits, byte[] nonce) {
        this.version = version;
        this.prevBlock = prevBlock;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
    }

    /**
     * Takes a byte stream and parses a block. Returns a Block object
     */
    public static Block parse(ByteArrayInputStream stream) {
        // s.read(n) will read n bytes from the stream
        // version - 4 bytes, little endian, interpret as int
        Int version = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // prev_block - 32 bytes, little endian (use [::-1] to reverse)
        byte[] prevBlock = Bytes.changeOrder(Bytes.read(stream, 32));
        // merkle_root - 32 bytes, little endian (use [::-1] to reverse)
        byte[] merkleRoot = Bytes.changeOrder(Bytes.read(stream, 32));
        // timestamp - 4 bytes, little endian, interpret as int
        Int timestamp = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // bits - 4 bytes
        byte[] bits = Bytes.read(stream, 4);
        // nonce - 4 bytes
        byte[] nonce = Bytes.read(stream, 4);
        // initialize class
        return new Block(version, prevBlock, merkleRoot, timestamp, bits, nonce);
    }

    /**
     * Returns the 80 byte block header
     */
    public byte[] serialize() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // version - 4 bytes, little endian
        result.writeBytes(this.getVersion().toBytesLittleEndian(4));
        // prev_block - 32 bytes, little endian
        result.writeBytes(Bytes.changeOrder(this.prevBlock));
        // merkle_root - 32 bytes, little endian
        result.writeBytes(Bytes.changeOrder(this.merkleRoot));
        // timestamp - 4 bytes, little endian
        result.writeBytes(Bytes.changeOrder(this.timestamp.toBytes()));
        // bits - 4 bytes
        result.writeBytes(this.bits);
        // nonce - 4 bytes
        result.writeBytes(this.nonce);
        return result.toByteArray();
    }

    /**
     * Returns the hash256 interpreted little endian of the block
     */
    public byte[] hash() {
        // serialize
        var stream = this.serialize();
        // hash256
        var h256 = Helper.hash256(stream);
        // reverse
        return Bytes.changeOrder(h256);
    }

    /**
     * Returns whether this block is signaling readiness for BIP9
     */
    public boolean isBip9() {
        // BIP9 is signalled if the top 3 bits are 001
        // remember version is 32 bytes so right shift 29 (>> 29) and see if that is 001
        return this.version.intValue() >> 29 == 0b001;
    }

    /**
     * Returns whether this block is signaling readiness for BIP91
     */
    public boolean isBip91() {
        // BIP91 is signalled if the 5th bit from the right is 1
        // shift 4 bits to the right and see if the last bit is 1
        return (this.version.intValue() >> 4 & 1) == 1;
    }

    /**
     * Returns whether this block is signaling readiness for BIP141
     */
    public boolean isBip141() {
        // BIP91 is signalled if the 2nd bit from the right is 1
        // shift 1 bit to the right and see if the last bit is 1
        return (this.version.intValue() >> 1 & 1) == 1;
    }

    /**
     * Returns the proof-of-work target based on the bits
     */
    public Int target() {
        return Bytes.bitsToTarget(this.bits);
    }

    /**
     * Returns the block difficulty based on the bits
     */
    public Int difficulty() {
        // note difficulty is (target of lowest difficulty) / (self's target)
        // lowest difficulty has bits that equal 0xffff001d
        var lowest = Hex.parse("ffff").mul(Int.parse(256).pow(Hex.parse("1d").sub(Int.parse(3))));
        return lowest.div(this.target());
    }

    public Int proof() {
        // get the hash256 of the serialization of this block
        var h256 = Helper.hash256(this.serialize());
        // interpret this hash as a little-endian number
        return Hex.parse(Bytes.changeOrder(h256));
    }

    /**
     * Returns whether this block satisfies proof of work
     */
    public boolean checkPow() {
        //  return whether this integer is less than the target
        return this.proof().lt(this.target());
    }

    /**
     * Gets the merkle root of the tx_hashes and checks that it's the same as the merkle root of this block.
     */
    public boolean validateMerkleRoot() {
        // reverse each item in self.tx_hashes
        var hashes = this.txHashes.stream().map(Bytes::changeOrder).collect(Collectors.toList());
        // compute the Merkle Root and reverse
        var root = Bytes.changeOrder(Merkle.merkleRoot(hashes));
        // return whether self.merkle_root is the same
        return Arrays.equals(this.merkleRoot, root);
    }

    public Int getVersion() {
        return version;
    }

    public Int getTimestamp() {
        return timestamp;
    }

    public byte[] getPrevBlock() {
        return prevBlock;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public byte[] getBits() {
        return bits;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public void setTxHashes(List<byte[]> txHashes) {
        this.txHashes = txHashes;
    }
}
