package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.block.Block;
import ch.bitagent.bitcoin.java.helper.Varint;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

public class HeadersMessage implements Message {

    private static final Logger log = Logger.getLogger(HeadersMessage.class.getSimpleName());

    public static final String COMMAND = "headers";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Block[] blocks;

    public HeadersMessage(Block[] blocks) {
        this.blocks = blocks;
    }

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

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    public Block[] getBlocks() {
        return blocks;
    }
}
