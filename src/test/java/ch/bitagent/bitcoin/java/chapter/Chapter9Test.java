package ch.bitagent.bitcoin.java.chapter;

import ch.bitagent.bitcoin.java.block.Block;
import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;
import ch.bitagent.bitcoin.java.script.OpCodeNames;
import ch.bitagent.bitcoin.java.script.Script;
import ch.bitagent.bitcoin.java.script.ScriptCmd;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Chapter9Test {

    @Test
    void example1() {
        var stream = new ByteArrayInputStream(Hex.parse("4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73").toBytes());
        var s = Script.parse(stream);
        assertArrayEquals("The Times 03/Jan/2009 Chancellor on brink of second bailout for banks".getBytes(), s.getCmds().get(2).getElement());
    }

    @Test
    void example2() {
        var stream = new ByteArrayInputStream(Hex.parse("5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00").toBytes());
        // d71b07 4d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101 OP_[250] OP_[190] OP_2DROP OP_2DROP OP_ELSE OP_[142] 8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e OP_0
        var scriptSig = Script.parse(stream);
        List<ScriptCmd> cmds = scriptSig.getCmds();
        assertEquals("d71b07", cmds.get(0).getElementAsHexString());
        assertEquals(Int.parse(465879), Hex.parse(Bytes.changeOrder(cmds.get(0).getElement())));
        assertEquals("4d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101", cmds.get(1).getElementAsHexString());
        assertEquals(OpCodeNames.OP_250_FA, cmds.get(2).getOpCode());
        assertEquals(OpCodeNames.OP_190_BE, cmds.get(3).getOpCode());
        assertEquals(OpCodeNames.OP_109_2DROP, cmds.get(4).getOpCode());
        assertEquals(OpCodeNames.OP_109_2DROP, cmds.get(5).getOpCode());
        assertEquals(OpCodeNames.OP_103_ELSE, cmds.get(6).getOpCode());
        assertEquals(OpCodeNames.OP_142_8E, cmds.get(7).getOpCode());
        assertEquals("8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e", cmds.get(8).getElementAsHexString());
        assertEquals(OpCodeNames.OP_0, cmds.get(9).getOpCode());
    }

    @Test
    void example3() {
        var block = Hex.parse("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d");
        var blockHash = Bytes.changeOrder(Helper.hash256(block.toBytes()));
        var blockId = Bytes.byteArrayToHexString(blockHash);
        assertEquals("0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523", blockId);
    }

    @Test
    void example4() {
        var b = Block.parse(new ByteArrayInputStream(Hex.parse("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d").toBytes()));
        // BIP9: The >> operator is the right bit-shift operator, which throws away the rightmost 29 bits, leaving just the top 3 bits. The 0b001 is a way of writing a number in binary in Python.
        assertEquals(0b001, b.getVersion().intValue() >> 29);
        // BIP91: The & operator is the "bitwise and" operator. In our case, we right-shift by 4 bits first and then check that the rightmost bit is 1.
        assertEquals(0, b.getVersion().intValue() >> 4 & 1);
        // BIP141: We shift 1 to the right because BIP0141 was assigned to bit 1.
        assertEquals(1, b.getVersion().intValue() >> 1 & 1);
    }

    @Test
    void example5() {
        var blockId = Bytes.changeOrder(Helper.hash256(Hex.parse("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d").toBytes()));
        assertEquals("0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523", Bytes.byteArrayToHexString(blockId));
    }

    @Test
    void example6and7() {
        var bits = Hex.parse("e93c0118").toBytes();
        var bitsLen = bits.length;
        // exponent, which is the last byte
        var exponent = Hex.parse(Arrays.copyOfRange(bits, bitsLen-1, bitsLen));
        assertEquals(Int.parse(24), exponent);
        // coefficient, which is the other three bytes in little-endian
        var coefficient = Hex.parse(Bytes.changeOrder(Arrays.copyOfRange(bits, 0, bitsLen-1)));
        assertEquals(Int.parse(81129), coefficient);
        // The target is a 256-bit number that is computed directly from the bits field (in our example, e93c0118).
        // The target is very small compared to an average 256-bit number.
        var target = coefficient.mul(Int.parse(256).pow(exponent.sub(Int.parse(3))));
        assertEquals("30353962581764818649842367179120467226026534727449575424", target.toString());
        // We are purposefully printing this number as 64 hexadecimal digits to show how small the number is in 256-bit terms.
        assertEquals("0000000000000000013ce9000000000000000000000000000000000000000000", Helper.zfill64(target.toHex().toString()));

        var proof = Hex.parse(Bytes.changeOrder(Helper.hash256(Hex.parse("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d").toBytes())));
        assertEquals("0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523", Helper.zfill64(proof.toHex().toString()));
        // proof-of-work is lower than target
        assertTrue(proof.lt(target));
    }

    @Test
    void example8() {
        var target = Bytes.bitsToTarget(Hex.parse("e93c0118").toBytes());
        var dividend = new BigDecimal(Hex.parse("ffff").mul(Int.parse(256).pow(Hex.parse("1d").sub(Int.parse(3)))).bigInt());
        var divisor = new BigDecimal(target.bigInt());
        var difficulty = dividend.divide(divisor, 4, RoundingMode.HALF_EVEN);
        assertEquals("888171856257.3206", difficulty.toString());
    }

    @Test
    void example9() {
        var lastBlock = Block.parse(new ByteArrayInputStream(Hex.parse("00000020fdf740b0e49cf75bb3d5168fb3586f7613dcc5cd89675b0100000000000000002e37b144c0baced07eb7e7b64da916cd3121f2427005551aeb0ec6a6402ac7d7f0e4235954d801187f5da9f5").toBytes()));
        var firstBlock = Block.parse(new ByteArrayInputStream(Hex.parse("000000201ecd89664fd205a37566e694269ed76e425803003628ab010000000000000000bfcade29d080d9aae8fd461254b041805ae442749f2a40100440fc0e3d5868e55019345954d80118a1721b2e").toBytes()));
        var timeDifferential = lastBlock.getTimestamp().sub(firstBlock.getTimestamp());
        if (timeDifferential.gt(Helper.TWO_WEEKS.mul(Int.parse(4)))) {
            timeDifferential = Helper.TWO_WEEKS.mul(Int.parse(4));
        }
        if (timeDifferential.lt(Helper.TWO_WEEKS.div(Int.parse(4)))) {
            timeDifferential = Helper.TWO_WEEKS.div(Int.parse(4));
        }
        var newTarget = lastBlock.target().mul(timeDifferential).div(Helper.TWO_WEEKS);
        assertEquals("0000000000000000007615000000000000000000000000000000000000000000", Helper.zfill64(newTarget.toHex().toString()));
    }
}
