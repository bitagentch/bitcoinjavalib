package ch.bitagent.bitcoin.lib.ecc;

import java.util.logging.Logger;

/**
 * A point on a elliptic curve
 */
public class Point {

    private static final Logger log = Logger.getLogger(Point.class.getSimpleName());

    private final PointOperators x;
    private final PointOperators y;
    private final PointOperators a;
    private final PointOperators b;

    /**
     * <p>Constructor for Point.</p>
     *
     * @param x a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @param y a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @param a a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @param b a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    public Point(PointOperators x, PointOperators y, PointOperators a, PointOperators b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
        if (x == null && y == null) {
            return;
        }
        if (x == null) {
            throw new IllegalStateException();
        }
        if (!this.onCurve()) {
            String error = String.format("%s is not on the curve", this);
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * <p>Constructor for Point.</p>
     *
     * @param x a int
     * @param y a int
     * @param a a int
     * @param b a int
     */
    public Point(int x, int y, int a, int b) {
        this(Int.parse(x), Int.parse(y), Int.parse(a), Int.parse(b));
    }

    private boolean onCurve() {
        return this.y.pow(Int.parse(2)).eq(this.x.pow(Int.parse(3)).add(this.a.mul(this.x)).add(this.b));
    }

    /**
     * <p>eq.</p>
     *
     * @param other a {@link ch.bitagent.bitcoin.lib.ecc.Point} object
     * @return a boolean
     */
    public boolean eq(Point other) {
        if (other == null) {
            return false;
        }
        if (this.x == null && this.y == null) {
            return other.x == null && other.y == null && this.a.eq(other.a) && this.b.eq(other.b);
        } else if (other.x == null && other.y == null) {
            return false;
        } else if (this.x == null) {
            throw new IllegalStateException();
        } else {
            return this.x.eq(other.x) && this.y.eq(other.y) && this.a.eq(other.a) && this.b.eq(other.b);
        }
    }

    /**
     * <p>ne.</p>
     *
     * @param other a {@link ch.bitagent.bitcoin.lib.ecc.Point} object
     * @return a boolean
     */
    public boolean ne(Point other) {
        return !this.eq(other);
    }

    /**
     * <p>add.</p>
     *
     * @param other a {@link ch.bitagent.bitcoin.lib.ecc.Point} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Point} object
     */
    public Point add(Point other) {
        if (this.a.ne(other.a) || this.b.ne(other.b)) {
            String error = String.format("%s and %s are not on the same curve", this, other);
            log.severe(error);
            throw new IllegalArgumentException(error);
        }
        if (this.x == null) {
            return other;
        }
        if (other.x == null) {
            return this;
        }
        if (this.x.eq(other.x) && this.y.ne(other.y)) {
            return new Point(null, null, this.a, this.b);
        }
        if (this.x.ne(other.x)) {
            PointOperators s = other.y.sub(this.y).div(other.x.sub(this.x));
            PointOperators sx = s.pow(Int.parse(2)).sub(this.x).sub(other.x);
            PointOperators sy = s.mul(this.x.sub(sx)).sub(this.y);
            return new Point(sx, sy, this.a, this.b);
        }
        if (this.eq(other) && this.y.eq(this.x.mul(0))) {
            return new Point(null, null, this.a, this.b);
        }
        if (this.eq(other)) {
            PointOperators s = this.x.pow(Int.parse(2)).mul(3).add(this.a).div(this.y.mul(2));
            PointOperators sx = s.pow(Int.parse(2)).sub(this.x.mul(2));
            PointOperators sy = s.mul(this.x.sub(sx)).sub(this.y);
            return new Point(sx, sy, this.a, this.b);
        }
        throw new IllegalStateException();
    }

    /**
     * <p>mul.</p>
     *
     * @param coefficient a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.Point} object
     */
    public Point mul(Int coefficient) {
        var coeff = coefficient;
        var zero = Int.parse(0);
        var current = this;
        var result = new Point(null, null, this.a, this.b);
        while (coeff.gt(zero)) {
            if (coeff.bigInt().testBit(0)) {
                result = result.add(current);
            }
            current = current.add(current);
            coeff = Int.parse(coeff.bigInt().shiftRight(1));
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (this.x instanceof FieldElement && this.y instanceof FieldElement && this.a instanceof FieldElement && this.b instanceof FieldElement) {
            return String.format("Point(%s,%s)_%s_%s FieldElement(%s)", getNum(x), getNum(y), getNum(a), getNum(b), getPrime(x));
        } else if (this.x == null && this.y == null && this.a instanceof Int && this.b instanceof Int) {
            return "Point(infinity)";
        } else if (this.x == null && this.y == null && this.a instanceof FieldElement && this.b instanceof FieldElement) {
            return "Point(infinity)";
        } else {
            return String.format("Point(%s,%s)_%s_%s", x, y, a, b);
        }
    }

    /**
     * <p>Getter for the field <code>x</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    public PointOperators getX() {
        return x;
    }

    /**
     * <p>Getter for the field <code>y</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    public PointOperators getY() {
        return y;
    }

    /**
     * <p>Getter for the field <code>a</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    public PointOperators getA() {
        return a;
    }

    /**
     * <p>Getter for the field <code>b</code>.</p>
     *
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    public PointOperators getB() {
        return b;
    }

    public static Int getNum(PointOperators po) {
        if (po instanceof FieldElement) {
            return ((FieldElement) po).getNum();
        } else {
            return null;
        }
    }

    public static Int getPrime(PointOperators po) {
        if (po instanceof FieldElement) {
            return ((FieldElement) po).getPrime();
        } else {
            return null;
        }
    }
}
