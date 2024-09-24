package ch.bitagent.bitcoin.java.chapter;

import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.ecc.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Chapter2Test {

    @Test
    void example1() {
        assertDoesNotThrow(() -> new Point(-1, -1, 5, 7));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Point(-1, -2, 5, 7));
    }

    @Test
    void example2() {
        var p1 = new Point(-1, -1, 5, 7);
        var p2 = new Point(-1, 1, 5, 7);
        var inf = new Point(null, null, Int.parse(5), Int.parse(7));
        assertEquals("Point(-1,-1)_5_7", p1.add(inf).toString());
        assertEquals("Point(-1,1)_5_7", inf.add(p2).toString());
        assertEquals("Point(infinity)", p1.add(p2).toString());
    }
}
