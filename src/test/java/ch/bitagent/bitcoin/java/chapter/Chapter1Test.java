package ch.bitagent.bitcoin.java.chapter;

import ch.bitagent.bitcoin.java.ecc.FieldElement;
import ch.bitagent.bitcoin.java.ecc.Int;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class Chapter1Test {

    private static final Logger log = Logger.getLogger(Chapter1Test.class.getSimpleName());

    @Test
    void example1() {
        var a = new FieldElement(7, 13);
        var b = new FieldElement(6, 13);
        assertFalse(a.eq(b));
        assertTrue(a.eq(a));
    }

    @Test
    void example4() {
        var a = new FieldElement(7, 13);
        var b = new FieldElement(12, 13);
        var c = new FieldElement(6, 13);
        assertTrue(a.add(b).eq(c));
    }

    @Test
    void exercise5() {
        Int prime = Int.parse(19);
        Int[] k = {Int.parse(1), Int.parse(3), Int.parse(7), Int.parse(13), Int.parse(18)};
        for (Int ki : k) {
            List<Int> list = new ArrayList<>();
            for (int pi = 0; pi < prime.intValue(); pi++) {
                list.add(ki.mul(Int.parse(pi)).mod(prime));
            }
            log.fine(list.toString());
            assertEquals(prime.intValue(), new HashSet<>(list).size());
        }
        for (Int ki : k) {
            List<Int> list = new ArrayList<>();
            for (int pi = 0; pi < prime.intValue(); pi++) {
                list.add(ki.mul(Int.parse(pi)).mod(prime));
            }
            Collections.sort(list);
            log.fine(list.toString());
            assertEquals(prime.intValue(), new HashSet<>(list).size());
        }
    }

    @Test
    void example5() {
        var a = new FieldElement(3, 13);
        var b = new FieldElement(12, 13);
        var c = new FieldElement(10, 13);
        assertTrue(a.mul(b).eq(c));
    }

    @Test
    void example6() {
        var a = new FieldElement(3, 13);
        var b = new FieldElement(1, 13);
        assertTrue(a.pow(Int.parse(3)).eq(b));
    }

    @Test
    void exercise7() {
        Int[] p = {Int.parse(7), Int.parse(11), Int.parse(17), Int.parse(31)};
        for (Int prime : p) {
            List<Int> list = new ArrayList<>();
            for (int pi = 1; pi < prime.intValue(); pi++) {
                list.add(Int.parse(pi).powMod(prime.sub(Int.parse(1)), prime));
            }
            log.fine(list.toString());
            Set<Int> set = new HashSet<>(list);
            assertEquals(1, set.size());
            assertTrue(set.contains(Int.parse(1)));
        }
    }

    @Test
    void example7() {
        var a = new FieldElement(7, 13);
        var b = new FieldElement(8, 13);
        assertTrue(a.pow(Int.parse(-3)).eq(b));
    }
}