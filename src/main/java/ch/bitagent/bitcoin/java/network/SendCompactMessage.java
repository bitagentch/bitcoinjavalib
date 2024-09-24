package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;

import java.io.ByteArrayInputStream;

public class SendCompactMessage implements Message {

    public static final String COMMAND = "sendcmpct";

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int announce;
    private final Int version;

    public SendCompactMessage(Int announce, Int version) {
        this.announce = announce;
        this.version = version;
    }

    public static SendCompactMessage parse(ByteArrayInputStream stream) {
        // announce - 1 byte - An integer representing a boolean value, must be 0x01 (true) or 0x00 (false).
        var announce = Hex.parse(Bytes.read(stream, 1));
        // version - 8 bytes - A little-endian representation of a version number.
        // Version 2 compact blocks should be specified by setting version to 2
        var version = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 8)));
        // new instance
        return new SendCompactMessage(announce, version);
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "SendCompactMessage{" +
                "announce=" + announce +
                ", version=" + version +
                '}';
    }
}
