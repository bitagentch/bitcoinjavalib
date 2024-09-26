package ch.bitagent.bitcoin.java.ecc;

import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.script.ScriptCmd;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * An Integer based on BigInteger with Point Operators
 */
public class Int implements PointOperators, Comparable<Int> {

    private static final Logger log = Logger.getLogger(Int.class.getSimpleName());

    private final BigInteger bigInt;
    private final int bigIntLength;

    private Int(String i) {
        this.bigInt = new BigInteger(i);
        this.bigIntLength = i.length() / 2;
    }

    /**
     * <p>parse.</p>
     *
     * @param i a {@link java.lang.String} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public static Int parse(String i) {
        return new Int(i);
    }

    /**
     * <p>Constructor for Int.</p>
     *
     * @param i a {@link java.lang.String} object
     * @param radix a int
     */
    protected Int(String i, int radix) {
        this.bigInt = new BigInteger(i, radix);
        this.bigIntLength = i.length() / 2;
    }

    /**
     * <p>parse.</p>
     *
     * @param i a int
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public static Int parse(int i) {
        return new Int(String.valueOf(i));
    }

    /**
     * <p>parse.</p>
     *
     * @param i a long
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    public static Int parse(long i) {
        return new Int(String.valueOf(i));
    }

    /**
     * <p>Constructor for Int.</p>
     *
     * @param bi a {@link java.math.BigInteger} object
     */
    public Int(BigInteger bi) {
        this.bigInt = bi;
        this.bigIntLength = bi.toByteArray().length;
    }

    /** {@inheritDoc} */
    @Override
    public boolean eq(PointOperators otherPoint) {
        if (otherPoint == null) {
            return false;
        }
        Int other = (Int) otherPoint;
        return this.bigInt.equals(other.bigInt);
    }

    /** {@inheritDoc} */
    @Override
    public boolean ne(PointOperators otherPoint) {
        return !eq(otherPoint);
    }

    /**
     * <p>lt.</p>
     *
     * @param otherInt a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a boolean
     */
    public boolean lt(Int otherInt) {
        if (otherInt == null) {
            return false;
        }
        return this.bigInt.compareTo(otherInt.bigInt) < 0;
    }

    /**
     * <p>le.</p>
     *
     * @param otherInt a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a boolean
     */
    public boolean le(Int otherInt) {
        if (otherInt == null) {
            return false;
        }
        return this.bigInt.compareTo(otherInt.bigInt) <= 0;
    }

    /**
     * <p>gt.</p>
     *
     * @param otherInt a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a boolean
     */
    public boolean gt(Int otherInt) {
        if (otherInt == null) {
            return true;
        }
        return this.bigInt.compareTo(otherInt.bigInt) > 0;
    }

    /**
     * <p>ge.</p>
     *
     * @param otherInt a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a boolean
     */
    public boolean ge(Int otherInt) {
        if (otherInt == null) {
            return true;
        }
        return this.bigInt.compareTo(otherInt.bigInt) >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public Int add(PointOperators otherPoint) {
        Int other = (Int) otherPoint;
        return new Int(this.bigInt.add(other.bigInt));
    }

    /** {@inheritDoc} */
    @Override
    public Int sub(PointOperators otherPoint) {
        Int other = (Int) otherPoint;
        return new Int(this.bigInt.subtract(other.bigInt));
    }

    /** {@inheritDoc} */
    @Override
    public Int mul(PointOperators otherPoint) {
        Int other = (Int) otherPoint;
        return new Int(this.bigInt.multiply(other.bigInt));
    }

    /** {@inheritDoc} */
    @Override
    public PointOperators mul(int coefficient) {
        var product = Int.parse(0);
        for (int i = 0; i < coefficient; i++) {
            product = product.add(this);
        }
        return product;
    }

    /** {@inheritDoc} */
    @Override
    public Int pow(Int exponent) {
        return new Int(this.bigInt.pow(exponent.bigInt.intValue()));
    }

    /** {@inheritDoc} */
    @Override
    public Int div(PointOperators otherPoint) {
        Int other = (Int) otherPoint;
        return new Int(this.bigInt.divide(other.bigInt));
    }

    /** {@inheritDoc} */
    @Override
    public Int mod(Int divisor) {
        return new Int(this.bigInt.mod(divisor.bigInt));
    }

    /** {@inheritDoc} */
    @Override
    public Int powMod(Int exponent, Int divisor) {
        return new Int(this.bigInt.modPow(exponent.bigInt, divisor.bigInt));
    }

    /**
     * <p>toBytes.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] toBytes() {
        return this.toBytes(this.bigIntLength);
    }

    /**
     * <p>toBytes.</p>
     *
     * @param length a int
     * @return an array of {@link byte} objects
     */
    public byte[] toBytes(int length) {
        byte[] bytes = this.bigInt.toByteArray();
        int len = bytes.length;
        if (len == length) {
            return bytes;
        } else if (len == length + 1) {
            return Arrays.copyOfRange(bytes, 1, length + 1);
        } else if (len < length) {
            byte[] gap = Bytes.initFill(length - len, (byte) 0x00);
            return Bytes.add(gap, bytes);
        } else {
            String error = String.format("len is %s instead of %s", len, length);
            log.severe(error);
            throw new IllegalStateException(error);
        }
    }

    /**
     * <p>toBytesLittleEndian.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] toBytesLittleEndian() {
        return Bytes.changeOrder(this.toBytes(this.bigIntLength));
    }

    /**
     * <p>toBytesLittleEndian.</p>
     *
     * @param length a int
     * @return an array of {@link byte} objects
     */
    public byte[] toBytesLittleEndian(int length) {
        return Bytes.changeOrder(this.toBytes(length));
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Int) {
            return this.eq((Int) o);
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(this.bigInt);
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Int other) {
        return this.bigInt.compareTo(other.bigInt);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.bigInt.toString();
    }

    /**
     * <p>toHex.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Hex} object
     */
    public Hex toHex() {
        return Hex.parse(this.bigInt.toByteArray());
    }

    /**
     * <p>toScriptCmd.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.java.script.ScriptCmd} object
     */
    public ScriptCmd toScriptCmd() {
        return new ScriptCmd(this);
    }

    /**
     * <p>bigInt.</p>
     *
     * @return a {@link java.math.BigInteger} object
     */
    public BigInteger bigInt() {
        return this.bigInt;
    }

    /**
     * <p>intValue.</p>
     *
     * @return a int
     */
    public int intValue() {
        return this.bigInt.intValue();
    }

    /**
     * <p>longValue.</p>
     *
     * @return a long
     */
    public long longValue() {
        return this.bigInt.longValue();
    }

    /**
     * <p>log.</p>
     *
     * @deprecated temp use only
     * @param i a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     */
    @Deprecated(since = "0")
    public static Int log(Int i) {
        log.warning(String.format("%s (%s)", i.toString(), i.bigIntLength));
        return i;
    }
}
