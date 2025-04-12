package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.wallet.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaprootTest {

    @Test
    void tweakPubkey() {
        var internalPubkey = Bytes.hexStringToByteArray("d6889cb081036e0faefa3a35157ad71086b123b2b144b649798b494c300a961d");
        var tweakedPubkey = Bytes.hexStringToByteArray("53a1f6e454df1aa2776a2814a721372d6258050de330b3c6d10ee8f4e0dda343");
        assertArrayEquals(tweakedPubkey, Taproot.tweakPubkey(internalPubkey, new byte[0]));

        var scriptPubkey = Hex.parse("5153a1f6e454df1aa2776a2814a721372d6258050de330b3c6d10ee8f4e0dda343").toString();
        var bip350Address = Address.parse("bc1p2wsldez5mud2yam29q22wgfh9439spgduvct83k3pm50fcxa5dps59h4z5");
        assertEquals(scriptPubkey, bip350Address.scriptPubkey().toHex());
    }
}