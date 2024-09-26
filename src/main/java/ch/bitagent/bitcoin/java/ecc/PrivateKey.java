package ch.bitagent.bitcoin.java.ecc;

import ch.bitagent.bitcoin.java.helper.Base58;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;

import javax.crypto.Mac;

/**
 * A private key on a secp256k1 elliptic curve
 */
public class PrivateKey {

    private final Int secret;
    private final S256Point point;

    /**
     * <p>Constructor for PrivateKey.</p>
     *
     * @param secret a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public PrivateKey(Int secret) {
        this.secret = secret;
        this.point = S256Point.getG().mul(secret);
    }

    /**
     * <p>sign.</p>
     *
     * @param z a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Signature} object
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
     * @param z a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public Int deterministicK(Int z) {
        byte[] k = Bytes.initFill(32, (byte) 0x00);
        byte[] v = Bytes.initFill(32, (byte) 0x01);

        byte[] secretBytes = this.secret.toBytes(32);
        byte[] zBytes = z.toBytes(32);

        if (z.gt(S256Point.N)) {
            zBytes = z.sub(S256Point.N).toBytes(32);
        }

        Mac hmac = Helper.hmacS256Init(k);
        hmac.update(v);
        hmac.update(new byte[]{0x00});
        hmac.update(secretBytes);
        k = hmac.doFinal(zBytes);

        hmac = Helper.hmacS256Init(k);
        v = hmac.doFinal(v);

        hmac = Helper.hmacS256Init(k);
        hmac.update(v);
        hmac.update(new byte[]{0x01});
        hmac.update(secretBytes);
        k = hmac.doFinal(zBytes);

        hmac = Helper.hmacS256Init(k);
        v = hmac.doFinal(v);

        var one = Int.parse(1);
        while (true) {
            hmac = Helper.hmacS256Init(k);
            v = hmac.doFinal(v);

            var candidate = Hex.parse(v);
            if (candidate.ge(one) && candidate.lt(S256Point.N)) {
                return candidate;
            }

            hmac = Helper.hmacS256Init(k);
            hmac.update(v);
            k = hmac.doFinal(new byte[]{0x00});

            hmac = Helper.hmacS256Init(k);
            v = hmac.doFinal(v);
        }
    }

    /**
     * <p>wif.</p>
     *
     * @param compressed a boolean
     * @param testnet a boolean
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
     * <p>Getter for the field <code>point</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.java.ecc.S256Point} object
     */
    public S256Point getPoint() {
        return point;
    }
}
