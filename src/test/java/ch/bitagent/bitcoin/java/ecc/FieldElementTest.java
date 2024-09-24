package ch.bitagent.bitcoin.java.ecc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldElementTest {

    @Test
    void eq() {
        var a = new FieldElement(7, 13);
        var b = new FieldElement(6, 13);
        assertFalse(a.eq(b));
        assertTrue(a.eq(a));
    }

    @Test
    void ne() {
        var a = new FieldElement(7, 13);
        var b = new FieldElement(6, 13);
        assertTrue(a.ne(b));
        assertFalse(a.ne(a));

        a = new FieldElement(2, 31);
        b = new FieldElement(2, 31);
        var c = new  FieldElement(15, 31);
        assertTrue(a.eq(b));
        assertTrue(a.ne(c));
        assertFalse(a.ne(b));
    }

    @Test
    void add() {
        int prime = 19;
        var a = new FieldElement(7, prime);
        var b = new FieldElement(8, prime);
        var c = new FieldElement(15, prime);
        assertTrue(a.add(b).eq(c));

        a = new FieldElement(11, prime);
        b = new FieldElement(17, prime);
        c = new FieldElement(9, prime);
        assertTrue(a.add(b).eq(c));

        a = new FieldElement(9, prime);
        b = new FieldElement(10, prime);
        c = new FieldElement(0, prime);
        assertTrue(a.add(b).eq(c));

        prime = 57;
        a = new FieldElement(17, prime);
        b = new FieldElement(42, prime);
        c = new FieldElement(49, prime);
        var d = new FieldElement(51, prime);
        assertTrue(a.add(b).add(c).eq(d));

        a = new FieldElement(2, 31);
        b = new FieldElement(15, 31);
        assertTrue(a.add(b).eq(new FieldElement(17, 31)));

        a = new FieldElement(17, 31);
        b = new FieldElement(21, 31);
        assertTrue(a.add(b).eq(new FieldElement(7, 31)));
    }

    @Test
    void sub() {
        int prime = 19;
        var a = new FieldElement(6, prime);
        var b = new FieldElement(13, prime);
        var c = new FieldElement(12, prime);
        assertTrue(a.sub(b).eq(c));

        prime = 57;
        a = new FieldElement(9, prime);
        b = new FieldElement(29, prime);
        c = new FieldElement(37, prime);
        assertTrue(a.sub(b).eq(c));

        a = new FieldElement(52, prime);
        b = new FieldElement(30, prime);
        c = new FieldElement(38, prime);
        var d = new FieldElement(41, prime);
        assertTrue(a.sub(b).sub(c).eq(d));

        a = new FieldElement(29, 31);
        b = new FieldElement(4, 31);
        assertTrue(a.sub(b).eq(new FieldElement(25, 31)));

        a = new FieldElement(15, 31);
        b = new FieldElement(30, 31);
        assertTrue(a.sub(b).eq(new FieldElement(16, 31)));
    }

    @Test
    void mul() {
        int prime = 13;
        var a = new FieldElement(3, prime);
        var b = new FieldElement(12, prime);
        var c = new FieldElement(10, prime);
        assertTrue(a.mul(b).eq(c));

        a = new FieldElement(24, 31);
        b = new FieldElement(19, 31);
        assertTrue(a.mul(b).eq(new FieldElement(22, 31)));

        a = new FieldElement(24, 31);
        assertTrue(a.mul(2).eq(a.add(a)));
    }

    @Test
    void pow() {
        int prime = 13;
        var a = new FieldElement(3, prime);
        var b = new FieldElement(1, prime);
        assertTrue(a.pow(Int.parse(3)).eq(b));

        prime = 31;
        a = new FieldElement(17, prime).pow(Int.parse(-3));
        b = new FieldElement(29, prime);
        assertTrue(a.eq(b));

        a = new FieldElement(4, prime).pow(Int.parse(-4));
        b = new FieldElement(11, prime);
        var c = new FieldElement(13, prime);
        assertTrue(a.mul(b).eq(c));

        a = new FieldElement(17, 31);
        assertTrue(a.pow(Int.parse(3)).eq(new FieldElement(15, 31)));

        a = new FieldElement(5, 31);
        b = new FieldElement(18, 31);
        assertTrue(a.pow(Int.parse(5)).mul(b).eq(new FieldElement(16, 31)));
    }

    @Test
    void div() {
        int prime = 19;
        var a = new FieldElement(2, prime);
        var b = new FieldElement(7, prime);
        var c = new FieldElement(3, prime);
        assertTrue(a.div(b).eq(c));

        a = new FieldElement(7, prime);
        b = new FieldElement(5, prime);
        c = new FieldElement(9, prime);
        assertTrue(a.div(b).eq(c));

        prime = 31;
        a = new FieldElement(3, prime);
        b = new FieldElement(24, prime);
        c = new FieldElement(4, prime);
        assertTrue(a.div(b).eq(c));

        a = new FieldElement(17, 31);
        assertTrue(a.pow(Int.parse(-3)).eq(new FieldElement(29, 31)));

        a = new FieldElement(4, 31);
        b = new FieldElement(11, 31);
        assertTrue(a.pow(Int.parse(-4)).mul(b).eq(new FieldElement(13, 31)));
    }
}