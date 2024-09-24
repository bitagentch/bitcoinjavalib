package ch.bitagent.bitcoin.java.ecc;

import java.math.BigInteger;

/**
 * A finite field on a secp256k1 elliptic curve
 */
public class S256Field extends FieldElement {

    public static final Int P = new Int(BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(2).pow(32)).subtract(BigInteger.valueOf(977)));

    public S256Field(Int num) {
        super(num, P);
    }

    @Override
    public S256Field add(PointOperators otherPoint) {
        return new S256Field(super.add(otherPoint).num);
    }

    @Override
    public S256Field pow(Int divisor) {
        return new S256Field(super.pow(divisor).num);
    }

    public S256Field sqrt() {
        return new S256Field(this.pow(P.add(Int.parse(1)).div(Int.parse(4))).num);
    }

    @Override
    public String toString() {
        return String.format("S256Field_%s", super.num.toHex());
    }
}
