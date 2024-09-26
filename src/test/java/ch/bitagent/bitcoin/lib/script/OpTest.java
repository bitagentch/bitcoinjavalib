package ch.bitagent.bitcoin.lib.script;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpTest {

    @Test
    void op169Hash160() {
        var stack = new ArrayDeque<byte[]>();
        stack.push("hello world".getBytes());
        assertTrue(Op.op169Hash160(stack));
        assertTrue(Hex.parse("d7d5ee7824ff93f94c3055af9382c86c68b5ca92").eq(Hex.parse(stack.pop())));
    }

    @Test
    void op172Checksig() {
        var z = Hex.parse("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d");
        var sec = Hex.parse("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        var sig = Hex.parse("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601");
        var stack = new ArrayDeque<byte[]>();
        stack.push(sig.toHex().toBytes());
        stack.push(sec.toHex().toBytes());
        assertTrue(Op.op172Checksig(stack, z));
        assertEquals(1, Op.decodeNum(stack.pop()));
    }

    @Test
    void op174Checkmultisig() {
        var z = Hex.parse("e71bfa115715d6fd33796948126f40a8cdd39f187e4afb03896795189fe1423c");
        var sig1 = Bytes.hexStringToByteArray("3045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4ee942a8993701");
        var sig2 = Bytes.hexStringToByteArray("3045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e75402201");
        var sec1 = Bytes.hexStringToByteArray("022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb70");
        var sec2 = Bytes.hexStringToByteArray("03b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb71");
        var stack = new ArrayDeque<byte[]>();
        stack.push(new byte[0]);
        stack.push(sig1);
        stack.push(sig2);
        stack.push(new byte[]{0x02});
        stack.push(sec1);
        stack.push(sec2);
        stack.push(new byte[]{0x02});
        assertTrue(Op.op174Checkmultisig(stack, z));
        assertEquals(1, Op.decodeNum(stack.pop()));
    }
}