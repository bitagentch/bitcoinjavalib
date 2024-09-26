package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.helper.Bytes;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>SimpleNode class.</p>
 */
public class SimpleNode {

    private static final Logger log = Logger.getLogger(SimpleNode.class.getSimpleName());

    private final String host;
    private final Integer port;
    private final Boolean testnet;
    private final boolean logging;
    private final SocketChannel socketChannel;

    /**
     * <p>Constructor for SimpleNode.</p>
     *
     * @param host a {@link java.lang.String} object
     * @param port a {@link java.lang.Integer} object
     * @param testnet a {@link java.lang.Boolean} object
     * @param logging a {@link java.lang.Boolean} object
     */
    public SimpleNode(String host, Integer port, Boolean testnet, Boolean logging) {
        this.host = Objects.requireNonNullElse(host, "localhost");
        this.port = Objects.requireNonNullElseGet(port, () -> {
            if (Boolean.TRUE.equals(testnet)) {
                return 18333;
            } else {
                return 8333;
            }
        });
        this.testnet = Objects.requireNonNullElse(testnet, false);
        this.logging = Objects.requireNonNullElse(logging, false);
        try {
            this.socketChannel = SocketChannel.open();
            this.socketChannel.configureBlocking(true);
            this.socketChannel.connect(new InetSocketAddress(this.host, this.port));
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * <p>close.</p>
     */
    public void close() {
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    /**
     * Do a handshake with the other node.
     * Handshake is sending a version message and getting a verack back.
     *
     * @return an array of {@link byte} objects
     */
    public byte[] handshake() {
        // create a version message
        var version = new VersionMessage();
        // send the command
        this.send(version);
        // wait for a verack message
        var envelope = this.waitFor(Set.of(VerAckMessage.COMMAND, SendCompactMessage.COMMAND));
        if (envelope == null) {
            log.severe("no verack or sendcmpct");
            return new byte[0];
        }
        return envelope.getPayload();
    }

    /**
     * Send a message to the connected node
     *
     * @param message a {@link ch.bitagent.bitcoin.java.network.Message} object
     */
    public void send(Message message) {
        // create a network envelope
        var envelope = new NetworkEnvelope(message.getCommand(), message.serialize(), this.testnet);
        if (this.logging) {
            log.info(String.format("sending %s", envelope));
        }
        // send the serialized envelope over the socket using sendall
        try {
            this.socketChannel.write(ByteBuffer.wrap(envelope.serialize()));
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Read a message from the socket
     *
     * @return a {@link ch.bitagent.bitcoin.java.network.NetworkEnvelope} object
     */
    public NetworkEnvelope read() {
        try {
            long start = System.currentTimeMillis();
            log.fine("start ...");
            var buffer = ByteBuffer.allocate(256 * 1024);
            int currentLength = 0;
            int expectedLength = -1;
            while (true) {
                try {
                    currentLength += this.socketChannel.read(buffer);
                } catch (IOException e) {
                    log.severe(e.getMessage());
                    return null;
                }
                if (currentLength > 0) {
                    expectedLength = NetworkEnvelope.parseLength(new ByteArrayInputStream(buffer.array()));
                }
                if (currentLength < 0) {
                    log.warning(String.format("no data - currentLength %s < 0, expectedLength %s", currentLength, expectedLength));
                    return null;
                }
                if (currentLength >= expectedLength) {
                    log.fine(String.format("data read - currentLength %s >= expectedLength %s", currentLength, expectedLength));
                    break;
                }
            }
            if (buffer.remaining() < 16 * 1024) {
                log.warning(String.format("buffer to small with remaining %s", buffer.remaining()));
            }
            var envelope = NetworkEnvelope.parse(new ByteArrayInputStream(buffer.array()), this.testnet);
            if (this.logging) {
                log.fine(String.format("receiving %s", Bytes.byteArrayToString(envelope.getCommand())));
            }
            log.fine(String.format("in %sms.", System.currentTimeMillis() - start));
            return envelope;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Wait for one of the messages in the list
     *
     * @param commands a {@link java.util.Set} object
     * @return a {@link ch.bitagent.bitcoin.java.network.NetworkEnvelope} object
     */
    public NetworkEnvelope waitFor(Set<String> commands) {
        try {
            log.fine(String.format("%s ...", String.join(", ", commands)));
            // initialize the command we have, which should be None
            String command = "";
            // loop until the command is in the commands we want
            NetworkEnvelope envelope = null;
            while (!commands.contains(command)) {
                // get the next network message
                envelope = this.read();
                if (envelope != null) {
                    // set the command to be evaluated
                    command = Bytes.byteArrayToString(envelope.getCommand());
                    // we know how to respond to version and ping, handle that here
                    if (envelope.isCommand(VersionMessage.COMMAND)) {
                        // send verack
                        this.send(new VerAckMessage());
                    } else if (envelope.isCommand(PingMessage.COMMAND)) {
                        // send pong
                        this.send(new PongMessage(envelope.getPayload()));
                    } else if (commands.contains(command)) {
                        log.fine(String.format("got %s", command));
                    } else {
                        log.warning(String.format("got %s unexpected", command));
                    }
                } else {
                    break;
                }
            }
            if (envelope != null) {
                if (this.logging) {
                    log.info(String.format("return %s", Bytes.byteArrayToString(envelope.getCommand())));
                }
                return envelope;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }
}
