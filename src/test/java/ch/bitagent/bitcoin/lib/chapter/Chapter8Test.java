package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.S256Point;
import ch.bitagent.bitcoin.lib.ecc.Signature;
import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.helper.Varint;
import ch.bitagent.bitcoin.lib.script.Script;
import ch.bitagent.bitcoin.lib.tx.Tx;
import ch.bitagent.bitcoin.lib.tx.TxIn;
import ch.bitagent.bitcoin.lib.tx.TxOut;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Chapter8Test {

    @Test
    void example1() {
        var h160 = Hex.parse("74d691da1574e6b3c192ecfb52cc8984ee7b6c56");
        var b58c = Base58.encodeChecksum(Bytes.add(new byte[]{(byte) 0x05}, h160.toBytes()));
        assertEquals("3CLoMMyuoDQTPRD3XYZtCvgvkadrAdvdXh", b58c);
    }

    @Test
    void example2() {
        var modifiedTx = Hex.parse("0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c56870000000001000000");
        var s256 = Hash.hash256(modifiedTx.toBytes());
        var z = Hex.parse(s256);
        var want = Hex.parse("e71bfa115715d6fd33796948126f40a8cdd39f187e4afb03896795189fe1423c");
        assertEquals(want, z);
    }

    @Test
    void example3() {
        var modifiedTx = Hex.parse("0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c56870000000001000000");
        var h256 = Hash.hash256(modifiedTx.toBytes());
        var z = Hex.parse(h256);
        var sec = Hex.parse("022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb70");
        var der = Hex.parse("3045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4ee942a89937");
        var point = S256Point.parse(sec.toBytes());
        var sig = Signature.parse(der.toBytes());
        assertTrue(point.verify(z, sig));
    }

    @Test
    void exercise4() {
        var tx = Hex.parse("0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6bd304d87221a000000db00483045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4ee942a8993701483045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e75402201475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f400900000000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da1574e6b3c192ecfb52cc8984ee7b6c568700000000");
        var sec = Hex.parse("03b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb71");
        var der = Hex.parse("3045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e754022");
        var hexRedeemScript = Hex.parse("475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152ae");
        var redeemScript = Script.parse(new ByteArrayInputStream(hexRedeemScript.toBytes()));
        var stream = new ByteArrayInputStream(tx.toBytes());
        var txObj = Tx.parse(stream, null);
        var s = txObj.getVersion().toBytesLittleEndian(4);
        s = Bytes.add(s, Varint.encode(Int.parse(txObj.getTxIns().size())));
        var i = txObj.getTxIns().get(0);
        s = Bytes.add(s, new TxIn(i.getPrevTx(), i.getPrevIndex(), redeemScript, i.getSequence()).serialize());
        s = Bytes.add(s, Varint.encode(Int.parse(txObj.getTxOuts().size())));
        for (TxOut txOut : txObj.getTxOuts()) {
            s = Bytes.add(s, txOut.serialize());
        }
        s = Bytes.add(s, txObj.getLocktime().toBytesLittleEndian(4));
        s = Bytes.add(s, Hash.SIGHASH_ALL.toBytesLittleEndian(4));
        var z = Hex.parse(Hash.hash256(s));
        var point = S256Point.parse(sec.toBytes());
        var sig = Signature.parse(der.toBytes());
        assertTrue(point.verify(z, sig));
    }
}
