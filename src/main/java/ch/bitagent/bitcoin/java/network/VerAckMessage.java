package ch.bitagent.bitcoin.java.network;

/**
 * <p>VerAckMessage class.</p>
 */
public class VerAckMessage implements Message {

    /** Constant <code>COMMAND="verack"</code> */
    public static final String COMMAND = "verack";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    /**
     * <p>parse.</p>
     *
     * @param stream an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.java.network.VerAckMessage} object
     */
    public static VerAckMessage parse(byte[] stream) {
        return new VerAckMessage();
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
