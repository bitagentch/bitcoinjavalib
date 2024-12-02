package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivateKeyTest {

    @Test
    void sign() {
        var pk = new PrivateKey(Hex.parse(Hash.hash256("my secret".getBytes())));
        var z = Hex.parse(Hash.hash256("my message".getBytes()));
        var sig = pk.sign(z, 0);
        assertTrue(pk.getPoint().verify(z, sig));

        pk = new PrivateKey(Hex.parse(Bytes.randomBytes(S256Point.N.toBytes(32).length)));
        z = Hex.parse(Bytes.randomBytes(Int.parse(2).pow(Int.parse(256)).toBytes(32).length));
        sig = pk.sign(z, 0);
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

        pk = new PrivateKey(Hex.parse("0C28FCA386C7A227600B2FE50B7CAE11EC86D3BF1FBE471BE89827E19D72AA1D"));
        expected = "5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ";
        assertEquals(expected, pk.wif(false, false));
    }

    @Test
    void parseWif() {
        PrivateKey pk = PrivateKey.parseWif("cNYfWuhDpbNM1JWc3c6JTrtrFVxU4AGhUKgw5f93NP2QaBqmxKkg", true, true);
        assertEquals(Hex.parse("1cca23de92fd1862fb5b76e5f4f50eb082165e5191e116c18ed1a6b24be6a53f"), pk.getSecret());

        pk = PrivateKey.parseWif("5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ", false, false);
        assertEquals(Hex.parse("0C28FCA386C7A227600B2FE50B7CAE11EC86D3BF1FBE471BE89827E19D72AA1D"), pk.getSecret());
    }
}