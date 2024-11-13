package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * <p>Schnorr Signatures for secp256k1</p>
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0340.mediawiki">BIP-0340</a>
 */
public class Schnorr {

    private Schnorr() {
    }

    private static final Int P = Hex.parse("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F");

    private static final int BYTES_LENGTH = 32;

    private static byte[] getNumBytes(PointOperators po) {
        return Point.getNum(po).toBytes(BYTES_LENGTH);
    }

    private static boolean hasEvenY(S256Point s256Point) {
        return Point.getNum(s256Point.getY()).mod(Int.parse(2)).eq(Int.parse(0));
    }

    private static byte[] taggedHash(String tag, byte[] msg) {
        var tagHash = Hash.sha256(tag.getBytes());
        var tagged = Bytes.add(new byte[][]{tagHash, tagHash, msg});
        return Hash.sha256(tagged);
    }

    /**
     * <p>sign</p>
     *
     * @param msg     .
     * @param seckey  .
     * @param auxRand .
     * @return .
     */
    public static byte[] sign(byte[] msg, byte[] seckey, byte[] auxRand) {
        Int d0 = Hex.parse(seckey);
        if (d0.lt(Int.parse(1)) || d0.gt(S256Point.N.sub(Int.parse(1)))) {
            throw new IllegalArgumentException("The secret key must be an integer in the range 1..n-1.");
        }
        if (auxRand.length != BYTES_LENGTH) {
            throw new IllegalArgumentException(String.format("aux_rand must be %s bytes instead of %s.", BYTES_LENGTH, auxRand.length));
        }
        var p = S256Point.getG().mul(d0);
        var d = d0;
        if (!hasEvenY(p)) {
            d = S256Point.N.sub(d0);
        }
        var t = Bytes.xor(d.toBytes(BYTES_LENGTH), taggedHash("BIP0340/aux", auxRand));
        Int k0 = Hex.parse(taggedHash("BIP0340/nonce", Bytes.add(new byte[][]{t, getNumBytes(p.getX()), msg}))).mod(S256Point.N);
        if (k0.eq(Int.parse(0))) {
            throw new IllegalStateException("Failure. This happens only with negligible probability.");
        }
        var r = S256Point.getG().mul(k0);
        var k = k0;
        if (!hasEvenY(r)) {
            k = S256Point.N.sub(k0);
        }
        var e = Hex.parse(taggedHash("BIP0340/challenge", Bytes.add(new byte[][]{(getNumBytes(r.getX())), getNumBytes(p.getX()), msg}))).mod(S256Point.N);
        var sig = Bytes.add(getNumBytes(r.getX()), e.mul(d).add(k).mod(S256Point.N).toBytes(BYTES_LENGTH));
        if (!verify(msg, getNumBytes(p.getX()), sig)) {
            throw new IllegalStateException("The created signature does not pass verification.");
        }
        return sig;
    }

    private static S256Point liftX(Int x) {
        if (x.ge(P)) {
            return null;
        }
        var ySq = x.powMod(Int.parse(3), P).add(Int.parse(7)).mod(P);
        var y = ySq.powMod(P.add(Int.parse(1)).div(Int.parse(4)), P);
        if (y.powMod(Int.parse(2), P).ne(ySq)) {
            return null;
        }
        var xField = new S256Field(x);
        var yField = y.bigInt().and(BigInteger.ONE).compareTo(BigInteger.ZERO) == 0 ? new S256Field(y) : new S256Field(P.sub(y));
        return new S256Point(xField, yField);
    }

    /**
     * <p>verify</p>
     *
     * @param msg    .
     * @param pubkey .
     * @param sig    .
     * @return .
     */
    public static boolean verify(byte[] msg, byte[] pubkey, byte[] sig) {
        if (pubkey.length != BYTES_LENGTH) {
            throw new IllegalArgumentException("'The public key must be a 32-byte array.'");
        }
        if (sig.length != 2 * BYTES_LENGTH) {
            throw new IllegalArgumentException("The signature must be a 64-byte array.");
        }
        var p = liftX(Hex.parse(pubkey));
        if (p == null) {
            return false;
        }
        var r = Hex.parse(Arrays.copyOfRange(sig, 0, BYTES_LENGTH));
        if (r.ge(P)) {
            return false;
        }
        var s = Hex.parse(Arrays.copyOfRange(sig, BYTES_LENGTH, 2 * BYTES_LENGTH));
        if (s.ge(S256Point.N)) {
            return false;
        }
        var e = Hex.parse(taggedHash("BIP0340/challenge", Bytes.add(new byte[][]{Arrays.copyOfRange(sig, 0, BYTES_LENGTH), pubkey, msg}))).mod(S256Point.N);
        var rr = S256Point.getG().mul(s).add(p.mul(S256Point.N.sub(e)));
        if (rr == null) {
            return false;
        }
        if (!hasEvenY(rr)) {
            return false;
        }
        if (Point.getNum(rr.getX()).ne(r)) {
            return false;
        }
        return true;
    }
}
