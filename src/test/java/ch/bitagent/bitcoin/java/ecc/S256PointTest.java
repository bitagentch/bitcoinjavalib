package ch.bitagent.bitcoin.java.ecc;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class S256PointTest {

    private static final Logger log = Logger.getLogger(S256PointTest.class.getSimpleName());

    @Test
    void mul() {
        var s256inf = new S256Point(null, null);
        var s256ng = S256Point.getG().mul(S256Point.N);
        log.fine(s256ng.toString());
        assertTrue(s256ng.eq(s256inf));
        assertEquals("S256Point(infinity)", s256ng.toString());
    }

    @Test
    void order() {
        var point = S256Point.getG().mul(S256Point.N);
        assertNull(point.getX());
    }

    @Test
    void pubpoint() {
        var points = new Int[][]{
            {Int.parse(7), Hex.parse("5cbdf0646e5db4eaa398f365f2ea7a0e3d419b7e0330e39ce92bddedcac4f9bc"), Hex.parse("6aebca40ba255960a3178d6d861a54dba813d0b813fde7b5a5082628087264da")},
            {Int.parse(1485), Hex.parse("c982196a7466fbbbb0e27a940b6af926c1a74d5ad07128c82824a11b5398afda"), Hex.parse("7a91f9eae64438afb9ce6448a1c133db2d8fb9254e4546b6f001637d50901f55")},
            {Int.parse(2).pow(Int.parse(128)), Hex.parse("8f68b9d2f63b5f339239c1ad981f162ee88c5678723ea3351b7b444c9ec4c0da"), Hex.parse("662a9f2dba063986de1d90c2b6be215dbbea2cfe95510bfdf23cbf79501fff82")},
            {Int.parse(2).pow(Int.parse(240)).add(Int.parse(2).pow(Int.parse(31))), Hex.parse("9577ff57c8234558f293df502ca4f09cbc65a6572c842b39b366f21717945116"), Hex.parse("10b49c67fa9365ad7b90dab070be339a1daf9052373ec30ffae4f72d5e66d053")}
        };
        for (Int[] p : points) {
            var point = new S256Point(new S256Field(p[1]), new S256Field(p[2]));
            assertTrue(S256Point.getG().mul(p[0]).eq(point));
        }
    }

    @Test
    void verify() {
        var px = Hex.parse("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c");
        var py = Hex.parse("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var p = new S256Point(new S256Field(px), new S256Field(py));

        var z = Hex.parse("ec208baa0fc1c19f708a9ca96fdeff3ac3f230bb4a7ba4aede4942ad003c0f60");
        var r = Hex.parse("ac8d1c87e51d0d441be8b3dd5b05c8795b48875dffe00b7ffcfac23010d3a395");
        var s = Hex.parse("68342ceff8935ededd102dd876ffd6ba72d6a427a3edb13d26eb0781cb423c4");
        assertTrue(p.verify(z, new Signature(r, s)));

        z = Hex.parse("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        r = Hex.parse("eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c");
        s = Hex.parse("c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6");
        assertTrue(p.verify(z, new Signature(r, s)));
    }

    @Test
    void sec() {
        var coefficient = Int.parse(999).pow(Int.parse(3));
        var uncompressed = Hex.parse("049d5ca49670cbe4c3bfa84c96a8c87df086c6ea6a24ba6b809c9de234496808d56fa15cc7f3d38cda98dee2419f415b7513dde1301f8643cd9245aea7f3f911f9");
        var compressed = Hex.parse("039d5ca49670cbe4c3bfa84c96a8c87df086c6ea6a24ba6b809c9de234496808d5");
        var point = S256Point.getG().mul(coefficient);
        assertArrayEquals(uncompressed.toBytes(), point.sec(false));
        assertArrayEquals(compressed.toBytes(), point.sec(true));

        coefficient = Int.parse(123);
        uncompressed = Hex.parse("04a598a8030da6d86c6bc7f2f5144ea549d28211ea58faa70ebf4c1e665c1fe9b5204b5d6f84822c307e4b4a7140737aec23fc63b65b35f86a10026dbd2d864e6b");
        compressed = Hex.parse("03a598a8030da6d86c6bc7f2f5144ea549d28211ea58faa70ebf4c1e665c1fe9b5");
        point = S256Point.getG().mul(coefficient);
        assertArrayEquals(uncompressed.toBytes(), point.sec(false));
        assertArrayEquals(compressed.toBytes(), point.sec(true));

        coefficient = Int.parse(42424242);
        uncompressed = Hex.parse("04aee2e7d843f7430097859e2bc603abcc3274ff8169c1a469fee0f20614066f8e21ec53f40efac47ac1c5211b2123527e0e9b57ede790c4da1e72c91fb7da54a3");
        compressed = Hex.parse("03aee2e7d843f7430097859e2bc603abcc3274ff8169c1a469fee0f20614066f8e");
        point = S256Point.getG().mul(coefficient);
        assertArrayEquals(uncompressed.toBytes(), point.sec(false));
        assertArrayEquals(compressed.toBytes(), point.sec(true));
    }

    @Test
    void address() {
        var secret = Int.parse(888).pow(Int.parse(3));
        var point = S256Point.getG().mul(secret);
        var mainnetAddress = "148dY81A9BmdpMhvYEVznrM45kWN32vSCN";
        var testnetAddress = "mieaqB68xDCtbUBYFoUNcmZNwk74xcBfTP";
        assertEquals(mainnetAddress, point.address(true, false));
        assertEquals(testnetAddress, point.address(true, true));

        secret = Int.parse(321);
        point = S256Point.getG().mul(secret);
        mainnetAddress = "1S6g2xBJSED7Qr9CYZib5f4PYVhHZiVfj";
        testnetAddress = "mfx3y63A7TfTtXKkv7Y6QzsPFY6QCBCXiP";
        assertEquals(mainnetAddress, point.address(false, false));
        assertEquals(testnetAddress, point.address(false, true));

        secret = Int.parse("4242424242");
        point = S256Point.getG().mul(secret);
        mainnetAddress = "1226JSptcStqn4Yq9aAmNXdwdc2ixuH9nb";
        testnetAddress = "mgY3bVusRUL6ZB2Ss999CSrGVbdRwVpM8s";
        assertEquals(mainnetAddress, point.address(false, false));
        assertEquals(testnetAddress, point.address(false, true));
    }
}