package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtendedKeyTest {

    @Test
    void bip32_vector1_m() {
        var extendedPrivkey = ExtendedKey.parse("xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(0, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupje8YtGqsefD265TMg7usUDFdp6W1EGMcet8");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(0, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip32_vector1_m_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi");
        var k2 = k1.derive(0, true, false);
        var k3 = ExtendedKey.parse("xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi");
        var k2 = k1.derive(0, true, true);
        var k3 = ExtendedKey.parse("xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(1, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(1, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip32_vector1_m_0h_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7");
        var k2 = k1.derive(1);
        var k3 = ExtendedKey.parse("xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw");
        var k2 = k1.derive(1);
        var k3 = ExtendedKey.parse("xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1() {
        var extendedPrivkey = ExtendedKey.parse("xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(2, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(2, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip32_vector1_m_0h_1_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs");
        var k2 = k1.derive(2, true, false);
        var k3 = ExtendedKey.parse("xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs");
        var k2 = k1.derive(2, true, true);
        var k3 = ExtendedKey.parse("xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_2h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(3, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(3, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip32_vector1_m_0h_1_2h_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM");
        var k2 = k1.derive(2);
        var k3 = ExtendedKey.parse("xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8RfQMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_2h_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5");
        var k2 = k1.derive(2);
        var k3 = ExtendedKey.parse("xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_2h_2() {
        var extendedPrivkey = ExtendedKey.parse("xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8RfQMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(4, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(4, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip32_vector1_m_0h_1_2h_2_derive_privkey() {
        var k1 = ExtendedKey.parse("xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8RfQMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334");
        var k2 = k1.derive(1000000000);
        var k3 = ExtendedKey.parse("xprvA41z7zogVVwxVSgdKUHDy1SKmdb533PjDz7J6N6mV6uS3ze1ai8FHa8kmHScGpWmj4WggLyQjgPie1rFSruoUihUZREPSL39UNdE3BBDu76");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_2h_2_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV");
        var k2 = k1.derive(1000000000);
        var k3 = ExtendedKey.parse("xpub6H1LXWLaKsWFhvm6RVpEL9P4KfRZSW7abD2ttkWP3SSQvnyA8FSVqNTEcYFgJS2UaFcxupHiYkro49S8yGasTvXEYBVPamhGW6cFJodrTHy");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector1_m_0h_1_2h_2_1000000000() {
        var extendedPrivkey = ExtendedKey.parse("xprvA41z7zogVVwxVSgdKUHDy1SKmdb533PjDz7J6N6mV6uS3ze1ai8FHa8kmHScGpWmj4WggLyQjgPie1rFSruoUihUZREPSL39UNdE3BBDu76");
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(5, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("xpub6H1LXWLaKsWFhvm6RVpEL9P4KfRZSW7abD2ttkWP3SSQvnyA8FSVqNTEcYFgJS2UaFcxupHiYkro49S8yGasTvXEYBVPamhGW6cFJodrTHy");
        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(5, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip84_m() {
        var extendedPrivkey = ExtendedKey.parse("zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wixPox7zhZLfiUm8aunE96BBa4Kei5");
        assertArrayEquals(ExtendedKey.PREFIX_ZPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(0, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("zpub6jftahH18ngZxLmXaKw3GSZzZsszmt9WqedkyZdezFtWRFBZqsQH5hyUmb4pCEeZGmVfQuP5bedXTB8is6fTv19U1GQRyQUKQGUTzyHACMF");
        assertArrayEquals(ExtendedKey.PREFIX_ZPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(0, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip84_m_derive_privkey() {
        var k1 = ExtendedKey.parse("zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wixPox7zhZLfiUm8aunE96BBa4Kei5");
        var k2 = k1.derive(84, true, false)
                .derive(0, true, false)
                .derive(0, true, false);
        var k3 = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip84_m_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wixPox7zhZLfiUm8aunE96BBa4Kei5");
        var k2 = k1.derive(84, true, false)
                .derive(0, true, false)
                .derive(0, true, true);
        var k3 = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip84_m_84h_0h_0h() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        assertArrayEquals(ExtendedKey.PREFIX_ZPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(3, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        assertArrayEquals(ExtendedKey.PREFIX_ZPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(3, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    @Test
    void bip84_m_84h_0h_0h_derive_privkey() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        assertEquals(3, extendedPrivkey.getDepth());

        var extendedPrivkey0 = extendedPrivkey.derive(0);
        assertEquals(4, extendedPrivkey0.getDepth());

        var extendedPrivkey00 = extendedPrivkey0.derive(0);
        assertEquals(5, extendedPrivkey00.getDepth());
        var wif00 = PrivateKey.parse(extendedPrivkey00.getKey()).wif(true, false);
        assertEquals("KyZpNDKnfs94vbrwhJneDi77V6jF64PWPF8x5cdJb8ifgg2DUc9d", wif00);

        var extendedPrivkey01 = extendedPrivkey0.derive(1);
        assertEquals(5, extendedPrivkey01.getDepth());
        var wif01 = PrivateKey.parse(extendedPrivkey01.getKey()).wif(true, false);
        assertEquals("Kxpf5b8p3qX56DKEe5NqWbNUP9MnqoRFzZwHRtsFqhzuvUJsYZCy", wif01);

        var extendedPrivkey10 = extendedPrivkey.derive(1).derive(0);
        assertEquals(5, extendedPrivkey10.getDepth());
        var wif10 = PrivateKey.parse(extendedPrivkey10.getKey()).wif(true, false);
        assertEquals("KxuoxufJL5csa1Wieb2kp29VNdn92Us8CoaUG3aGtPtcF3AzeXvF", wif10);
    }

    @Test
    void bip84_m_84h_0h_0h_derive_pubkey() {
        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        assertEquals(3, extendedPubkey.getDepth());

        var extendedPubkey0 = extendedPubkey.derive(0);
        assertEquals(4, extendedPubkey0.getDepth());

        var extendedPubkey00 = extendedPubkey0.derive(0);
        assertEquals(5, extendedPubkey00.getDepth());
        var pubkey00 = S256Point.parse(Hex.parse("0330d54fd0dd420a6e5f8d3624f5f3482cae350f79d5f0753bf5beef9c2d91af3c").toBytes());
        assertArrayEquals(pubkey00.sec(true), extendedPubkey00.getKey());

        var extendedPubkey01 = extendedPubkey0.derive(1);
        assertEquals(5, extendedPubkey00.getDepth());
        var pubkey01 = S256Point.parse(Hex.parse("03e775fd51f0dfb8cd865d9ff1cca2a158cf651fe997fdc9fee9c1d3b5e995ea77").toBytes());
        assertArrayEquals(pubkey01.sec(true), extendedPubkey01.getKey());

        var extendedPubkey10 = extendedPubkey.derive(1).derive(0);
        assertEquals(5, extendedPubkey10.getDepth());
        var pubkey10 = S256Point.parse(Hex.parse("03025324888e429ab8e3dbaf1f7802648b9cd01e9b418485c5fa4c1b9b5700e1a6").toBytes());
        assertArrayEquals(pubkey10.sec(true), extendedPubkey10.getKey());
    }

    @Test
    void bip84_m_84h_0h_0h_0_0() {
        var privkey = PrivateKey.parseWif("KyZpNDKnfs94vbrwhJneDi77V6jF64PWPF8x5cdJb8ifgg2DUc9d", true, false);
        var pubkey = S256Point.parse(Hex.parse("0330d54fd0dd420a6e5f8d3624f5f3482cae350f79d5f0753bf5beef9c2d91af3c").toBytes());
        assertTrue(pubkey.eq(privkey.getPoint()));
        var address = Address.parse("bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu");
        assertArrayEquals(address.hash160(), pubkey.hash160(true));
        assertEquals(address.address(), pubkey.addressBech32P2wpkh(false));
    }

    @Test
    void bip84_m_84h_0h_0h_0_1() {
        var privkey = PrivateKey.parseWif("Kxpf5b8p3qX56DKEe5NqWbNUP9MnqoRFzZwHRtsFqhzuvUJsYZCy", true, false);
        var pubkey = S256Point.parse(Hex.parse("03e775fd51f0dfb8cd865d9ff1cca2a158cf651fe997fdc9fee9c1d3b5e995ea77").toBytes());
        assertTrue(pubkey.eq(privkey.getPoint()));
        var address = Address.parse("bc1qnjg0jd8228aq7egyzacy8cys3knf9xvrerkf9g");
        assertArrayEquals(address.hash160(), pubkey.hash160(true));
        assertEquals(address.address(), pubkey.addressBech32P2wpkh(false));
    }

    @Test
    void bip84_m_84h_0h_0h_1_0() {
        var privkey = PrivateKey.parseWif("KxuoxufJL5csa1Wieb2kp29VNdn92Us8CoaUG3aGtPtcF3AzeXvF", true, false);
        var pubkey = S256Point.parse(Hex.parse("03025324888e429ab8e3dbaf1f7802648b9cd01e9b418485c5fa4c1b9b5700e1a6").toBytes());
        assertTrue(pubkey.eq(privkey.getPoint()));
        var address = Address.parse("bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el");
        assertArrayEquals(address.hash160(), pubkey.hash160(true));
        assertEquals(address.address(), pubkey.addressBech32P2wpkh(false));
    }

    private static void compareExtendedKeys(ExtendedKey k3, ExtendedKey k2) {
        assertArrayEquals(k3.getPrefix(), k2.getPrefix());
        assertEquals(k3.getDepth(), k2.getDepth());
        assertArrayEquals(k3.getFingerprint(), k2.getFingerprint());
        assertArrayEquals(k3.getChildNumber(), k2.getChildNumber());
        assertArrayEquals(k3.getChainCode(), k2.getChainCode());
        assertArrayEquals(k3.getKey(), k2.getKey());
    }
}