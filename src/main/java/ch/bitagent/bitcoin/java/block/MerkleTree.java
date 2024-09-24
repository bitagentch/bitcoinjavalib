package ch.bitagent.bitcoin.java.block;

import ch.bitagent.bitcoin.java.helper.Helper;
import ch.bitagent.bitcoin.java.helper.Merkle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

public class MerkleTree {

    Logger log = Logger.getLogger(MerkleTree.class.getSimpleName());

    private final int total;
    private final int maxDepth;
    private final List<MerkleTreeLevel> nodes;
    private int currentDepth;
    private int currentIndex;

    public MerkleTree(int total) {
        this.total = total;
        // compute max depth math.ceil(math.log(self.total, 2))
        this.maxDepth = (int) Math.ceil(Helper.logWithBase(total, 2));
        // initialize the nodes property to hold the actual tree
        this.nodes = new ArrayList<>();
        // loop over the number of levels (max_depth+1)
        for (int depth = 0; depth < maxDepth+1; depth++) {
            // the number of items at this depth is
            // math.ceil(self.total / 2**(self.max_depth - depth))
            var numItems = (int) Math.ceil(total / Math.pow(2, maxDepth - depth));
            // create this level's hashes list with the right number of items
            var levelHashes = new MerkleTreeLevel(numItems);
            // append this level's hashes to the merkle tree
            nodes.add(levelHashes);
        }
        // set the pointer to the root (depth=0, index=0)
        currentDepth = 0;
        currentIndex = 0;
    }

    public void up() {
        // reduce depth by 1 and halve the index
        this.currentDepth -= 1;
        this.currentIndex /= 2;
    }

    public void left() {
        // increase depth by 1 and double the index
        this.currentDepth += 1;
        this.currentIndex *= 2;
    }

    public void right() {
        // increase depth by 1 and double the index + 1
        this.currentDepth += 1;
        this.currentIndex = this.currentIndex * 2 + 1;
    }

    public byte[] root() {
        return this.nodes.get(0).getItems().get(0);
    }

    public void setCurrentNode(byte[] value) {
        this.getNodes().get(this.currentDepth).getItems().set(this.currentIndex, value);
    }

    public byte[] getCurrentNode() {
        return this.getNodes().get(this.currentDepth).getItems().get(this.currentIndex);
    }

    public byte[] getLeftNode() {
        return this.getNodes().get(this.currentDepth + 1).getItems().get(this.currentIndex * 2);
    }

    public byte[] getRightNode() {
        return this.getNodes().get(this.currentDepth + 1).getItems().get(this.currentIndex * 2 + 1);
    }

    public boolean isLeaf() {
        return this.currentDepth == this.maxDepth;
    }

    public boolean rightExists() {
        return this.getNodes().get(this.currentDepth + 1).getItems().size() > this.currentIndex * 2 + 1;
    }

    public void populateTree(byte[] flagBits, List<byte[]> hashes) {
        Deque<Byte> flagBitsQueue = new ArrayDeque<>();
        for (int i = flagBits.length - 1; i >= 0; i--) {
            flagBitsQueue.push(flagBits[i]);
        }
        Deque<byte[]> hashesQueue = new ArrayDeque<>(hashes);
        // populate until we have the root
        while (this.root() == null) {
            // if we are a leaf, we know this position's hash
            if (this.isLeaf()) {
                // get the next bit from flag_bits: flag_bits.pop(0)
                flagBitsQueue.pop();
                // set the current node in the merkle tree to the next hash: hashes.pop(0)
                this.setCurrentNode(hashesQueue.pop());
                // go up a level
                this.up();
            } else {
                // get the left hash
                var leftHash = this.getLeftNode();
                // if we don't have the left hash
                if (leftHash == null) {
                    // if the next flag bit is 0, the next hash is our current node
                    if (flagBitsQueue.pop() == 0) {
                        // set the current node to be the next hash
                        this.setCurrentNode(hashesQueue.pop());
                        // sub-tree doesn't need calculation, go up
                        this.up();
                    } else {
                        // go to the left node
                        this.left();
                    }
                } else if (this.rightExists()) {
                    // get the right hash
                    var rightHash = this.getRightNode();
                    // if we don't have the right hash
                    if (rightHash == null) {
                        // go to the right node
                        this.right();
                    } else {
                        // combine the left and right hashes
                        this.setCurrentNode(Merkle.merkleParent(leftHash, rightHash));
                        // we've completed this sub-tree, go up
                        this.up();
                    }
                } else {
                    // combine the left hash twice
                    this.setCurrentNode(Merkle.merkleParent(leftHash, leftHash));
                    // we've completed this sub-tree, go up
                    this.up();
                }
            }
        }
        if (!hashesQueue.isEmpty()) {
            String error = String.format("hashes not all consumed %s", hashesQueue.size());
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        for (Byte flagBit : flagBitsQueue) {
            if (flagBit != 0) {
                String error = "flag bits not all consumed'";
                log.severe(error);
                throw new IllegalArgumentException(error);
            }
        }
    }

    @Override
    public String toString() {
        return "MerkleTree{" +
                "total=" + total +
                ", maxDepth=" + maxDepth +
                ", nodes=" + nodes +
                ", currentDepth=" + currentDepth +
                ", currentIndex=" + currentIndex +
                '}';
    }

    public List<MerkleTreeLevel> getNodes() {
        return nodes;
    }
}
