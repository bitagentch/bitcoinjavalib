package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;

/**
 * <p>Taproot: SegWit version 1 spending rules</p>
 *
 * <a href="https://github.com/bitcoin/bips/blob/master/bip-0341.mediawiki">BIP-0341</a>
 */
public class Taproot {

    private Taproot() {
    }

    public static byte[] tweakPubkey(byte[] pubkey, byte[] h) {
        var taggedHash = Hash.taggedHash("TapTweak", Bytes.add(pubkey, h));
        var t = Hex.parse(taggedHash);
        if (t.ge(S256Point.P)) {
            throw new IllegalStateException();
        }
        var P = S256Point.liftX(Hex.parse(pubkey));
        if (P == null) {
            throw new IllegalStateException();
        }
        var Q = P.add(S256Point.getG().mul(t));
        if (Q.hasEvenY()) {
            return new byte[0];
        } else {
            return Schnorr.getNumBytes(Q.getX());
        }
    }
}
