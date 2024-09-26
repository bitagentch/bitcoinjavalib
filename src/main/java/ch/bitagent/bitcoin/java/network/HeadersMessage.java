package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.block.Block;
import ch.bitagent.bitcoin.java.helper.Varint;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

/**
 * <p>HeadersMessage class.</p>
 */
public class HeadersMessage implements Message {

    private static final Logger log = Logger.getLogger(HeadersMessage.class.getSimpleName());

    /** Constant <code>COMMAND="headers"</code> */
    public static final String COMMAND = "headers";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Block[] blocks;

    /**
     * <p>Constructor for HeadersMessage.</p>
     *
     * @param blocks an array of {@link ch.bitagent.bitcoin.java.block.Block} objects
     */
    public HeadersMessage(Block[] blocks) {
        this.blocks = blocks;
    }

    /**
     * <p>parse.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.java.network.HeadersMessage} object
     */
    public static HeadersMessage parse(ByteArrayInputStream stream) {
        // number of headers is in a varint
        var numHeaders = Varint.read(stream);
        // initialize the blocks array
        var blocks = new Block[numHeaders.intValue()];
        // loop through number of headers times
        for (int i = 0; i < numHeaders.intValue(); i++) {
            // add a block to the blocks array by parsing the stream
            blocks[i] = Block.parse(stream);
            // read the next varint (num_txs)
            var numTxs = Varint.read(stream);
            // num_txs should be 0 or raise a RuntimeError
            if (numTxs.intValue() != 0) {
                String error = "number of txs not 0";
                log.severe(error);
                throw new IllegalStateException(error);
            }
        }
        // return a class instance
        return new HeadersMessage(blocks);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    /**
     * <p>Getter for the field <code>blocks</code>.</p>
     *
     * @return an array of {@link ch.bitagent.bitcoin.java.block.Block} objects
     */
    public Block[] getBlocks() {
        return blocks;
    }
}
