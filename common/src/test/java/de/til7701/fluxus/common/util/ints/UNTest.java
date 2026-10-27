package de.til7701.fluxus.common.util.ints;

import de.til7701.fluxus.common.util.n.UN;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UNTest {

    private static final long ONES = 0xffffffffffffffffL;

    @Test
    void testAdd() {
        UN a = UN.of(1, 2);
        UN b = UN.of(2, 2);
        UN sum = UN.add(a, b);
        assertEquals(UN.of(3, 2), sum);
    }

    @Test
    void testAddOverflow() {
        UN a = UN.of(3, 2);
        UN b = UN.of(1, 2);
        UN sum = UN.add(a, b);
        assertEquals(UN.of(0, 2), sum);
    }

    @Test
    void testSub() {
        UN a = UN.of(2, 2);
        UN b = UN.of(1, 2);
        UN sum = UN.sub(a, b);
        assertEquals(UN.of(1, 2), sum);
    }

    @Test
    void testSubOverZero() {
        UN a = UN.of(1, 2);
        UN b = UN.of(2, 2);
        UN sum = UN.sub(a, b);
        assertEquals(UN.of(3, 2), sum);
    }

    @Test
    void testSubUnderflow() {
        UN a = UN.of(0, 2);
        UN b = UN.of(1, 2);
        UN sum = UN.sub(a, b);
        assertEquals(UN.of(3, 2), sum);
    }

    @Test
    void testLongMax() {
        UN a = UN.of(ONES - 1, 64);
        UN b = UN.of(1, 64);
        UN sum = UN.add(a, b);
        assertEquals(UN.of(ONES, 64), sum);
    }

    @Test
    void testLongMaxOverflow() {
        UN a = UN.of(ONES, 64);
        UN b = UN.of(1, 64);
        UN sum = UN.add(a, b);
        assertEquals(UN.of(0, 64), sum);
    }

    @Test
    void testLongMin() {
        UN a = UN.of(0, 64);
        UN b = UN.of(1, 64);
        UN sum = UN.sub(a, b);
        assertEquals(UN.of(ONES, 64), sum);
    }

    @Test
    void testLongMinOverflow() {
        UN a = UN.of(0, 64);
        UN b = UN.of(1, 64);
        UN sum = UN.sub(a, b);
        assertEquals(UN.of(ONES, 64), sum);
    }

}
