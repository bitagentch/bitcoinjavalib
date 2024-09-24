package ch.bitagent.bitcoin.java.ecc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void eq() {
        var p1 = new Point(Int.parse(-1), Int.parse(-1), Int.parse(5), Int.parse(7));
        var p2 = new Point(Int.parse(18), Int.parse(77), Int.parse(5), Int.parse(7));
        assertTrue(p1.eq(p1));
        assertFalse(p1.eq(p2));
    }

    @Test
    void ne() {
        var p1 = new Point(Int.parse(-1), Int.parse(-1), Int.parse(5), Int.parse(7));
        var p2 = new Point(Int.parse(18), Int.parse(77), Int.parse(5), Int.parse(7));
        assertFalse(p1.ne(p1));
        assertTrue(p1.ne(p2));

        var a = new Point(3, -7, 5, 7);
        var b = new Point(18, 77, 5, 7);
        assertTrue(a.ne(b));
        assertFalse(a.ne(a));
    }

    @Test
    void addInt() {
        var p1 = new Point(Int.parse(-1), Int.parse(-1), Int.parse(5), Int.parse(7));
        var p2 = new Point(Int.parse(-1), Int.parse(1), Int.parse(5), Int.parse(7));
        var inf = new Point(null, null, Int.parse(5), Int.parse(7));

        var p3 = p1.add(p2);
        assertTrue(p3.eq(inf));

        var p4 = p1.add(inf);
        assertTrue(p4.eq(p1));

        var p5 = new Point(Int.parse(2), Int.parse(5), Int.parse(5), Int.parse(7));
        var p6 = new Point(Int.parse(3), Int.parse(-7), Int.parse(5), Int.parse(7));
        var p7 = p5.add(p1);
        assertTrue(p6.eq(p7));

        var p8 = new Point(Int.parse(18), Int.parse(77), Int.parse(5), Int.parse(7));
        var p9 = p1.add(p1);
        assertTrue(p8.eq(p9));

        var a = new Point(null, null, Int.parse(5), Int.parse(7));
        var b = new Point(2, 5, 5, 7);
        var c = new Point(2, -5, 5, 7);
        assertTrue(a.add(b).eq(b));
        assertTrue(b.add(a).eq(b));
        assertTrue(b.add(c).eq(a));

        a = new Point(3, 7, 5, 7);
        b = new Point(-1, -1, 5, 7);
        assertTrue(a.add(b).eq(new Point(2, -5, 5, 7)));

        a = new Point(-1, 1, 5, 7);
        assertTrue(a.add(a).eq(new Point(18, -77, 5, 7)));
    }

    @Test
    void addField() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var p1 = new Point(new FieldElement(192, prime), new FieldElement(105, prime), a, b);
        var p2 = new Point(new FieldElement(17, prime), new FieldElement(56, prime), a, b);
        var p3 = new Point(new FieldElement(170, prime), new FieldElement(142, prime), a, b);
        assertTrue(p1.add(p2).eq(p3));
    }

    @Test
    void onCurve() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(Int.parse(2), Int.parse(4), Int.parse(5), Int.parse(7)));
        assertDoesNotThrow(() -> new Point(Int.parse(-1), Int.parse(-1), Int.parse(5), Int.parse(7)));
        assertDoesNotThrow(() -> new Point(Int.parse(18), Int.parse(77), Int.parse(5), Int.parse(7)));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(Int.parse(5), Int.parse(7), Int.parse(5), Int.parse(7)));

        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(-2, 4, 5, 7));
        assertDoesNotThrow(() -> new Point(3, -7, 5, 7));
        assertDoesNotThrow(() -> new Point(18, 77, 5, 7));

        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        assertDoesNotThrow(() -> new Point(new FieldElement(192, prime), new FieldElement(105, prime), a, b));
        assertDoesNotThrow(() -> new Point(new FieldElement(17, prime), new FieldElement(56, prime), a, b));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(new FieldElement(200, prime), new FieldElement(119, prime), a, b));
        assertDoesNotThrow(() -> new Point(new FieldElement(1, prime), new FieldElement(193, prime), a, b));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(new FieldElement(42, prime), new FieldElement(99, prime), a, b));
    }

    @Test
    void mul() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        var x = new FieldElement(15, prime);
        var y = new FieldElement(86, prime);
        var p = new Point(x, y, a, b);
        var inf = new Point(null, null, a, b);
        var p7 = p.mul(Int.parse(7));
        assertTrue(p7.eq(inf));
    }
}