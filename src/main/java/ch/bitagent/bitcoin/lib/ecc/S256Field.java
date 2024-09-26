package ch.bitagent.bitcoin.lib.ecc;

import java.math.BigInteger;

/**
 * A finite field on a secp256k1 elliptic curve
 */
public class S256Field extends FieldElement {

    /** Constant <code>P</code> */
    public static final Int P = new Int(BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(2).pow(32)).subtract(BigInteger.valueOf(977)));

    /**
     * <p>Constructor for S256Field.</p>
     *
     * @param num a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public S256Field(Int num) {
        super(num, P);
    }

    /** {@inheritDoc} */
    @Override
    public S256Field add(PointOperators otherPoint) {
        return new S256Field(super.add(otherPoint).num);
    }

    /** {@inheritDoc} */
    @Override
    public S256Field pow(Int divisor) {
        return new S256Field(super.pow(divisor).num);
    }

    /**
     * <p>sqrt.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.S256Field} object
     */
    public S256Field sqrt() {
        return new S256Field(this.pow(P.add(Int.parse(1)).div(Int.parse(4))).num);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("S256Field_%s", super.num.toHex());
    }
}
