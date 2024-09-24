package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Varint;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class GetHeadersMessage implements Message {

    private static final Logger log = Logger.getLogger(GetHeadersMessage.class.getSimpleName());

    public static final String COMMAND = "getheaders";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int version;
    private final Int numHashes;
    private final byte[] startBlock;
    private final byte[] endBlock;

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
