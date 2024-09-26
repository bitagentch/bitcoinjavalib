package ch.bitagent.bitcoin.java.network;

/**
 * <p>Message interface.</p>
 */
public interface Message {

    /**
     * <p>getCommand.</p>
     *
     * @return an array of {@link byte} objects
     */
    byte[] getCommand();

    /**
     * <p>serialize.</p>
     *
     * @return an array of {@link byte} objects
     */
    byte[] serialize();
}
