package ch.bitagent.bitcoin.java.ecc;

/**
 * Point operators on an elliptic curve
 */
public interface PointOperators {

    boolean eq(PointOperators otherPoint);

    boolean ne(PointOperators otherPoint);

    PointOperators add(PointOperators otherPoint);

    PointOperators sub(PointOperators otherPoint);

    PointOperators mul(PointOperators otherPoint);

    PointOperators mul(int coefficient);

    PointOperators pow(Int exponent);

    PointOperators div(PointOperators otherPoint);

    PointOperators mod(Int divisor);

    PointOperators powMod(Int exponent, Int divisor);
}
