package ch.bitagent.bitcoin.java.ecc;

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

    public Point(int x, int y, int a, int b) {
        this(Int.parse(x), Int.parse(y), Int.parse(a), Int.parse(b));
    }

    private boolean onCurve() {
        return this.y.pow(Int.parse(2)).eq(this.x.pow(Int.parse(3)).add(this.a.mul(this.x)).add(this.b));
    }

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

    public boolean ne(Point other) {
        return !this.eq(other);
    }

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
            coeff = new Int(coeff.bigInt().shiftRight(1));
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.x instanceof FieldElement && this.y instanceof FieldElement && this.a instanceof FieldElement && this.b instanceof FieldElement) {
            return String.format("Point(%s,%s)_%s_%s FieldElement(%s)", ((FieldElement) x).getNum(), ((FieldElement) y).getNum(), ((FieldElement) a).getNum(), ((FieldElement) b).getNum(), ((FieldElement) x).getPrime());
        } else if (this.x == null && this.y == null && this.a instanceof Int && this.b instanceof Int) {
            return "Point(infinity)";
        } else if (this.x == null && this.y == null && this.a instanceof FieldElement && this.b instanceof FieldElement) {
            return "Point(infinity)";
        } else {
            return String.format("Point(%s,%s)_%s_%s", x, y, a, b);
        }
    }

    public PointOperators getX() {
        return x;
    }

    public PointOperators getY() {
        return y;
    }

    public PointOperators getA() {
        return a;
    }

    public PointOperators getB() {
        return b;
    }
}
