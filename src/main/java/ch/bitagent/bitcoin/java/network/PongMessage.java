package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.helper.Bytes;

import java.io.ByteArrayInputStream;

public class PongMessage implements Message {

    public static final String COMMAND = "pong";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final byte[] nonce;

    public PongMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    public static PongMessage parse(ByteArrayInputStream stream) {
        var nonce = Bytes.read(stream, 8);
        return new PongMessage(nonce);
    }

    @Override
    public byte[] serialize() {
        return this.nonce;
    }
}
