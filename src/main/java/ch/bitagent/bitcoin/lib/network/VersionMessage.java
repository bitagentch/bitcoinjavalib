package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Helper;
import ch.bitagent.bitcoin.lib.helper.Varint;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * <p>VersionMessage class.</p>
 */
public class VersionMessage implements Message {

    /** Constant <code>COMMAND="version"</code> */
    public static final String COMMAND = "version";

    /** {@inheritDoc} */
    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    private final Int version;
    private final Int services;
    private final Int timestamp;
    private final Int receiverServices;
    private final byte[] receiverIp;
    private final Int receiverPort;
    private final Int senderServices;
    private final byte[] senderIp;
    private final Int senderPort;
    private final byte[] nonce;
    private final String userAgent;
    private final Int latestBlock;
    private final Boolean relay;

    /**
     * <p>Constructor for VersionMessage.</p>
     */
    public VersionMessage() {
        this(null, null, null,
                null, null, null,
                null, null, null,
                null, null, null, null);
    }

    /**
     * <p>Constructor for VersionMessage.</p>
     *
     * @param version a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param services a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param timestamp a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param receiverServices a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param receiverIp an array of {@link byte} objects
     * @param receiverPort a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param senderServices a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param senderIp an array of {@link byte} objects
     * @param senderPort a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param nonce an array of {@link byte} objects
     * @param userAgent a {@link java.lang.String} object
     * @param latestBlock a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param relay a {@link java.lang.Boolean} object
     */
    public VersionMessage(Int version, Int services, Int timestamp,
                          Int receiverServices, byte[] receiverIp, Int receiverPort,
                          Int senderServices, byte[] senderIp, Int senderPort,
                          byte[] nonce, String userAgent, Int latestBlock, Boolean relay) {
        this.version = Objects.requireNonNullElse(version, Int.parse(70015));
        this.services = Objects.requireNonNullElse(services, Int.parse(0));
        this.timestamp = Objects.requireNonNullElse(timestamp, Int.parse(String.valueOf(System.currentTimeMillis() / 1000)));
        this.receiverServices = Objects.requireNonNullElse(receiverServices, Int.parse(0));
        this.receiverIp = Objects.requireNonNullElse(receiverIp, new byte[]{0x00, 0x00, 0x00, 0x00});
        this.receiverPort = Objects.requireNonNullElse(receiverPort, Int.parse(8333));
        this.senderServices = Objects.requireNonNullElse(senderServices, Int.parse(0));
        this.senderIp = Objects.requireNonNullElse(senderIp, new byte[]{0x00, 0x00, 0x00, 0x00});
        this.senderPort = Objects.requireNonNullElse(senderPort, Int.parse(8333));
        this.nonce = Objects.requireNonNullElse(nonce, Helper.randomBytes(8));
        this.userAgent = Objects.requireNonNullElse(userAgent, "/bitcoinjavalib/");
        this.latestBlock = Objects.requireNonNullElse(latestBlock, Int.parse(0));
        this.relay = Objects.requireNonNullElse(relay, false);
    }

    /**
     * {@inheritDoc}
     *
     * Serialize this message to send over the network
     */
    @Override
    public byte[] serialize() {
        var result = new ByteArrayOutputStream();
        // version is 4 bytes little endian
        result.writeBytes(this.version.toBytesLittleEndian(4));
        // services is 8 bytes little endian
        result.writeBytes(this.services.toBytesLittleEndian(8));
        // timestamp is 8 bytes little endian
        result.writeBytes(this.timestamp.toBytesLittleEndian(8));
        // receiver services is 8 bytes little endian
        result.writeBytes(this.receiverServices.toBytesLittleEndian(8));
        // IPV4 is 10 00 bytes and 2 ff bytes then receiver ip
        result.writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        result.writeBytes(new byte[]{(byte) 0xff, (byte) 0xff});
        result.writeBytes(this.receiverIp);
        // receiver port is 2 bytes, big endian
        result.writeBytes(this.receiverPort.toBytes(2));
        // sender services is 8 bytes little endian
        result.writeBytes(this.senderServices.toBytesLittleEndian(8));
        // IPV4 is 10 00 bytes and 2 ff bytes then sender ip
        result.writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        result.writeBytes(new byte[]{(byte) 0xff, (byte) 0xff});
        result.writeBytes(this.senderIp);
        // sender port is 2 bytes, big endian
        result.writeBytes(this.senderPort.toBytes(2));
        // nonce should be 8 bytes
        result.writeBytes(Bytes.changeOrder(this.nonce));
        // useragent is a variable string, so varint first
        result.writeBytes(Varint.encode(Int.parse(this.userAgent.length())));
        result.writeBytes(this.userAgent.getBytes());
        // latest block is 4 bytes little endian
        result.writeBytes(this.latestBlock.toBytesLittleEndian(4));
        // relay is 00 if false, 01 if true
        if (Boolean.TRUE.equals(this.relay)) {
            result.writeBytes(new byte[]{0x01});
        } else {
            result.writeBytes(new byte[]{0x00});
        }
        return result.toByteArray();
    }
}
