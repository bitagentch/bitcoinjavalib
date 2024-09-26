package ch.bitagent.bitcoin.java.block;

import ch.bitagent.bitcoin.java.ecc.Hex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>MerkleTreeLevel class.</p>
 */
public class MerkleTreeLevel {

    private List<byte[]> items;

    /**
     * <p>Constructor for MerkleTreeLevel.</p>
     *
     * @param numItems a int
     */
    public MerkleTreeLevel(int numItems) {
        this.items = new ArrayList<>(Collections.nCopies(numItems, null));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "{" +
                items.stream().map(item -> {
                    if (item == null) return null;
                    var shrt = String.format("%s...", Hex.parse(item).toString().substring(0, 8));
                    if (this.items.size() == 1) {
                        return String.format("*%s*", shrt.substring(0, shrt.length()-2));
                    } else {
                        return shrt;
                    }
                }).collect(Collectors.joining(", ")) +
                '}';
    }

    /**
     * <p>Getter for the field <code>items</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<byte[]> getItems() {
        return items;
    }

    /**
     * <p>Setter for the field <code>items</code>.</p>
     *
     * @param items a {@link java.util.List} object
     */
    public void setItems(List<byte[]> items) {
        this.items = items;
    }
}
