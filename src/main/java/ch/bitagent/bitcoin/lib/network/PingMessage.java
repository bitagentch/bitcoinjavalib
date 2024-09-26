package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.helper.Bytes;

import java.io.ByteArrayInputStream;

/**
 * <p>PingMessage class.</p>
 */
public class PingMessage implements Message {

    /** Constant <code>COMMAND="ping"</code> */
    public static final String COMMAND = "ping";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final byte[] nonce;

    /**
     * <p>Constructor for PingMessage.</p>
     *
     * @param nonce an array of {@link byte} objects
     */
    public PingMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * <p>parse.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a {@link ch.bitagent.bitcoin.lib.network.PingMessage} object
     */
    public static PingMessage parse(ByteArrayInputStream stream) {
        var nonce = Bytes.read(stream, 8);
        return new PingMessage(nonce);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize() {
        return this.nonce;
    }
}
