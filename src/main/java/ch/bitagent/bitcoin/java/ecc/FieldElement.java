package ch.bitagent.bitcoin.java.ecc;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * A finite field with point operators
 */
public class FieldElement implements PointOperators {

    private static final Logger log = Logger.getLogger(FieldElement.class.getSimpleName());

    final Int num;
    final Int prime;

    public FieldElement(Int num, Int prime) {
        if (num.ge(prime) || num.lt(Int.parse(0))) {
            String error = String.format("Num %s not in the field range 0 to %s", num, prime.bigInt().subtract(BigInteger.ONE));
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        this.num = num;
        this.prime = prime;
    }

    public FieldElement(int num, int prime) {
        this(Int.parse(num), Int.parse(prime));
    }

    @Override
    public boolean eq(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null) {
            return false;
        }
        return this.num.eq(other.num) && this.prime.eq(other.prime);
    }

    @Override
    public boolean ne(PointOperators otherPoint) {
        return !this.eq(otherPoint);
    }

    @Override
    public FieldElement add(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null || this.prime.ne(other.prime)) {
            String error = "Cannot add two numbers in different Fields";
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        Int add = this.num.add(other.num).mod(this.prime);
        return new FieldElement(add, this.prime);
    }

    @Override
    public FieldElement sub(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null || this.prime.ne(other.prime)) {
            String error = "Cannot subtract two numbers in different Fields";
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        Int sub = this.num.sub(other.num).mod(this.prime);
        return new FieldElement(sub, this.prime);
    }

    @Override
    public FieldElement mul(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null || this.prime.ne(other.prime)) {
            String error = "Cannot multiply two numbers in different Fields";
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        Int mul = this.num.mul(other.num).mod(this.prime);
        return new FieldElement(mul, this.prime);
    }

    @Override
    public FieldElement mul(int coefficient) {
        var product = new FieldElement(Int.parse(0), this.prime);
        for (int i = 0; i < coefficient; i++) {
            product = product.add(this);
        }
        return product;
    }

    @Override
    public FieldElement pow(Int exponent) {
        Int pow = this.num.powMod(exponent, this.prime);
        return new FieldElement(pow, this.prime);
    }

    @Override
    public FieldElement div(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null || this.prime.ne(other.prime)) {
            String error = "Cannot divide two numbers in different Fields";
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        Int div = this.num.mul(other.num.powMod(this.prime.sub(Int.parse(2)), this.prime)).mod(this.prime);
        return new FieldElement(div, this.prime);
    }

    @Override
    public FieldElement mod(Int divisor) {
        throw new IllegalStateException();
    }

    @Override
    public FieldElement powMod(Int exponent, Int divisor) {
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return String.format("FieldElement_%s(%s)", num, prime);
    }

    public Int getNum() {
        return num;
    }

    public Int getPrime() {
        return prime;
    }
}