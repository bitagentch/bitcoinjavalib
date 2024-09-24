package ch.bitagent.bitcoin.java.network;

public class VerAckMessage implements Message {

    public static final String COMMAND = "verack";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    public static VerAckMessage parse(byte[] stream) {
        return new VerAckMessage();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
