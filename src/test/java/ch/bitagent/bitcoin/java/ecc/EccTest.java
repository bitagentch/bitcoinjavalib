package ch.bitagent.bitcoin.java.ecc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EccTest {

    @Test
    void onCurve() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        int[][] validPoints = {{192, 105}, {17, 56}, {1, 193}};
        int[][] invalidPoints = {{200, 119}, {42, 99}};
        for (int[] validPoint : validPoints) {
            var x = new FieldElement(validPoint[0], prime);
            var y = new FieldElement(validPoint[1], prime);
            assertDoesNotThrow(() -> new Point(x, y, a, b));
        }
        for (int[] invalidPoint : invalidPoints) {
            var x = new FieldElement(invalidPoint[0], prime);
            var y = new FieldElement(invalidPoint[1], prime);
            assertThrowsExactly(IllegalArgumentException.class, () -> new Point(x, y, a, b));
        }
    }

    @Test
    void add() {
        int prime = 223;
        var a = new FieldElement(0, prime);
        var b = new FieldElement(7, prime);
        int[][] additions = {
                {192, 105, 17, 56, 170, 142},
                {47, 71, 117, 141, 60, 139},
                {143, 98, 76, 66, 47, 71}
        };
        for (int[] addition : additions) {
            var x1 = new FieldElement(addition[0], prime);
            var y1 = new FieldElement(addition[1], prime);
            var p1 = new Point(x1, y1, a, b);
            var x2 = new FieldElement(addition[2], prime);
            var y2 = new FieldElement(addition[3], prime);
            var p2 = new Point(x2, y2, a, b);
            var x3 = new FieldElement(addition[4], prime);
            var y3 = new FieldElement(addition[5], prime);
            var p3 = new Point(x3, y3, a, b);
            assertTrue(p1.add(p2).eq(p3));
        }
    }
}
