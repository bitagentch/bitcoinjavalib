package ch.bitagent.bitcoin.lib.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Merkle class.</p>
 */
public class Merkle {

    private static final Logger log = Logger.getLogger(Merkle.class.getSimpleName());

    private Merkle() {
    }

    /**
     * Takes the binary hashes and calculates the hash256
     *
     * @param hash0 an array of {@link byte} objects
     * @param hash1 an array of {@link byte} objects
     * @return an array of {@link byte} objects
     */
    public static byte[] merkleParent(byte[] hash0, byte[] hash1) {
        // return the hash256 of hash1 + hash2
        return Hash.hash256(Bytes.add(hash0, hash1));
    }

    /**
     * Takes a list of binary hashes and returns a list that's half the length
     *
     * @param hashes a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    public static List<byte[]> merkleParentLevel(List<byte[]> hashes) {
        // if the list has exactly 1 element raise an error
        if (hashes.size() <= 1) {
            String error = String.format("Cannot take a parent level with only %s item", hashes.size());
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        // if the list has an odd number of elements, duplicate the last one
        // and put it at the end so it has an even number of elements
        if (hashes.size() % 2 == 1) {
            hashes.add(hashes.get(hashes.size() - 1));
        }
        // initialize next level
        var parentLevel = new ArrayList<byte[]>();
        // loop over every pair (use: for i in range(0, len(hashes), 2))
        for (int i = 0; i < hashes.size(); i = i + 2) {
            // get the merkle parent of the hashes at index i and i+1
            var parent = merkleParent(hashes.get(i), hashes.get(i + 1));
            // append parent to parent level
            parentLevel.add(parent);
        }
        // return parent level
        return parentLevel;
    }

    /**
     * Takes a list of binary hashes and returns the merkle root
     *
     * @param hashes a {@link java.util.List} object
     * @return an array of {@link byte} objects
     */
    public static byte[] merkleRoot(List<byte[]> hashes) {
        // current level starts as hashes
        var currentLevel = hashes;
        // loop until there's exactly 1 element
        while (currentLevel.size() > 1) {
            // current level becomes the merkle parent level
            currentLevel = merkleParentLevel(currentLevel);
        }
        // return the 1st item of the current level
        return currentLevel.get(0);
    }
}
