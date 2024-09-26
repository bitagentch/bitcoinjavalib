package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Helper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureTest {

    @Test
    void der() {
        var r = Hex.parse("01");
        var s = Hex.parse("02");
        var sig1 = new Signature(r, s);
        var der = sig1.der();
        var sig2 = Signature.parse(der);
        assertTrue(r.eq(sig2.getR()));
        assertTrue(s.eq(sig2.getS()));

        for (int i = 0; i < 2; i++) {
            r = Hex.parse(Helper.randomBytes(Int.parse(2).pow(Int.parse(256)).toBytes(32).length));
            s = Hex.parse(Helper.randomBytes(Int.parse(2).pow(Int.parse(255)).toBytes(32).length));
            sig1 = new Signature(r, s);
            der = sig1.der();
            sig2 = Signature.parse(der);
            assertTrue(r.eq(sig2.getR()));
            assertTrue(s.eq(sig2.getS()));
        }
    }

}