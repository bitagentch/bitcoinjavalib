package ch.bitagent.bitcoin.java.block;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Varint;
import ch.bitagent.bitcoin.java.network.Message;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MerkleBlock implements Message {

    public static final String COMMAND = "merkleblock";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int version;
    private final byte[] prevBlock;
    private final byte[] merkleRoot;
    private final Int timestamp;
    private final byte[] bits;
    private final byte[] nonce;
    private final int total;
    private final List<byte[]> hashes;
    private final byte[] flags;

    public MerkleBlock(Int version, byte[] prevBlock, byte[] merkleRoot, Int timestamp, byte[] bits, byte[] nonce, int total, List<byte[]> hashes, byte[] flags) {
        this.version = version;
        this.prevBlock = prevBlock;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.bits = bits;
        this.nonce = nonce;
        this.total = total;
        this.hashes = hashes;
        this.flags = flags;
    }

    /**
     * Takes a byte stream and parses a merkle block. Returns a Merkle Block object
     */
    public static MerkleBlock parse(ByteArrayInputStream stream) {
        // version - 4 bytes, Little-Endian integer
        var version = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // prev_block - 32 bytes, Little-Endian (use [::-1])
        var prevBlock = Bytes.changeOrder(Bytes.read(stream, 32));
        // merkle_root - 32 bytes, Little-Endian (use [::-1])
        var merkleRoot = Bytes.changeOrder(Bytes.read(stream, 32));
        // timestamp - 4 bytes, Little-Endian integer
        var timestamp = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // bits - 4 bytes
        var bits = Bytes.read(stream, 4);
        // nonce - 4 bytes
        var nonce = Bytes.read(stream, 4);
        // total transactions in block - 4 bytes, Little-Endian integer
        var total = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4))).intValue();
        // number of transaction hashes - varint
        var numHashes = Varint.read(stream);
        // each transaction is 32 bytes, Little-Endian
        var hashes = new ArrayList<byte[]>();
        for (int i = 0; i < numHashes.intValue(); i++) {
            hashes.add(Bytes.changeOrder(Bytes.read(stream, 32)));
        }
        // length of flags field - varint
        var flagsLength = Varint.read(stream);
        // read the flags field
        var flags = Bytes.read(stream, flagsLength.intValue());
        // initialize class
        return new MerkleBlock(version, prevBlock, merkleRoot, timestamp, bits, nonce, total, hashes, flags);
    }

    /**
     * Verifies whether the merkle tree information validates to the merkle root
     */
    public boolean isValid() {
        // convert the flags field to a bit field
        var flagBits = Bytes.bytesToBitField(this.flags);
        // reverse self.hashes for the merkle root calculation
        var myHashes = this.hashes.stream().map(Bytes::changeOrder).collect(Collectors.toList());
        // initialize the merkle tree
        var merkleTree = new MerkleTree(this.total);
        // populate the tree with flag bits and hashes
        merkleTree.populateTree(flagBits, myHashes);
        // check if the computed root reversed is the same as the merkle root
        return Arrays.equals(Bytes.changeOrder(merkleTree.root()), this.merkleRoot);
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    public Int getVersion() {
        return version;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public byte[] getPrevBlock() {
        return prevBlock;
    }

    public Int getTimestamp() {
        return timestamp;
    }

    public byte[] getBits() {
        return bits;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public int getTotal() {
        return total;
    }

    public List<byte[]> getHashes() {
        return hashes;
    }

    public byte[] getFlags() {
        return flags;
    }
}
