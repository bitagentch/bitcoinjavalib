package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.helper.Bytes;

import java.io.ByteArrayInputStream;

/**
 * <p>PongMessage class.</p>
 */
public class PongMessage implements Message {

    /** Constant <code>COMMAND="pong"</code> */
    public static final String COMMAND = "pong";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final byte[] nonce;

    /**
     * <p>Constructor for PongMessage.</p>
     *
     * @param nonce an array of {@link byte} objects
     */
    public PongMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * <p>parse.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.lib.network.PongMessage} object
     */
    public static PongMessage parse(ByteArrayInputStream stream) {
        var nonce = Bytes.read(stream, 8);
        return new PongMessage(nonce);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize() {
        return this.nonce;
    }
}
