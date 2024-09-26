package ch.bitagent.bitcoin.lib.ecc;

import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * A finite field with point operators
 */
public class FieldElement implements PointOperators {

    private static final Logger log = Logger.getLogger(FieldElement.class.getSimpleName());

    final Int num;
    final Int prime;

    /**
     * <p>Constructor for FieldElement.</p>
     *
     * @param num a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param prime a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public FieldElement(Int num, Int prime) {
        if (num.ge(prime) || num.lt(Int.parse(0))) {
            String error = String.format("Num %s not in the field range 0 to %s", num, prime.bigInt().subtract(BigInteger.ONE));
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        this.num = num;
        this.prime = prime;
    }

    /**
     * <p>Constructor for FieldElement.</p>
     *
     * @param num a int
     * @param prime a int
     */
    public FieldElement(int num, int prime) {
        this(Int.parse(num), Int.parse(prime));
    }

    /** {@inheritDoc} */
    @Override
    public boolean eq(PointOperators otherPoint) {
        FieldElement other = (FieldElement) otherPoint;
        if (other == null) {
            return false;
        }
        return this.num.eq(other.num) && this.prime.eq(other.prime);
    }

    /** {@inheritDoc} */
    @Override
    public boolean ne(PointOperators otherPoint) {
        return !this.eq(otherPoint);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public FieldElement mul(int coefficient) {
        var product = new FieldElement(Int.parse(0), this.prime);
        for (int i = 0; i < coefficient; i++) {
            product = product.add(this);
        }
        return product;
    }

    /** {@inheritDoc} */
    @Override
    public FieldElement pow(Int exponent) {
        Int pow = this.num.powMod(exponent, this.prime);
        return new FieldElement(pow, this.prime);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public FieldElement mod(Int divisor) {
        throw new IllegalStateException();
    }

    /** {@inheritDoc} */
    @Override
    public FieldElement powMod(Int exponent, Int divisor) {
        throw new IllegalStateException();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("FieldElement_%s(%s)", num, prime);
    }

    /**
     * <p>Getter for the field <code>num</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getNum() {
        return num;
    }

    /**
     * <p>Getter for the field <code>prime</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     */
    public Int getPrime() {
        return prime;
    }
}
