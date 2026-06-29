package ch.bitagent.bitcoin.lib.block;

import ch.bitagent.bitcoin.lib.ecc.Int;

public class BlockHeight extends Block {

    private final Int height;

    public BlockHeight(Block block, Int height) {
        super(block.getVersion(), block.getPrevBlock(), block.getMerkleRoot(), block.getTimestamp(), block.getBits(), block.getNonce());
        this.height = height;
    }

    public Int getHeight() {
        return height;
    }
}
