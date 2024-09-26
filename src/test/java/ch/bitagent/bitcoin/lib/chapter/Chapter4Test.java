package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.Signature;
import ch.bitagent.bitcoin.lib.helper.Base58;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Chapter4Test {

    private static final Logger log = Logger.getLogger(Chapter4Test.class.getSimpleName());

    @Test
    void exercise1() {
        var priv = new PrivateKey(Int.parse(5000));
        var hex = Hex.parse(priv.getPoint().sec(false));
        log.fine(String.format("%s", hex));
        assertEquals("04ffe558e388852f0120e46af2d1b370f85854a8eb0841811ece0e3e03d282d57c315dc72890a4f10a1481c031b03b351b0dc79901ca18a00cf009dbdb157a1d10", hex.toString());

        priv = new PrivateKey(Int.parse(2018).pow(Int.parse(5)));
        hex = Hex.parse(priv.getPoint().sec(false));
        log.fine(String.format("%s", hex));
        assertEquals("04027f3da1918455e03c46f659266a1bb5204e959db7364d2f473bdf8f0a13cc9dff87647fd023c13b4a4994f17691895806e1b40b57f4fd22581a4f46851f3b06", hex.toString());

        priv = new PrivateKey(Hex.parse("deadbeef12345"));
        hex = Hex.parse(priv.getPoint().sec(false));
        log.fine(String.format("%s", hex));
        assertEquals("04d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f842efa762fd59961d0e99803c61edba8b3e3f7dc3a341836f97733aebf987121", hex.toString());
    }

    @Test
    void exercise2() {
        var priv = new PrivateKey(Int.parse(5001));
        var hex = Hex.parse(priv.getPoint().sec(true));
        log.fine(String.format("%s", hex));
        assertEquals("0357a4f368868a8a6d572991e484e664810ff14c05c0fa023275251151fe0e53d1", hex.toString());

        priv = new PrivateKey(Int.parse(2019).pow(Int.parse(5)));
        hex = Hex.parse(priv.getPoint().sec(true));
        log.fine(String.format("%s", hex));
        assertEquals("02933ec2d2b111b92737ec12f1c5d20f3233a0ad21cd8b36d0bca7a0cfa5cb8701", hex.toString());

        priv = new PrivateKey(Hex.parse("deadbeef54321"));
        hex = Hex.parse(priv.getPoint().sec(true));
        log.fine(String.format("%s", hex));
        assertEquals("0296be5b1292f6c856b3c5654e886fc13511462059089cdf9c479623bfcbe77690", hex.toString());
    }

    @Test
    void exercise3() {
        var r = Hex.parse("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6");
        var s = Hex.parse("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec");
        var sig = new Signature(r, s);
        var hex = Hex.parse(sig.der());
        log.fine(String.format("%s", hex));
        assertEquals("3045022037206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c60221008ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec", hex.toString());
    }

    @Test
    void exercise4() {
        var h = Hex.parse("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        var b58 = Base58.encode(h.toBytes());
        log.fine(String.format("%s", b58));
        assertEquals("9MA8fRQrT4u8Zj8ZRd6MAiiyaxb2Y1CMpvVkHQu5hVM6", b58);

        h = Hex.parse("eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c");
        b58 = Base58.encode(h.toBytes());
        log.fine(String.format("%s", b58));
        assertEquals("4fE3H2E6XMp4SsxtwinF7w9a34ooUrwWe4WsW1458Pd", b58);

        h = Hex.parse("c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6");
        b58 = Base58.encode(h.toBytes());
        log.fine(String.format("%s", b58));
        assertEquals("EQJsjkd6JaGwxrjEhfeqPenqHwrBmPQZjJGNSCHBkcF7", b58);
    }

    @Test
    void exercise5() {
        var priv = new PrivateKey(Int.parse(5002));
        var address = priv.getPoint().address(false, true);
        log.fine(address);
        assertEquals("mmTPbXQFxboEtNRkwfh6K51jvdtHLxGeMA", address);

        priv = new PrivateKey(Int.parse(2020).pow(Int.parse(5)));
        address = priv.getPoint().address(true, true);
        log.fine(address);
        assertEquals("mopVkxp8UhXqRYbCYJsbeE1h1fiF64jcoH", address);

        priv = new PrivateKey(Hex.parse("12345deadbeef"));
        address = priv.getPoint().address(true, false);
        log.fine(address);
        assertEquals("1F1Pn2y6pDb68E5nYJJeba4TLg2U7B6KF1", address);
    }

    @Test
    void exercise6() {
        var priv = new PrivateKey(Int.parse(5003));
        var wif = priv.wif(true, true);
        log.fine(wif);
        assertEquals("cMahea7zqjxrtgAbB7LSGbcQUr1uX1ojuat9jZodMN8rFTv2sfUK", wif);

        priv = new PrivateKey(Int.parse(2021).pow(Int.parse(5)));
        wif = priv.wif(false, true);
        log.fine(wif);
        assertEquals("91avARGdfge8E4tZfYLoxeJ5sGBdNJQH4kvjpWAxgzczjbCwxic", wif);

        priv = new PrivateKey(Hex.parse("54321deadbeef"));
        wif = priv.wif(true, false);
        log.fine(wif);
        assertEquals("KwDiBf89QgGbjEhKnhXJuH7LrciVrZi3qYjgiuQJv1h8Ytr2S53a", wif);
    }
}
