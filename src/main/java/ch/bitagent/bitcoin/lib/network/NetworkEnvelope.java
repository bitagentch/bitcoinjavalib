package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * <p>NetworkEnvelope class.</p>
 */
public class NetworkEnvelope {

    private static final Logger log = Logger.getLogger(NetworkEnvelope.class.getSimpleName());

    private static final byte[] NETWORK_MAGIC = new byte[]{(byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9};
    private static final byte[] TESTNET_NETWORK_MAGIC = new byte[]{(byte) 0x0b, (byte) 0x11, (byte) 0x09, (byte) 0x07};

    private final byte[] command;
    private final byte[] payload;
    private final Boolean testnet;
    private final byte[] magic;

    /**
     * <p>Constructor for NetworkEnvelope.</p>
     *
     * @param command an array of {@link byte} objects
     * @param payload an array of {@link byte} objects
     * @param testnet a {@link java.lang.Boolean} object
     */
    public NetworkEnvelope(byte[] command, byte[] payload, Boolean testnet) {
        this.command = command;
        this.payload = payload;
        this.testnet = Objects.requireNonNullElse(testnet, false);
        if (Boolean.TRUE.equals(testnet)) {
            this.magic = TESTNET_NETWORK_MAGIC;
        } else {
            this.magic = NETWORK_MAGIC;
        }
    }

    /**
     * <p>parseLength.</p>
     *
     * @param stream a {@link java.io.ByteArrayInputStream} object
     * @return a int
     */
    public static int parseLength(ByteArrayInputStream stream) {
        Bytes.read(stream, 4);
        Bytes.read(stream, 12);
        var length = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        return length.intValue();
    }

    /**
     * Takes a stream and creates a NetworkEnvelope
     *
     * @param stream  a {@link java.io.ByteArrayInputStream} object
     * @param testnet a {@link java.lang.Boolean} object
     * @return a {@link ch.bitagent.bitcoin.lib.network.NetworkEnvelope} object
     */
    public static NetworkEnvelope parse(ByteArrayInputStream stream, Boolean testnet) {
        // check the network magic
        var magic = Bytes.read(stream, 4);
        byte[] expectedMagic;
        if (Boolean.TRUE.equals(testnet)) {
            expectedMagic = TESTNET_NETWORK_MAGIC;
        } else {
            expectedMagic = NETWORK_MAGIC;
        }
        if (Arrays.compare(magic, expectedMagic) != 0) {
            String error = String.format("magic is not right %s vs %s", Hex.parse(magic), Hex.parse(expectedMagic));
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        // command 12 bytes
        var command = Bytes.read(stream, 12);
        // strip the trailing 0's
        command = Bytes.strip(command, (byte) 0x00);
        // payload length 4 bytes, little endian
        var payloadLength = Hex.parse(Bytes.changeOrder(Bytes.read(stream, 4)));
        // checksum 4 bytes, first four of hash256 of payload
        var checksum = Bytes.read(stream, 4);
        // payload is of length payload_length
        var payload = Bytes.read(stream, payloadLength.intValue());
        // verify checksum
        var calculatedChecksum = Arrays.copyOfRange(Hash.hash256(payload), 0, 4);
        if (Arrays.compare(calculatedChecksum, checksum) != 0) {
            throw new IllegalArgumentException(String.format("checksum does not match - expected %s - calculated %s - payload length %s", Hex.parse(checksum), Hex.parse(calculatedChecksum), payload.length));
        } else {
            log.fine(String.format("checksum match %s - payload length %s", Hex.parse(checksum), payload.length));
        }
        // return an instance of the class
        return new NetworkEnvelope(command, payload, testnet);
    }

    /**
     * Returns the byte serialization of the entire network message
     *
     * @return an array of {@link byte} objects
     */
    public byte[] serialize() {
        var result = new ByteArrayOutputStream();
        // add the network magic
        result.writeBytes(this.magic);
        // command 12 bytes
        // fill with 0's
        var commandFiller = Bytes.initFill(12 - this.command.length, (byte) 0x00);
        result.writeBytes(Bytes.add(this.command, commandFiller));
        // payload length 4 bytes, little endian
        result.writeBytes(Int.parse(this.payload.length).toBytesLittleEndian(4));
        // checksum 4 bytes, first four of hash256 of payload
        result.writeBytes(Arrays.copyOfRange(Hash.hash256(this.payload), 0, 4));
        // payload
        result.writeBytes(this.payload);
        return result.toByteArray();
    }

    /**
     * <p>isCommand.</p>
     *
     * @param command a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean isCommand(String command) {
        return Arrays.equals(this.command, command.getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "NetworkEnvelope{" +
                "command=" + Bytes.byteArrayToString(command) +
                ", payload=" + Hex.parse(payload) +
                ", testnet=" + testnet +
                ", magic=" + Hex.parse(magic) +
                '}';
    }

    /**
     * <p>Getter for the field <code>command</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getCommand() {
        return command;
    }

    /**
     * <p>Getter for the field <code>payload</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * <p>Getter for the field <code>testnet</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getTestnet() {
        return testnet;
    }

    /**
     * <p>Getter for the field <code>magic</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getMagic() {
        return magic;
    }
}
