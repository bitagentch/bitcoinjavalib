package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.ecc.*;
import ch.bitagent.bitcoin.lib.helper.Helper;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Chapter3Test {

    private static final Logger log = Logger.getLogger(Chapter3Test.class.getSimpleName());

    @Test
    void example1() {
        var a = new FieldElement(0, 223);
        var b = new FieldElement(7, 223);
        var x = new FieldElement(192, 223);
        var y = new FieldElement(105, 223);
        var p1 = new Point(x, y, a, b);
        assertEquals("Point(192,105)_0_7 FieldElement(223)", p1.toString());
    }

    @Test
    void example3() {
        var prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var x1 = new FieldElement(192, prime);
        var y1 = new FieldElement(105, prime);
        var x2 = new FieldElement(17, prime);
        var y2 = new FieldElement(56, prime);
        var p1 = new Point(x1, y1, a, b);
        var p2 = new Point(x2, y2, a, b);
        assertEquals("Point(170,142)_0_7 FieldElement(223)", p1.add(p2).toString());
    }

    @Test
    void exercise4() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);

        var x1 = new FieldElement(192, prime);
        var y1 = new FieldElement(105, prime);
        var p1 = new Point(x1, y1, a, b);
        var p = p1.add(p1);
        log.fine(p.toString());
        var x2 = new FieldElement(49, prime);
        var y2 = new FieldElement(71, prime);
        var p2 = new Point(x2, y2, a, b);
        assertTrue(p.eq(p2));

        x1 = new FieldElement(143, prime);
        y1 = new FieldElement(98, prime);
        p1 = new Point(x1, y1, a, b);
        p = p1.add(p1);
        log.fine(p.toString());
        x2 = new FieldElement(64, prime);
        y2 = new FieldElement(168, prime);
        p2 = new Point(x2, y2, a, b);
        assertTrue(p.eq(p2));

        x1 = new FieldElement(47, prime);
        y1 = new FieldElement(71, prime);
        p1 = new Point(x1, y1, a, b);
        p = p1.add(p1);
        log.fine(p.toString());
        x2 = new FieldElement(36, prime);
        y2 = new FieldElement(111, prime);
        p2 = new Point(x2, y2, a, b);
        assertTrue(p.eq(p2));

        var p4 = p1.add(p1).add(p1).add(p1);
        log.fine(p4.toString());
        x2 = new FieldElement(194, prime);
        y2 = new FieldElement(51, prime);
        p2 = new Point(x2, y2, a, b);
        assertTrue(p4.eq(p2));

        var p8 = p1.add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1);
        log.fine(p8.toString());
        x2 = new FieldElement(116, prime);
        y2 = new FieldElement(55, prime);
        p2 = new Point(x2, y2, a, b);
        assertTrue(p8.eq(p2));

        var p21 = p1.add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1).add(p1);
        log.fine(p21.toString());
        p2 = new Point(null, null, a, b);
        assertTrue(p21.eq(p2));
    }

    @Test
    void example4() {
        var prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var x = new FieldElement(47, prime);
        var y = new FieldElement(71, prime);
        var p = new Point(x, y, a, b);
        assertEquals("Point(47,71)_0_7 FieldElement(223)", p.mul(Int.parse(1)).toString());
        assertEquals("Point(36,111)_0_7 FieldElement(223)", p.mul(Int.parse(2)).toString());
        assertEquals("Point(15,137)_0_7 FieldElement(223)", p.mul(Int.parse(3)).toString());
        assertEquals("Point(194,51)_0_7 FieldElement(223)", p.mul(Int.parse(4)).toString());
        assertEquals("Point(126,96)_0_7 FieldElement(223)", p.mul(Int.parse(5)).toString());
        assertEquals("Point(139,137)_0_7 FieldElement(223)", p.mul(Int.parse(6)).toString());
        assertEquals("Point(92,47)_0_7 FieldElement(223)", p.mul(Int.parse(7)).toString());
        assertEquals("Point(116,55)_0_7 FieldElement(223)", p.mul(Int.parse(8)).toString());
        assertEquals("Point(69,86)_0_7 FieldElement(223)", p.mul(Int.parse(9)).toString());
        assertEquals("Point(154,150)_0_7 FieldElement(223)", p.mul(Int.parse(10)).toString());
        assertEquals("Point(154,73)_0_7 FieldElement(223)", p.mul(Int.parse(11)).toString());
        assertEquals("Point(69,137)_0_7 FieldElement(223)", p.mul(Int.parse(12)).toString());
        assertEquals("Point(116,168)_0_7 FieldElement(223)", p.mul(Int.parse(13)).toString());
        assertEquals("Point(92,176)_0_7 FieldElement(223)", p.mul(Int.parse(14)).toString());
        assertEquals("Point(139,86)_0_7 FieldElement(223)", p.mul(Int.parse(15)).toString());
        assertEquals("Point(126,127)_0_7 FieldElement(223)", p.mul(Int.parse(16)).toString());
        assertEquals("Point(194,172)_0_7 FieldElement(223)", p.mul(Int.parse(17)).toString());
        assertEquals("Point(15,86)_0_7 FieldElement(223)", p.mul(Int.parse(18)).toString());
        assertEquals("Point(36,112)_0_7 FieldElement(223)", p.mul(Int.parse(19)).toString());
        assertEquals("Point(47,152)_0_7 FieldElement(223)", p.mul(Int.parse(20)).toString());
    }

    @Test
    void exercise5() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var x = new FieldElement(15, prime);
        var y = new FieldElement(86, prime);
        var p = new Point(x, y, a, b);
        var inf = new Point(null, null, a, b);
        var product = p;
        var count = 1;
        while (product.ne(inf)) {
            product = product.add(p);
            count += 1;
        }
        log.fine(String.format("count %s", count));
        assertEquals(7, count);
    }

    @Test
    void example5() {
        var prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var x = new FieldElement(15, prime);
        var y = new FieldElement(86, prime);
        var p = new Point(x, y, a, b);
        assertEquals("Point(infinity)", p.mul(Int.parse(7)).toString());
    }

    @Test
    void example6() {
        var gx = Hex.parse("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798");
        var gy = Hex.parse("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8");
        var p = Int.parse(2).pow(Int.parse(256)).sub(Int.parse(2).pow(Int.parse(32))).sub(Int.parse(977));
        assertEquals(gy.pow(Int.parse(2)).mod(p), gx.pow(Int.parse(3)).add(Int.parse(7)).mod(p));
    }

    @Test
    void example7()  {
        var gx = Hex.parse("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798");
        var gy = Hex.parse("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8");
        var p = Int.parse(2).pow(Int.parse(256)).sub(Int.parse(2).pow(Int.parse(32))).sub(Int.parse(977));
        var n = Hex.parse("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141");
        var x = new FieldElement(gx, p);
        var y = new FieldElement(gy, p);
        var seven = new FieldElement(Int.parse(7), p);
        var zero = new FieldElement(Int.parse(0), p);
        var G = new Point(x, y, zero, seven);
        assertEquals("Point(infinity)", G.mul(n).toString());
    }

    @Test
    void example8() {
        assertEquals("S256Point(infinity)", S256Point.getG().mul(S256Point.N).toString());
    }

    @Test
    void secp256k1() {
        var gx = Hex.parse("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798");
        var gy = Hex.parse("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8");

        assertEquals(gy.pow(Int.parse(2)).mod(S256Field.P), gx.pow(Int.parse(3)).add(Int.parse(7)).mod(S256Field.P));

        var x = new FieldElement(gx, S256Field.P);
        var y = new FieldElement(gy, S256Field.P);
        var seven = new FieldElement(Int.parse(7), S256Field.P);
        var zero = new FieldElement(Int.parse(0), S256Field.P);
        var inf = new Point(null, null, zero, seven);
        var g = new Point(x, y, zero, seven);
        var ng = g.mul(S256Point.N);
        log.fine(ng.toString());
        assertTrue(ng.eq(inf));

        var s256inf = new S256Point(null, null);
        var s256ng = S256Point.getG().mul(S256Point.N);
        log.fine(s256ng.toString());
        assertTrue(s256ng.eq(s256inf));
    }

    @Test
    void example9VerifySig() {
        var z = Hex.parse("bc62d4b80d9e36da29c16c5d4d9f11731f36052c72401a76c23c0fb5a9b74423");
        var r = Hex.parse("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6");
        var s = Hex.parse("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec");
        var px = Hex.parse("04519fac3d910ca7e7138f7013706f619fa8f033e6ec6e09370ea38cee6a7574");
        var py = Hex.parse("82b51eab8c27c66e26c858a079bcdf4f1ada34cec420cafc7eac1a42216fb6c4");
        var p = new S256Point(new S256Field(px), new S256Field(py));
        var sInv = s.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N);
        var u = z.mul(sInv.mod(S256Point.N));
        var v = r.mul(sInv.mod(S256Point.N));
        assertTrue(((S256Field) S256Point.getG().mul(u).add(p.mul(v)).getX()).getNum().eq(r));
    }

    @Test
    void exercise6() {
        var px = Hex.parse("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c");
        var py = Hex.parse("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var p = new S256Point(new S256Field(px), new S256Field(py));

        var z = Hex.parse("ec208baa0fc1c19f708a9ca96fdeff3ac3f230bb4a7ba4aede4942ad003c0f60");
        var r = Hex.parse("ac8d1c87e51d0d441be8b3dd5b05c8795b48875dffe00b7ffcfac23010d3a395");
        var s = Hex.parse("68342ceff8935ededd102dd876ffd6ba72d6a427a3edb13d26eb0781cb423c4");
        var u = s.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N).mod(S256Point.N).mul(z);
        var v = s.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N).mod(S256Point.N).mul(r);
        var equals = ((S256Field) S256Point.getG().mul(u).add(p.mul(v)).getX()).getNum().eq(r);
        assertTrue(equals);

        z = Hex.parse("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        r = Hex.parse("eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c");
        s = Hex.parse("c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6");
        u = s.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N).mod(S256Point.N).mul(z);
        v = s.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N).mod(S256Point.N).mul(r);
        equals = ((S256Field) S256Point.getG().mul(u).add(p.mul(v)).getX()).getNum().eq(r);
        assertTrue(equals);
    }

    @Test
    void example10CreateSig() {
        var e = Hex.parse(Helper.hash256("my secret".getBytes()));
        var z = Hex.parse(Helper.hash256("my message".getBytes()));
        log.fine(String.format("z %s", z));
        assertEquals("0231c6f3d980a6b0fb7152f85cee7eb52bf92433d9919b9c5218cb08e79cce78", z.toString());

        var k = Int.parse(1234567890);
        var r = ((S256Field) S256Point.getG().mul(k).getX()).getNum();
        log.fine(String.format("r %s", r.toHex()));
        assertEquals("0x2b698a0f0a4041b77e63488ad48c23e8e8838dd1fb7520408b121697b782ef22", "0x" + r.toHex());

        var kInv = k.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N);
        var s = r.mul(e).add(z).mul(kInv).mod(S256Point.N);
        log.fine(String.format("s %s", s.toHex()));
        assertEquals("0xbb14e602ef9e3f872e25fad328466b34e6734b7a0fcd58b1eb635447ffae8cb9", "0x" + s.toHex());

        var point = S256Point.getG().mul(e);
        log.fine(String.format("%s", point));
        assertEquals("S256Point(0x028d003eab2e428d11983f3e97c3fa0addf3b42740df0d211795ffb3be2f6c52,0x0ae987b9ec6ea159c78cb2a937ed89096fb218d9e7594f02b547526d8cd309e2)", point.toString() );
        assertTrue(point.verify(z, new Signature(r.toHex(), s.toHex())));
    }

    @Test
    void exercise7() {
        var e = Int.parse(12345);
        var z = Hex.parse(Helper.hash256("Programming Bitcoin!".getBytes()));
        log.fine(String.format("z %s", z));
        assertEquals("969f6056aa26f7d2795fd013fe88868d09c9f6aed96965016e1936ae47060d48", z.toString());

        var k = Int.parse(1234567890);
        var r = ((S256Field) S256Point.getG().mul(k).getX()).getNum();
        log.fine(String.format("r %s", r.toHex()));
        assertEquals("0x2b698a0f0a4041b77e63488ad48c23e8e8838dd1fb7520408b121697b782ef22", "0x" + r.toHex());

        var kInv = k.powMod(S256Point.N.sub(Int.parse(2)), S256Point.N);
        var s = r.mul(e).add(z).mul(kInv).mod(S256Point.N);
        log.fine(String.format("s %s", s.toHex()));
        assertEquals("0x1dbc63bfef4416705e602a7b564161167076d8b20990a0f26f316cff2cb0bc1a", "0x" + s.toHex());

        var point = S256Point.getG().mul(e);
        log.fine(String.format("%s", point));
        assertEquals("S256Point(0xf01d6b9018ab421dd410404cb869072065522bf85734008f105cf385a023a80f,0x0eba29d0f0c5408ed681984dc525982abefccd9f7ff01dd26da4999cf3f6a295)", point.toString() );
        assertTrue(point.verify(z, new Signature(r, s)));
    }
}
