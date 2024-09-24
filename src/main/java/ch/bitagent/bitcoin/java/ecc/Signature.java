package ch.bitagent.bitcoin.java.ecc;

import ch.bitagent.bitcoin.java.helper.Bytes;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

/**
 * A signature on a secp256k1 elliptic curve
 */
public class Signature {

    private static final Logger log = Logger.getLogger(Signature.class.getSimpleName());

    private final Int r;
    private final Int s;

    public Signature(Int r, Int s) {
        this.r = r;
        this.s = s;
    }

    public byte[] der() {
        var rBin = this.getR().toBytes(32);
        log.fine(String.format("rBin %s", Bytes.byteArrayToHexString(rBin)));
        rBin = Bytes.lstrip(rBin, (byte) 0x00);
        if ((rBin[0] & 0x80) != 0) {
            rBin = Bytes.add(new byte[]{0x00}, rBin);
        }
        var rBytes = new byte[]{0x02, (byte) rBin.length};
        var rResult = Bytes.add(rBytes, rBin);

        var sBin = this.getS().toBytes(32);
        log.fine(String.format("sBin %s", Bytes.byteArrayToHexString(sBin)));
        sBin = Bytes.lstrip(sBin, (byte) 0x00);
        if ((sBin[0] & 0x80) != 0) {
            sBin = Bytes.add(new byte[]{0x00}, sBin);
        }
        var sBytes = new byte[]{0x02, (byte) sBin.length};
        var sResult = Bytes.add(sBytes, sBin);

        byte[] der = Bytes.add(new byte[][]{new byte[]{0x30}, new byte[]{(byte) (rResult.length + sResult.length)}, rResult, sResult});
        log.fine(String.format("der %s", Bytes.byteArrayToHexString(der)));

        // test der
        boolean derNok = false;
        var sig2 = Signature.parse(der);
        if (sig2.getR().ne(this.getR())) {
            derNok = true;
            log.severe(String.format("invalid der r %s %s", sig2.getR().toHex(), this.getR().toHex()));
        }
        if (sig2.getS().ne(this.getS())) {
            derNok = true;
            log.severe(String.format("invalid der s %s %s", sig2.getS().toHex(), this.getS().toHex()));
        }
        if (derNok) {
            log.severe(String.format("invalid der %s", Bytes.byteArrayToHexString(der)));
        } else {
            log.fine(String.format("valid der %s", Bytes.byteArrayToHexString(der)));
        }

        return der;
    }

    public static Signature parse(byte[] signatureBin) {
        var signatureStream = new ByteArrayInputStream(signatureBin);
        var compound = Hex.parse(Bytes.read(signatureStream, 1));
        if (compound.ne(Hex.parse("30"))) {
            throw new IllegalArgumentException("Bad Signature Compound");
        }
        var length = Hex.parse(Bytes.read(signatureStream, 1));
        if (length.add(Int.parse(2)).ne(Int.parse(signatureBin.length))) {
            throw new IllegalArgumentException("Bad Signature Length");
        }
        var marker = Hex.parse(Bytes.read(signatureStream, 1));
        if (marker.ne(Hex.parse("02"))) {
            throw new IllegalArgumentException("Bad Signature Marker");
        }
        var rLength = Hex.parse(Bytes.read(signatureStream, 1));
        var r = Hex.parse(Bytes.read(signatureStream, rLength.intValue()));
        marker = Hex.parse(Bytes.read(signatureStream, 1));
        if (marker.ne(Hex.parse("02"))) {
            throw new IllegalArgumentException("Bad Signature Marker");
        }
        var sLength = Hex.parse(Bytes.read(signatureStream, 1));
        var s = Hex.parse(Bytes.read(signatureStream, sLength.intValue()));
        if (signatureBin.length != 6 + rLength.intValue() + sLength.intValue()) {
            throw new IllegalArgumentException("Signature too long");
        }
        return new Signature(r, s);
    }

    @Override
    public String toString() {
        return String.format("Signature(0x%s,0x%s)", this.r.toString(), this.s.toString());
    }

    public Int getR() {
        return r;
    }

    public Int getS() {
        return s;
    }
}
