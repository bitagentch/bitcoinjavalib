package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.*;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Varint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Message
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0137.mediawiki">BIP-0137</a>
 * <a href="https://github.com/sparrowwallet/drongo/blob/master/src/main/java/com/sparrowwallet/drongo/crypto/ECKey.java">Drongo ECKey</a>
 */
public class Message {

    private static final Logger log = Logger.getLogger(Message.class.getSimpleName());

    private static final String BITCOIN_SIGNED_MESSAGE_HEADER = "Bitcoin Signed Message:\n";

    private Message() {
    }

    /**
     * sign
     *
     * @param privateKey  .
     * @param message     .
     * @param addressType .
     * @param electrum    .
     * @return .
     */
    public static String sign(PrivateKey privateKey, String message, String addressType, boolean electrum) {
        var messageFormatted = formatMessageForSigning(message);
        var z = Hex.parse(Hash.hash256(messageFormatted));
        int counter = 0;
        var signature = privateKey.sign(z, counter);
        while (signature.der().length >= 71) {
            // A low R signature will have less than 71 bytes when encoded to DER
            signature = privateKey.sign(z, ++counter);
        }
        byte recoveryId = findRecoveryId(z, signature, privateKey.getPoint());
        if (electrum) {
            recoveryId -= 8;
        }
        int headerByte = recoveryId + getSigningTypeConstant(addressType);
        byte[] sigData = new byte[65];  // 1 header + 32 bytes for R + 32 bytes for S
        sigData[0] = (byte) headerByte;
        System.arraycopy(signature.getR().toBytes(32), 0, sigData, 1, 32);
        System.arraycopy(signature.getS().toBytes(32), 0, sigData, 33, 32);
        return new String(Base64.getEncoder().encode(sigData));
    }

    /**
     * verify
     *
     * @param publicKey    .
     * @param signatureB64 .
     * @param message      .
     * @param electrum     .
     * @return .
     */
    public static boolean verify(S256Point publicKey, String signatureB64, String message, boolean electrum) {
        var signatureDecoded = Base64.getDecoder().decode(signatureB64);
        var messageFormatted = formatMessageForSigning(message);
        var z = Hex.parse(Hash.hash256(messageFormatted));

        if (signatureDecoded.length < 65) {
            throw new IllegalArgumentException("Signature truncated, expected 65 bytes and got " + signatureDecoded.length);
        }
        int header = signatureDecoded[0] & 0xFF;
        if (header < 27 || header > 42) {
            throw new IllegalArgumentException("Header byte out of range: " + header);
        }
        var r = Hex.parse(Arrays.copyOfRange(signatureDecoded, 1, 33));
        var s = Hex.parse(Arrays.copyOfRange(signatureDecoded, 33, 65));
        var signature = new Signature(r, s);

        if (header >= 39) { // this is a bech32 signature
            header -= 12;
        } else if (header >= 35 && !electrum) { // this is a segwit p2sh signature
            header -= 8;
        } else if (header >= 31) { // this is a compressed key signature
            header -= 4;
        }
        int recoveryId = header - 27;
        var key = recoverFromSignature(recoveryId, signature, z);
        if (key == null) {
            throw new IllegalArgumentException("Could not recover public key from signature");
        }
        return key.eq(publicKey);
    }

    private static byte[] formatMessageForSigning(String message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER.getBytes().length);
            bos.write(BITCOIN_SIGNED_MESSAGE_HEADER.getBytes());
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            var size = Varint.encode(Int.parse(messageBytes.length));
            bos.write(size);
            bos.write(messageBytes);
            return bos.toByteArray();
        } catch (IOException e) {
            log.severe(e.getMessage());
            return new byte[0];
        }
    }

    private static byte findRecoveryId(Int message, Signature sig, S256Point p) {
        byte recId = -1;
        for (byte i = 0; i < 4; i++) {
            S256Point k = recoverFromSignature(i, sig, message);
            if (k != null && k.eq(p)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new IllegalStateException("Could not construct a recoverable key. This should never happen.");
        }
        return recId;
    }

    private static S256Point recoverFromSignature(int recId, Signature sig, Int message) {
        if (recId < 0) {
            throw new IllegalArgumentException("recId must be positive");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        var n = S256Point.N;
        var i = Int.parse(recId / 2);
        var x = sig.getR().add(i.mul(n));
        var prime = S256Point.P;
        if (x.ge(prime)) {
            return null;
        }
        S256Point R = decompressKey(x, (recId & 1) == 1);
        if (R.mul(n).getX() != null) {
            return null;
        }
        var e = message;

        // Q = mi(r) * (sR - eG)
        // Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        var eInv = Int.parse(0).sub(e).mod(n); // -e
        var rInv = Int.parse(sig.getR().bigInt().modInverse(n.bigInt())); // mi(r)
        var srInv = rInv.mul(sig.getS()).mod(n); // mi(r) * s
        var eInvrInv = rInv.mul(eInv).mod(n); // mi(r) * -e
        var q1 = R.mul(srInv);
        var q2 = S256Point.getG().mul(eInvrInv);
        return q1.add(q2);
    }

    private static S256Point decompressKey(Int xBN, boolean yBit) {
        byte[] compEnc = xBN.toBytes(33);
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return S256Point.parse(compEnc);
    }

    private static int getSigningTypeConstant(String addressType) {
        if (Address.P2PKH.equalsIgnoreCase(addressType)) {
            return 31;
        } else if (Address.P2SH.equalsIgnoreCase(addressType)) {
            return 35;
        } else if (Address.BECH32.equalsIgnoreCase(addressType)) {
            return 39;
        }

        throw new IllegalArgumentException("Address type " + addressType + " is not supported for message signing");
    }
}