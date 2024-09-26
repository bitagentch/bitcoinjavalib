package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Varint;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * <p>GetHeadersMessage class.</p>
 */
public class GetHeadersMessage implements Message {

    private static final Logger log = Logger.getLogger(GetHeadersMessage.class.getSimpleName());

    /** Constant <code>COMMAND="getheaders"</code> */
    public static final String COMMAND = "getheaders";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int version;
    private final Int numHashes;
    private final byte[] startBlock;
    private final byte[] endBlock;

    /**
     * <p>Constructor for GetHeadersMessage.</p>
     *
     * @param version a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param numHashes a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param startBlock an array of {@link byte} objects
     * @param endBlock an array of {@link byte} objects
     */
    public GetHeadersMessage(Int version, Int numHashes, byte[] startBlock, byte[] endBlock) {
        this.version = Objects.requireNonNullElse(version, Int.parse(70015));
        this.numHashes = Objects.requireNonNullElse(numHashes, Int.parse(1));
        this.startBlock = Objects.requireNonNullElseGet(startBlock, () -> {
            String error = "a start block is required";
            log.severe(error);
            throw new IllegalArgumentException(error);
        });
        this.endBlock = Objects.requireNonNullElseGet(endBlock, () -> Bytes.initFill(32, (byte) 0x00));
    }

    /**
     * {@inheritDoc}
     *
     * Serialize this message to send over the network
     */
    @Override
    public byte[] serialize() {
        var result = new ByteArrayOutputStream();
        // protocol version is 4 bytes little-endian
        result.writeBytes(this.version.toBytesLittleEndian(4));
        // number of hashes is a varint
        result.writeBytes(Varint.encode(this.numHashes));
        // start block is in little-endian
        result.writeBytes(Bytes.changeOrder(this.startBlock));
        // end block is also in little-endian
        result.writeBytes(Bytes.changeOrder(this.endBlock));
        return result.toByteArray();
    }
}
