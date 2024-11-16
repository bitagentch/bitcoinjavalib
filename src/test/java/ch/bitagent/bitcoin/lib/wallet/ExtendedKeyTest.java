package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtendedKeyTest {

    @Test
    void bip32_vector1_m() {
        var seed = Hex.parse("000102030405060708090a0b0c0d0e0f").toBytes();
        var xprv = "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi";
        var xpub = "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupje8YtGqsefD265TMg7usUDFdp6W1EGMcet8";

        assertEquals(xprv, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPRV));
        assertEquals(xpub, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPUB));

        var extendedPrivkey = ExtendedKey.parse(xprv);
        var extendedPubkey = ExtendedKey.parse(xpub);
        compareXKeys(extendedPrivkey, extendedPubkey, 0);
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
        var extendedPubkey = ExtendedKey.parse("xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw");
        compareXKeys(extendedPrivkey, extendedPubkey, 1);
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
        var extendedPubkey = ExtendedKey.parse("xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ");
        compareXKeys(extendedPrivkey, extendedPubkey, 2);
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
        var extendedPubkey = ExtendedKey.parse("xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5");
        compareXKeys(extendedPrivkey, extendedPubkey, 3);
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
        var extendedPubkey = ExtendedKey.parse("xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV");
        compareXKeys(extendedPrivkey, extendedPubkey, 4);
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
        var extendedPubkey = ExtendedKey.parse("xpub6H1LXWLaKsWFhvm6RVpEL9P4KfRZSW7abD2ttkWP3SSQvnyA8FSVqNTEcYFgJS2UaFcxupHiYkro49S8yGasTvXEYBVPamhGW6cFJodrTHy");
        compareXKeys(extendedPrivkey, extendedPubkey, 5);
    }

    @Test
    void bip32_vector2_m() {
        var seed = Hex.parse("fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542").toBytes();
        var xprv = "xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U";
        var xpub = "xpub661MyMwAqRbcFW31YEwpkMuc5THy2PSt5bDMsktWQcFF8syAmRUapSCGu8ED9W6oDMSgv6Zz8idoc4a6mr8BDzTJY47LJhkJ8UB7WEGuduB";

        assertEquals(xprv, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPRV));
        assertEquals(xpub, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPUB));

        var extendedPrivkey = ExtendedKey.parse(xprv);
        var extendedPubkey = ExtendedKey.parse(xpub);
        compareXKeys(extendedPrivkey, extendedPubkey, 0);
    }

    @Test
    void bip32_vector2_m_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U");
        var k2 = k1.derive(0);
        var k3 = ExtendedKey.parse("xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub661MyMwAqRbcFW31YEwpkMuc5THy2PSt5bDMsktWQcFF8syAmRUapSCGu8ED9W6oDMSgv6Zz8idoc4a6mr8BDzTJY47LJhkJ8UB7WEGuduB");
        var k2 = k1.derive(0);
        var k3 = ExtendedKey.parse("xpub69H7F5d8KSRgmmdJg2KhpAK8SR3DjMwAdkxj3ZuxV27CprR9LgpeyGmXUbC6wb7ERfvrnKZjXoUmmDznezpbZb7ap6r1D3tgFxHmwMkQTPH");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0() {
        var extendedPrivkey = ExtendedKey.parse("xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt");
        var extendedPubkey = ExtendedKey.parse("xpub69H7F5d8KSRgmmdJg2KhpAK8SR3DjMwAdkxj3ZuxV27CprR9LgpeyGmXUbC6wb7ERfvrnKZjXoUmmDznezpbZb7ap6r1D3tgFxHmwMkQTPH");
        compareXKeys(extendedPrivkey, extendedPubkey, 1);
    }

    @Test
    void bip32_vector2_m_0_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt");
        var k2 = k1.derive(2147483647, true, false);
        var k3 = ExtendedKey.parse("xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt");
        var k2 = k1.derive(2147483647, true, true);
        var k3 = ExtendedKey.parse("xpub6ASAVgeehLbnwdqV6UKMHVzgqAG8Gr6riv3Fxxpj8ksbH9ebxaEyBLZ85ySDhKiLDBrQSARLq1uNRts8RuJiHjaDMBU4Zn9h8LZNnBC5y4a");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9");
        var extendedPubkey = ExtendedKey.parse("xpub6ASAVgeehLbnwdqV6UKMHVzgqAG8Gr6riv3Fxxpj8ksbH9ebxaEyBLZ85ySDhKiLDBrQSARLq1uNRts8RuJiHjaDMBU4Zn9h8LZNnBC5y4a");
        compareXKeys(extendedPrivkey, extendedPubkey, 2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9");
        var k2 = k1.derive(1);
        var k3 = ExtendedKey.parse("xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub6ASAVgeehLbnwdqV6UKMHVzgqAG8Gr6riv3Fxxpj8ksbH9ebxaEyBLZ85ySDhKiLDBrQSARLq1uNRts8RuJiHjaDMBU4Zn9h8LZNnBC5y4a");
        var k2 = k1.derive(1);
        var k3 = ExtendedKey.parse("xpub6DF8uhdarytz3FWdA8TvFSvvAh8dP3283MY7p2V4SeE2wyWmG5mg5EwVvmdMVCQcoNJxGoWaU9DCWh89LojfZ537wTfunKau47EL2dhHKon");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1() {
        var extendedPrivkey = ExtendedKey.parse("xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef");
        var extendedPubkey = ExtendedKey.parse("xpub6DF8uhdarytz3FWdA8TvFSvvAh8dP3283MY7p2V4SeE2wyWmG5mg5EwVvmdMVCQcoNJxGoWaU9DCWh89LojfZ537wTfunKau47EL2dhHKon");
        compareXKeys(extendedPrivkey, extendedPubkey, 3);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef");
        var k2 = k1.derive(2147483646, true, false);
        var k3 = ExtendedKey.parse("xprvA1RpRA33e1JQ7ifknakTFpgNXPmW2YvmhqLQYMmrj4xJXXWYpDPS3xz7iAxn8L39njGVyuoseXzU6rcxFLJ8HFsTjSyQbLYnMpCqE2VbFWc");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef");
        var k2 = k1.derive(2147483646, true, true);
        var k3 = ExtendedKey.parse("xpub6ERApfZwUNrhLCkDtcHTcxd75RbzS1ed54G1LkBUHQVHQKqhMkhgbmJbZRkrgZw4koxb5JaHWkY4ALHY2grBGRjaDMzQLcgJvLJuZZvRcEL");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_2147483646h() {
        var extendedPrivkey = ExtendedKey.parse("xprvA1RpRA33e1JQ7ifknakTFpgNXPmW2YvmhqLQYMmrj4xJXXWYpDPS3xz7iAxn8L39njGVyuoseXzU6rcxFLJ8HFsTjSyQbLYnMpCqE2VbFWc");
        var extendedPubkey = ExtendedKey.parse("xpub6ERApfZwUNrhLCkDtcHTcxd75RbzS1ed54G1LkBUHQVHQKqhMkhgbmJbZRkrgZw4koxb5JaHWkY4ALHY2grBGRjaDMzQLcgJvLJuZZvRcEL");
        compareXKeys(extendedPrivkey, extendedPubkey, 4);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_2147483646h_derive_privkey() {
        var k1 = ExtendedKey.parse("xprvA1RpRA33e1JQ7ifknakTFpgNXPmW2YvmhqLQYMmrj4xJXXWYpDPS3xz7iAxn8L39njGVyuoseXzU6rcxFLJ8HFsTjSyQbLYnMpCqE2VbFWc");
        var k2 = k1.derive(2);
        var k3 = ExtendedKey.parse("xprvA2nrNbFZABcdryreWet9Ea4LvTJcGsqrMzxHx98MMrotbir7yrKCEXw7nadnHM8Dq38EGfSh6dqA9QWTyefMLEcBYJUuekgW4BYPJcr9E7j");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_2147483646h_derive_pubkey() {
        var k1 = ExtendedKey.parse("xpub6ERApfZwUNrhLCkDtcHTcxd75RbzS1ed54G1LkBUHQVHQKqhMkhgbmJbZRkrgZw4koxb5JaHWkY4ALHY2grBGRjaDMzQLcgJvLJuZZvRcEL");
        var k2 = k1.derive(2);
        var k3 = ExtendedKey.parse("xpub6FnCn6nSzZAw5Tw7cgR9bi15UV96gLZhjDstkXXxvCLsUXBGXPdSnLFbdpq8p9HmGsApME5hQTZ3emM2rnY5agb9rXpVGyy3bdW6EEgAtqt");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector2_m_0_2147483647h_1_2147483646h_2() {
        var extendedPrivkey = ExtendedKey.parse("xprvA2nrNbFZABcdryreWet9Ea4LvTJcGsqrMzxHx98MMrotbir7yrKCEXw7nadnHM8Dq38EGfSh6dqA9QWTyefMLEcBYJUuekgW4BYPJcr9E7j");
        var extendedPubkey = ExtendedKey.parse("xpub6FnCn6nSzZAw5Tw7cgR9bi15UV96gLZhjDstkXXxvCLsUXBGXPdSnLFbdpq8p9HmGsApME5hQTZ3emM2rnY5agb9rXpVGyy3bdW6EEgAtqt");
        compareXKeys(extendedPrivkey, extendedPubkey, 5);
    }

    @Test
    void bip32_vector3_m() {
        var seed = Hex.parse("4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be").toBytes();
        var xprv = "xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6";
        var xpub = "xpub661MyMwAqRbcEZVB4dScxMAdx6d4nFc9nvyvH3v4gJL378CSRZiYmhRoP7mBy6gSPSCYk6SzXPTf3ND1cZAceL7SfJ1Z3GC8vBgp2epUt13";

        assertEquals(xprv, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPRV));
        assertEquals(xpub, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPUB));

        var extendedPrivkey = ExtendedKey.parse(xprv);
        var extendedPubkey = ExtendedKey.parse(xpub);
        compareXKeys(extendedPrivkey, extendedPubkey, 0);
    }

    @Test
    void bip32_vector3_m_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6");
        var k2 = k1.derive(0, true, false);
        var k3 = ExtendedKey.parse("xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7AANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector3_m_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6");
        var k2 = k1.derive(0, true, true);
        var k3 = ExtendedKey.parse("xpub68NZiKmJWnxxS6aaHmn81bvJeTESw724CRDs6HbuccFQN9Ku14VQrADWgqbhhTHBaohPX4CjNLf9fq9MYo6oDaPPLPxSb7gwQN3ih19Zm4Y");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector3_m_0h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7AANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L");
        var extendedPubkey = ExtendedKey.parse("xpub68NZiKmJWnxxS6aaHmn81bvJeTESw724CRDs6HbuccFQN9Ku14VQrADWgqbhhTHBaohPX4CjNLf9fq9MYo6oDaPPLPxSb7gwQN3ih19Zm4Y");
        compareXKeys(extendedPrivkey, extendedPubkey, 1);
    }

    @Test
    void bip32_vector4_m() {
        var seed = Hex.parse("3ddd5602285899a946114506157c7997e5444528f3003f6134712147db19b678").toBytes();
        var xprv = "xprv9s21ZrQH143K48vGoLGRPxgo2JNkJ3J3fqkirQC2zVdk5Dgd5w14S7fRDyHH4dWNHUgkvsvNDCkvAwcSHNAQwhwgNMgZhLtQC63zxwhQmRv";
        var xpub = "xpub661MyMwAqRbcGczjuMoRm6dXaLDEhW1u34gKenbeYqAix21mdUKJyuyu5F1rzYGVxyL6tmgBUAEPrEz92mBXjByMRiJdba9wpnN37RLLAXa";

        assertEquals(xprv, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPRV));
        assertEquals(xpub, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPUB));

        var extendedPrivkey = ExtendedKey.parse(xprv);
        var extendedPubkey = ExtendedKey.parse(xpub);
        compareXKeys(extendedPrivkey, extendedPubkey, 0);
    }

    @Test
    void bip32_vector4_m_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K48vGoLGRPxgo2JNkJ3J3fqkirQC2zVdk5Dgd5w14S7fRDyHH4dWNHUgkvsvNDCkvAwcSHNAQwhwgNMgZhLtQC63zxwhQmRv");
        var k2 = k1.derive(0, true, false);
        var k3 = ExtendedKey.parse("xprv9vB7xEWwNp9kh1wQRfCCQMnZUEG21LpbR9NPCNN1dwhiZkjjeGRnaALmPXCX7SgjFTiCTT6bXes17boXtjq3xLpcDjzEuGLQBM5ohqkao9G");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector4_m_derive_pubkey() {
        var k1 = ExtendedKey.parse("xprv9s21ZrQH143K48vGoLGRPxgo2JNkJ3J3fqkirQC2zVdk5Dgd5w14S7fRDyHH4dWNHUgkvsvNDCkvAwcSHNAQwhwgNMgZhLtQC63zxwhQmRv");
        var k2 = k1.derive(0, true, true);
        var k3 = ExtendedKey.parse("xpub69AUMk3qDBi3uW1sXgjCmVjJ2G6WQoYSnNHyzkmdCHEhSZ4tBok37xfFEqHd2AddP56Tqp4o56AePAgCjYdvpW2PU2jbUPFKsav5ut6Ch1m");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector4_m_0h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9vB7xEWwNp9kh1wQRfCCQMnZUEG21LpbR9NPCNN1dwhiZkjjeGRnaALmPXCX7SgjFTiCTT6bXes17boXtjq3xLpcDjzEuGLQBM5ohqkao9G");
        var extendedPubkey = ExtendedKey.parse("xpub69AUMk3qDBi3uW1sXgjCmVjJ2G6WQoYSnNHyzkmdCHEhSZ4tBok37xfFEqHd2AddP56Tqp4o56AePAgCjYdvpW2PU2jbUPFKsav5ut6Ch1m");
        compareXKeys(extendedPrivkey, extendedPubkey, 1);
    }

    @Test
    void bip32_vector4_m_0h_derive_privkey() {
        var k1 = ExtendedKey.parse("xprv9vB7xEWwNp9kh1wQRfCCQMnZUEG21LpbR9NPCNN1dwhiZkjjeGRnaALmPXCX7SgjFTiCTT6bXes17boXtjq3xLpcDjzEuGLQBM5ohqkao9G");
        var k2 = k1.derive(1, true, false);
        var k3 = ExtendedKey.parse("xprv9xJocDuwtYCMNAo3Zw76WENQeAS6WGXQ55RCy7tDJ8oALr4FWkuVoHJeHVAcAqiZLE7Je3vZJHxspZdFHfnBEjHqU5hG1Jaj32dVoS6XLT1");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector4_m_0h_derive_privkey_neutral() {
        var k1 = ExtendedKey.parse("xprv9vB7xEWwNp9kh1wQRfCCQMnZUEG21LpbR9NPCNN1dwhiZkjjeGRnaALmPXCX7SgjFTiCTT6bXes17boXtjq3xLpcDjzEuGLQBM5ohqkao9G");
        var k2 = k1.derive(1, true, true);
        var k3 = ExtendedKey.parse("xpub6BJA1jSqiukeaesWfxe6sNK9CCGaujFFSJLomWHprUL9DePQ4JDkM5d88n49sMGJxrhpjazuXYWdMf17C9T5XnxkopaeS7jGk1GyyVziaMt");
        compareExtendedKeys(k3, k2);
    }

    @Test
    void bip32_vector4_m_0h_1h() {
        var extendedPrivkey = ExtendedKey.parse("xprv9xJocDuwtYCMNAo3Zw76WENQeAS6WGXQ55RCy7tDJ8oALr4FWkuVoHJeHVAcAqiZLE7Je3vZJHxspZdFHfnBEjHqU5hG1Jaj32dVoS6XLT1");
        var extendedPubkey = ExtendedKey.parse("xpub6BJA1jSqiukeaesWfxe6sNK9CCGaujFFSJLomWHprUL9DePQ4JDkM5d88n49sMGJxrhpjazuXYWdMf17C9T5XnxkopaeS7jGk1GyyVziaMt");
        compareXKeys(extendedPrivkey, extendedPubkey, 2);
    }

    @Test
    void bip32_vector5() {
        // pubkey version / prvkey mismatch
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661MyMwAqRbcEYS8w7XLSVeEsBXy79zSzH1J8vCdxAZningWLdN3zgtU6LBpB85b3D2yc8sfvZU521AAwdZafEz7mnzBBsz4wKY5fTtTQBm"));
        // prvkey version / pubkey mismatch
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K24Mfq5zL5MhWK9hUhhGbd45hLXo2Pq2oqzMMo63oStZzFGTQQD3dC4H2D5GBj7vWvSQaaBv5cxi9gafk7NF3pnBju6dwKvH"));
        // invalid pubkey prefix 04
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661MyMwAqRbcEYS8w7XLSVeEsBXy79zSzH1J8vCdxAZningWLdN3zgtU6Txnt3siSujt9RCVYsx4qHZGc62TG4McvMGcAUjeuwZdduYEvFn"));
        // invalid prvkey prefix 04
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K24Mfq5zL5MhWK9hUhhGbd45hLXo2Pq2oqzMMo63oStZzFGpWnsj83BHtEy5Zt8CcDr1UiRXuWCmTQLxEK9vbz5gPstX92JQ"));
        // invalid pubkey prefix 01
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661MyMwAqRbcEYS8w7XLSVeEsBXy79zSzH1J8vCdxAZningWLdN3zgtU6N8ZMMXctdiCjxTNq964yKkwrkBJJwpzZS4HS2fxvyYUA4q2Xe4"));
        // invalid prvkey prefix 01
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K24Mfq5zL5MhWK9hUhhGbd45hLXo2Pq2oqzMMo63oStZzFAzHGBP2UuGCqWLTAPLcMtD9y5gkZ6Eq3Rjuahrv17fEQ3Qen6J"));
        // zero depth with non-zero parent fingerprint
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s2SPatNQ9Vc6GTbVMFPFo7jsaZySyzk7L8n2uqKXJen3KUmvQNTuLh3fhZMBoG3G4ZW1N2kZuHEPY53qmbZzCHshoQnNf4GvELZfqTUrcv"));
        // zero depth with non-zero parent fingerprint
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661no6RGEX3uJkY4bNnPcw4URcQTrSibUZ4NqJEw5eBkv7ovTwgiT91XX27VbEXGENhYRCf7hyEbWrR3FewATdCEebj6znwMfQkhRYHRLpJ"));
        // zero depth with non-zero index
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH4r4TsiLvyLXqM9P7k1K3EYhA1kkD6xuquB5i39AU8KF42acDyL3qsDbU9NmZn6MsGSUYZEsuoePmjzsB3eFKSUEh3Gu1N3cqVUN"));
        // zero depth with non-zero index
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661MyMwAuDcm6CRQ5N4qiHKrJ39Xe1R1NyfouMKTTWcguwVcfrZJaNvhpebzGerh7gucBvzEQWRugZDuDXjNDRmXzSZe4c7mnTK97pTvGS8"));
        // unknown extended key version
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("DMwo58pR1QLEFihHiXPVykYB6fJmsTeHvyTp7hRThAtCX8CvYzgPcn8XnmdfHGMQzT7ayAmfo4z3gY5KfbrZWZ6St24UVf2Qgo6oujFktLHdHY4"));
        // unknown extended key version
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("DMwo58pR1QLEFihHiXPVykYB6fJmsTeHvyTp7hRThAtCX8CvYzgPcn8XnmdfHPmHJiEDXkTiJTVV9rHEBUem2mwVbbNfvT2MTcAqj3nesx8uBf9"));
        // private key 0 not in 1..n-1
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K24Mfq5zL5MhWK9hUhhGbd45hLXo2Pq2oqzMMo63oStZzF93Y5wvzdUayhgkkFoicQZcP3y52uPPxFnfoLZB21Teqt1VvEHx"));
        // private key n not in 1..n-1
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K24Mfq5zL5MhWK9hUhhGbd45hLXo2Pq2oqzMMo63oStZzFAzHGBP2UuGCqWLTAPLcMtD5SDKr24z3aiUvKr9bJpdrcLg1y3G"));
        // invalid pubkey 020000000000000000000000000000000000000000000000000000000000000007
        assertThrowsExactly(IllegalArgumentException.class, () -> ExtendedKey.parse("xpub661MyMwAqRbcEYS8w7XLSVeEsBXy79zSzH1J8vCdxAZningWLdN3zgtU6Q5JXayek4PRsn35jii4veMimro1xefsM58PgBMrvdYre8QyULY"));
        // invalid checksum
        assertThrowsExactly(IllegalStateException.class, () -> ExtendedKey.parse("xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHL"));
    }

    @Test
    void bip84_m() {
        var mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        var zprv = "zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wixPox7zhZLfiUm8aunE96BBa4Kei5";
        var zpub = "zpub6jftahH18ngZxLmXaKw3GSZzZsszmt9WqedkyZdezFtWRFBZqsQH5hyUmb4pCEeZGmVfQuP5bedXTB8is6fTv19U1GQRyQUKQGUTzyHACMF";

        var seed = MnemonicSentence.mnemonicToSeed(mnemonic, null);
        assertEquals(zprv, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_ZPRV));
        assertEquals(zpub, MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_ZPUB));

        var extendedPrivkey = ExtendedKey.parse(zprv);
        var extendedPubkey = ExtendedKey.parse(zpub);
        compareZKeys(extendedPrivkey, extendedPubkey, 0);
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
        var extendedPubkey = ExtendedKey.parse("zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs");
        compareZKeys(extendedPrivkey, extendedPubkey, 3);
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
        var address = Address.parse("bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu");
        compareAddress(privkey, pubkey, address);
    }

    @Test
    void bip84_m_84h_0h_0h_0_1() {
        var privkey = PrivateKey.parseWif("Kxpf5b8p3qX56DKEe5NqWbNUP9MnqoRFzZwHRtsFqhzuvUJsYZCy", true, false);
        var pubkey = S256Point.parse(Hex.parse("03e775fd51f0dfb8cd865d9ff1cca2a158cf651fe997fdc9fee9c1d3b5e995ea77").toBytes());
        var address = Address.parse("bc1qnjg0jd8228aq7egyzacy8cys3knf9xvrerkf9g");
        compareAddress(privkey, pubkey, address);
    }

    @Test
    void bip84_m_84h_0h_0h_1_0() {
        var privkey = PrivateKey.parseWif("KxuoxufJL5csa1Wieb2kp29VNdn92Us8CoaUG3aGtPtcF3AzeXvF", true, false);
        var pubkey = S256Point.parse(Hex.parse("03025324888e429ab8e3dbaf1f7802648b9cd01e9b418485c5fa4c1b9b5700e1a6").toBytes());
        var address = Address.parse("bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el");
        compareAddress(privkey, pubkey, address);
    }

    private static void compareXKeys(ExtendedKey extendedPrivkey, ExtendedKey extendedPubkey, int depth) {
        assertArrayEquals(ExtendedKey.PREFIX_XPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(depth, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        assertArrayEquals(ExtendedKey.PREFIX_XPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(depth, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    private static void compareZKeys(ExtendedKey extendedPrivkey, ExtendedKey extendedPubkey, int depth) {
        assertArrayEquals(ExtendedKey.PREFIX_ZPRV.toBytes(), extendedPrivkey.getPrefix());
        assertEquals(depth, extendedPrivkey.getDepth());
        var privkey = PrivateKey.parse(extendedPrivkey.getKey());

        assertArrayEquals(ExtendedKey.PREFIX_ZPUB.toBytes(), extendedPubkey.getPrefix());
        assertEquals(depth, extendedPubkey.getDepth());
        var pubkey = S256Point.parse(extendedPubkey.getKey());

        assertTrue(pubkey.eq(privkey.getPoint()));
    }

    private static void compareExtendedKeys(ExtendedKey k3, ExtendedKey k2) {
        assertArrayEquals(k3.getPrefix(), k2.getPrefix());
        assertEquals(k3.getDepth(), k2.getDepth());
        assertArrayEquals(k3.getFingerprint(), k2.getFingerprint());
        assertArrayEquals(k3.getChildNumber(), k2.getChildNumber());
        assertArrayEquals(k3.getChainCode(), k2.getChainCode());
        assertArrayEquals(k3.getKey(), k2.getKey());
    }

    private static void compareAddress(PrivateKey privkey, S256Point pubkey, Address address) {
        assertTrue(privkey.getPoint().eq(pubkey));
        assertArrayEquals(address.hash160(), pubkey.hash160(true));
        assertEquals(address.address(), pubkey.addressBech32P2wpkh(false));
    }
}