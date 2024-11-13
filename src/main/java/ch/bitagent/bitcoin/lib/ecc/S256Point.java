package ch.bitagent.bitcoin.lib.ecc;

import ch.bitagent.bitcoin.lib.helper.Base58;
import ch.bitagent.bitcoin.lib.helper.Bech32;
import ch.bitagent.bitcoin.lib.helper.Bytes;
import ch.bitagent.bitcoin.lib.helper.Hash;
import ch.bitagent.bitcoin.lib.script.OpCodeNames;
import ch.bitagent.bitcoin.lib.script.Script;
import ch.bitagent.bitcoin.lib.script.ScriptCmd;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A point (public key) on a secp256k1 elliptic curve
 */
public class S256Point extends Point {

    /**
     * Constant <code>N</code>
     */
    public static final Int N = Hex.parse("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141");

    private static final Int GX = Hex.parse("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798");
    private static final Int GY = Hex.parse("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8");
    private static S256Point g;

    private static final Int A = Int.parse(0);
    private static final Int B = Int.parse(7);

    /**
     * <p>Constructor for S256Point.</p>
     *
     * @param x a {@link ch.bitagent.bitcoin.lib.ecc.S256Field} object
     * @param y a {@link ch.bitagent.bitcoin.lib.ecc.S256Field} object
     */
    public S256Point(S256Field x, S256Field y) {
        super(x, y, new S256Field(A), new S256Field(B));
    }

    /**
     * <p>Getter for the field <code>g</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.S256Point} object
     */
    public static S256Point getG() {
        if (S256Point.g == null) {
            S256Point.g = new S256Point(new S256Field(GX), new S256Field(GY));
        }
        return S256Point.g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S256Point add(Point other) {
        Point point = super.add(other);
        if (point.getX() == null) {
            return null;
        }
        var x = new S256Field(getNum(point.getX()));
        var y = new S256Field(getNum(point.getY()));
        return new S256Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S256Point mul(Int coefficient) {
        var coeff = coefficient.mod(N);
        Point point = super.mul(coeff);
        if (point.getX() == null && point.getY() == null) {
            return new S256Point(null, null);
        } else if (point.getX() != null) {
            var x = new S256Field(getNum(point.getX()));
            var y = new S256Field(getNum(point.getY()));
            return new S256Point(x, y);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * <p>verify.</p>
     *
     * @param z         a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param signature a {@link ch.bitagent.bitcoin.lib.ecc.Signature} object
     * @return a boolean
     */
    public boolean verify(Int z, Signature signature) {
        var sInv = signature.getS().powMod(N.sub(Int.parse(2)), N);
        var u = sInv.mul(z).mod(N);
        var v = sInv.mul(signature.getR()).mod(N);
        var total = S256Point.getG().mul(u).add(this.mul(v));
        return ((S256Field) total.getX()).num.eq(signature.getR());
    }

    /**
     * returns the binary version of the SEC format
     *
     * @param compressed a {@link java.lang.Boolean} object
     * @return an array of {@link byte} objects
     */
    public byte[] sec(Boolean compressed) {
        compressed = Objects.requireNonNullElse(compressed, true);
        byte[] x = ((S256Field) this.getX()).num.toBytes(32);
        if (Boolean.TRUE.equals(compressed)) {
            if (((S256Field) this.getY()).num.mod(Int.parse(2)).eq(Int.parse(0))) {
                return Bytes.add(new byte[]{0x02}, x);
            } else {
                return Bytes.add(new byte[]{0x03}, x);
            }
        } else {
            return Bytes.add(new byte[][]{new byte[]{0x04}, x, ((S256Field) this.getY()).num.toBytes(32)});
        }
    }

    /**
     * <p>hash160.</p>
     *
     * @param compressed a {@link java.lang.Boolean} object
     * @return an array of {@link byte} objects
     */
    public byte[] hash160(Boolean compressed) {
        return Hash.hash160(this.sec(compressed));
    }

    /**
     * Returns the address string
     *
     * @param compressed a {@link java.lang.Boolean} object
     * @param testnet    a boolean
     * @return a {@link java.lang.String} object
     */
    public String address(Boolean compressed, boolean testnet) {
        var h160 = this.hash160(compressed);
        byte prefix;
        if (testnet) {
            prefix = (byte) 0x6f;
        } else {
            prefix = (byte) 0x00;
        }
        return Base58.encodeChecksum(Bytes.add(new byte[]{prefix}, h160));
    }

    /**
     * Returns the bech32 p2wpkh address
     *
     * @param testnet
     * @return
     */
    public String addressBech32P2wpkh(boolean testnet) {
        var script = new Script(List.of(OpCodeNames.OP_0.toScriptCmd(), OpCodeNames.OP_20_PUSHBYTES_20.toScriptCmd(), new ScriptCmd(this.hash160(true))));
        return Bech32.encodeSegwit(testnet ? "tb" : "bc", script.toHex());
    }

    /**
     * returns a Point object from a SEC binary (not hex)
     *
     * @param secBin an array of {@link byte} objects
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.S256Point} object
     */
    public static S256Point parse(byte[] secBin) {
        if (secBin[0] == 4) {
            // uncompressed
            var x = Hex.parse(Arrays.copyOfRange(secBin, 1, 33));
            var y = Hex.parse(Arrays.copyOfRange(secBin, 33, 65));
            return new S256Point(new S256Field(x), new S256Field(y));
        } else if (secBin[0] == 2 || secBin[0] == 3) {
            // compressed
            var x = new S256Field(Hex.parse(Arrays.copyOfRange(secBin, 1, 33)));
            var alpha = x.pow(Int.parse(3)).add(new S256Field(B));
            var beta = alpha.sqrt();
            S256Field evenBeta;
            S256Field oddBeta;
            if (beta.num.mod(Int.parse(2)).eq(Int.parse(0))) {
                evenBeta = beta;
                oddBeta = new S256Field(S256Field.P.sub(beta.num));
            } else {
                evenBeta = new S256Field(S256Field.P.sub(beta.num));
                oddBeta = beta;
            }
            var isEven = secBin[0] == 2;
            if (isEven) {
                return new S256Point(x, evenBeta);
            } else {
                return new S256Point(x, oddBeta);
            }
        } else {
            throw new IllegalArgumentException("Invalid pubkey");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this.getX() instanceof FieldElement && this.getY() instanceof FieldElement && this.getA() instanceof FieldElement && this.getB() instanceof FieldElement) {
            return String.format("S256Point(0x%s,0x%s)", getNum(this.getX()).toHex(), getNum(this.getY()).toHex());
        } else if (this.getX() == null && this.getY() == null && this.getA() instanceof FieldElement && this.getB() instanceof FieldElement) {
            return "S256Point(infinity)";
        } else {
            return String.format("S256Point(%s,%s)_%s_%s", super.getX(), super.getY(), super.getA(), super.getB());
        }
    }
}
