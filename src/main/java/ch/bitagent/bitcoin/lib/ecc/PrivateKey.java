package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;

import javax.crypto.Mac;
import java.util.Arrays;

/**
 * A private key on a secp256k1 elliptic curve
 */
public class PrivateKey {

    private final Int secret;
    private final S256Point point;

    /**
     * <p>Constructor for PrivateKey.</p>
     *
     * @param secret a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public PrivateKey(Int secret) {
        this.secret = secret;
        this.point = S256Point.getG().mul(secret);
    }

    /**
     * parse
     *
     * @param secret .
     * @return .
     */
    public static PrivateKey parse(byte[] secret) {
        return new PrivateKey(Hex.parse(secret));
    }

    /**
     * <p>sign.</p>
     *
     * @param z a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Signature} object
     */
    public Signature sign(Int z) {
        var k = this.deterministicK(z);
        // r is the x coordinate of the resulting point k*G
        var r = ((S256Field) S256Point.getG().mul(k).getX()).num;
        // remember 1/k = pow(k, N-2, N)
        var kInv = k.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N);
        // s = (z+r*secret) / k
        var s = r.mul(this.secret).add(z).mul(kInv).mod(S256Point.N);
        if (s.gt(S256Point.N.div(Int.parse(2)))) {
            s = S256Point.N.sub(s);
        }
        // return an instance of Signature(r, s)
        return new Signature(r, s);
    }

    /**
     * <p>deterministicK.</p>
     *
     * @param z a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int deterministicK(Int z) {
        byte[] k = Bytes.initFill(32, (byte) 0x00);
        byte[] v = Bytes.initFill(32, (byte) 0x01);

        byte[] secretBytes = this.secret.toBytes(32);
        byte[] zBytes = z.toBytes(32);

        if (z.gt(S256Point.N)) {
            zBytes = z.sub(S256Point.N).toBytes(32);
        }

        Mac hmac = Hash.hmacS256Init(k);
        hmac.update(v);
        hmac.update(new byte[]{0x00});
        hmac.update(secretBytes);
        k = hmac.doFinal(zBytes);

        hmac = Hash.hmacS256Init(k);
        v = hmac.doFinal(v);

        hmac = Hash.hmacS256Init(k);
        hmac.update(v);
        hmac.update(new byte[]{0x01});
        hmac.update(secretBytes);
        k = hmac.doFinal(zBytes);

        hmac = Hash.hmacS256Init(k);
        v = hmac.doFinal(v);

        var one = Int.parse(1);
        while (true) {
            hmac = Hash.hmacS256Init(k);
            v = hmac.doFinal(v);

            var candidate = Hex.parse(v);
            if (candidate.ge(one) && candidate.lt(S256Point.N)) {
                return candidate;
            }

            hmac = Hash.hmacS256Init(k);
            hmac.update(v);
            k = hmac.doFinal(new byte[]{0x00});

            hmac = Hash.hmacS256Init(k);
            v = hmac.doFinal(v);
        }
    }

    /**
     * <p>wif.</p>
     *
     * @param compressed a boolean
     * @param testnet    a boolean
     * @return a {@link java.lang.String} object
     */
    public String wif(boolean compressed, boolean testnet) {
        byte[] secretBytes = this.secret.toBytes(32);
        byte prefix;
        if (testnet) {
            prefix = (byte) 0xef;
        } else {
            prefix = (byte) 0x80;
        }
        if (compressed) {
            return Base58.encodeChecksum(Bytes.add(new byte[][]{new byte[]{prefix}, secretBytes, new byte[]{0x01}}));
        } else {
            return Base58.encodeChecksum(Bytes.add(new byte[]{prefix}, secretBytes));
        }
    }

    /**
     * parseWif
     *
     * @param wif        .
     * @param compressed .
     * @param testnet    .
     * @return .
     */
    public static PrivateKey parseWif(String wif, boolean compressed, boolean testnet) {
        var decodedBytes = Base58.decodeWif(wif, compressed);
        byte prefix = decodedBytes[0];
        byte prefixExpected;
        if (testnet) {
            prefixExpected = (byte) 0xef;
        } else {
            prefixExpected = (byte) 0x80;
        }
        if (prefix != prefixExpected) {
            throw new IllegalArgumentException("Invalid prefix");
        }
        byte[] secretBytes;
        if (compressed) {
            secretBytes = Arrays.copyOfRange(decodedBytes, 1, decodedBytes.length - 1);
        } else {
            secretBytes = Arrays.copyOfRange(decodedBytes, 1, decodedBytes.length);
        }
        Int secret = Hex.parse(secretBytes);
        return new PrivateKey(secret);
    }

    /**
     * <p>Get the point (public key)</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.S256Point} object
     */
    public S256Point getPoint() {
        return point;
    }

    /**
     * Get the secret
     *
     * @return .
     */
    public Int getSecret() {
        return this.secret;
    }
}
