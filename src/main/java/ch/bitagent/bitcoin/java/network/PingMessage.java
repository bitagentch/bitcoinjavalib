package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.helper.Bytes;

import java.io.ByteArrayInputStream;

public class PingMessage implements Message {

    public static final String COMMAND = "ping";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final byte[] nonce;

    public PingMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    public static PingMessage parse(ByteArrayInputStream stream) {
        var nonce = Bytes.read(stream, 8);
        return new PingMessage(nonce);
    }

    @Override
    public byte[] serialize() {
        return this.nonce;
    }
}
