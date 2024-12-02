package ch.bitagent.bitcoin.lib.wallet;

import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {

    @Test
    void signVerifyBip137() {
        var extendedPrivkey = ExtendedKey.parse("zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE");
        var privkey = PrivateKey.parse(extendedPrivkey.derive(0).derive(0).getKey());
        var pubkey = privkey.getPoint();
        assertEquals("bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu", pubkey.addressBech32P2wpkh(false));

        var message = "";
        var signature = Message.sign(privkey, message, Address.BECH32, false);
        assertEquals("KC/aKFLHavVeaSrHf0KOFeHLGtb+tKk17jJld3kstxL7Vzk7ksabuMBtx+UkIDeLG0i0WF02AlgWBP+Vta/aMSE=", signature);
        assertTrue(Message.verify(pubkey, signature, message, false));

        signature = Message.sign(privkey, message, Address.BECH32, true);
        assertEquals("IC/aKFLHavVeaSrHf0KOFeHLGtb+tKk17jJld3kstxL7Vzk7ksabuMBtx+UkIDeLG0i0WF02AlgWBP+Vta/aMSE=", signature);
        assertTrue(Message.verify(pubkey, signature, message, true));

        message = "Hello World";
        signature = Message.sign(privkey, message, Address.BECH32, false);
        assertEquals("Jw662OPAokgnLvuNSV411BpOkZ7KcwqDuvw/rJZ3z87jFLtKXgFUJMhy05G8xIiT69/tzvq7bVQV49jWixD3XSg=", signature);
        assertTrue(Message.verify(pubkey, signature, message, false));

        signature = Message.sign(privkey, message, Address.BECH32, true);
        assertEquals("Hw662OPAokgnLvuNSV411BpOkZ7KcwqDuvw/rJZ3z87jFLtKXgFUJMhy05G8xIiT69/tzvq7bVQV49jWixD3XSg=", signature);
        assertTrue(Message.verify(pubkey, signature, message, true));
    }

    @Test
    void signVerifyDrongo() {
        var words = "absent essay fox snake vast pumpkin height crouch silent bulb excuse razor";
        var seed = MnemonicSentence.mnemonicToSeed(words, null);
        var extendedKey = ExtendedKey.parse(MnemonicSentence.seedToExtendedKey(seed, ExtendedKey.PREFIX_XPRV));
        var extendedKey440h0h00 = extendedKey.derive(44, true, false)
                .derive(0, true, false)
                .derive(0, true, false)
                .derive(0)
                .derive(0);
        var privkey = PrivateKey.parse(extendedKey440h0h00.getKey());
        var pubkey = privkey.getPoint();
        assertEquals("14JmU9a7SzieZNEtBnsZo688rt3mGrw6hr", pubkey.address(true, false));

        // 1 attempt required for low R
        var message = "Test2";
        var signature = Message.sign(privkey, message, Address.P2PKH, false);
        assertEquals("IHra0jSywF1TjIJ5uf7IDECae438cr4o3VmG6Ri7hYlDL+pUEXyUfwLwpiAfUQVqQFLgs6OaX0KsoydpuwRI71o=", signature);
        assertTrue(Message.verify(pubkey, signature, message, false));

        // 2 attempts required for low R
        message = "Test";
        signature = Message.sign(privkey, message, Address.P2PKH, false);
        assertEquals("IDgMx1ljPhLHlKUOwnO/jBIgK+K8n8mvDUDROzTgU8gOaPDMs+eYXJpNXXINUx5WpeV605p5uO6B3TzBVcvs478=", signature);
        assertTrue(Message.verify(pubkey, signature, message, false));

        // 3 attempts required for low R
        message = "Test1";
        signature = Message.sign(privkey, message, Address.P2PKH, false);
        assertEquals("IEt/v9K95YVFuRtRtWaabPVwWOFv1FSA/e874I8ABgYMbRyVvHhSwLFz0RZuO87ukxDd4TOsRdofQwMEA90LCgI=", signature);
        assertTrue(Message.verify(pubkey, signature, message, false));
    }
}