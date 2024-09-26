package ch.bitagent.bitcoin.lib.network;

/**
 * <p>GenericMessage class.</p>
 */
public class GenericMessage implements Message {

    private final String COMMAND;
    private final byte[] payload;

    /**
     * <p>Constructor for GenericMessage.</p>
     *
     * @param command a {@link java.lang.String} object
     * @param payload an array of {@link byte} objects
     */
    public GenericMessage(String command, byte[] payload) {
        this.COMMAND = command;
        this.payload = payload;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return this.COMMAND.getBytes();
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize() {
        return this.payload;
    }
}
