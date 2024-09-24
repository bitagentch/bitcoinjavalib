package ch.bitagent.bitcoin.java.ecc;

import ch.bitagent.bitcoin.java.helper.Helper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivateKeyTest {

    @Test
    void sign() {
        var pk = new PrivateKey(Hex.parse(Helper.hash256("my secret".getBytes())));
        var z = Hex.parse(Helper.hash256("my message".getBytes()));
        var sig = pk.sign(z);
        assertTrue(pk.getPoint().verify(z, sig));

        pk = new PrivateKey(Hex.parse(Helper.randomBytes(S256Point.N.toBytes(32).length)));
        z = Hex.parse(Helper.randomBytes(Int.parse(2).pow(Int.parse(256)).toBytes(32).length));
        sig = pk.sign(z);
        assertTrue(pk.getPoint().verify(z, sig));
    }

    @Test
    void wif() {
        var pk = new PrivateKey(Int.parse(2).pow(Int.parse(256)).sub(Int.parse(2).pow(Int.parse(199))));
        var expected = "L5oLkpV3aqBJ4BgssVAsax1iRa77G5CVYnv9adQ6Z87te7TyUdSC";
        assertEquals(expected, pk.wif(true, false));

        pk = new PrivateKey(Int.parse(2).pow(Int.parse(256)).sub(Int.parse(2).pow(Int.parse(201))));
        expected = "93XfLeifX7Jx7n7ELGMAf1SUR6f9kgQs8Xke8WStMwUtrDucMzn";
        assertEquals(expected, pk.wif(false, true));

        pk = new PrivateKey(Hex.parse("0dba685b4511dbd3d368e5c4358a1277de9486447af7b3604a69b8d9d8b7889d"));
        expected = "5HvLFPDVgFZRK9cd4C5jcWki5Skz6fmKqi1GQJf5ZoMofid2Dty";
        assertEquals(expected, pk.wif(false, false));

        pk = new PrivateKey(Hex.parse("1cca23de92fd1862fb5b76e5f4f50eb082165e5191e116c18ed1a6b24be6a53f"));
        expected = "cNYfWuhDpbNM1JWc3c6JTrtrFVxU4AGhUKgw5f93NP2QaBqmxKkg";
        assertEquals(expected, pk.wif(true, true));
    }
}