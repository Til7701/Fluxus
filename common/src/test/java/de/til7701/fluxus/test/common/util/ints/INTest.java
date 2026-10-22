package de.til7701.fluxus.test.common.util.ints;

import de.til7701.fluxus.common.util.ints.IN;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class INTest {

    @Test
    void testAdd() {
        IN a = IN.of(1, 3);
        IN b = IN.of(2, 3);
        IN sum = IN.add(a, b);
        assertEquals(IN.of(3, 3), sum);
    }

    @Test
    void testAddOverflow() {
        IN a = IN.of(3, 3);
        IN b = IN.of(1, 3);
        IN sum = IN.add(a, b);
        assertEquals(IN.of(-4, 3), sum);
    }

    @Test
    void testAddNegative() {
        IN a = IN.of(3, 3);
        IN b = IN.of(-1, 3);
        IN sum = IN.add(a, b);
        assertEquals(IN.of(2, 3), sum);
    }

    @Test
    void testSub() {
        IN a = IN.of(2, 3);
        IN b = IN.of(1, 3);
        IN sum = IN.sub(a, b);
        assertEquals(IN.of(1, 3), sum);
    }

    @Test
    void testSubOverZero() {
        IN a = IN.of(1, 3);
        IN b = IN.of(2, 3);
        IN sum = IN.sub(a, b);
        assertEquals(IN.of(-1, 3), sum);
    }

    @Test
    void testSubUnderflow() {
        IN a = IN.of(-4, 3);
        IN b = IN.of(1, 3);
        IN sum = IN.sub(a, b);
        assertEquals(IN.of(3, 3), sum);
    }

    @Test
    void testLongMax() {
        IN a = IN.of(Long.MAX_VALUE - 1, 64);
        IN b = IN.of(1, 64);
        IN sum = IN.add(a, b);
        assertEquals(IN.of(Long.MAX_VALUE, 64), sum);
    }

    @Test
    void testLongMaxOverflow() {
        IN a = IN.of(Long.MAX_VALUE, 64);
        IN b = IN.of(1, 64);
        IN sum = IN.add(a, b);
        assertEquals(IN.of(Long.MIN_VALUE, 64), sum);
    }

    @Test
    void testLongMin() {
        IN a = IN.of(Long.MIN_VALUE + 1, 64);
        IN b = IN.of(1, 64);
        IN sum = IN.sub(a, b);
        assertEquals(IN.of(Long.MIN_VALUE, 64), sum);
    }

    @Test
    void testLongMinOverflow() {
        IN a = IN.of(Long.MIN_VALUE, 64);
        IN b = IN.of(1, 64);
        IN sum = IN.sub(a, b);
        assertEquals(IN.of(Long.MAX_VALUE, 64), sum);
    }

}
