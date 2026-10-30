package de.til7701.fluxus.n;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UN {

    @Getter
    private final long value;
    private final long valueBitmask;
    @Getter
    private final long bitCount;

    public static UN of(long value, long bitCount) {
        if (bitCount > 64)
            throw new UnsupportedOperationException();
        return new UN(value, createBitmask(bitCount), bitCount);
    }

    private static long createBitmask(long bitCount) {
        long bitmask = 0;
        for (int i = 0; i < bitCount; i++) {
            bitmask = bitmask << 1;
            bitmask |= 1;
        }
        return bitmask;
    }

    private static long longerBitmask(UN in1, UN in2) {
        long longerBitmask = in1.valueBitmask;
        if (Long.bitCount(longerBitmask) < Long.bitCount(in2.valueBitmask))
            longerBitmask = in2.valueBitmask;
        return longerBitmask;
    }

    private static long crop(long valueToCrop, long bitmask) {
        return valueToCrop & bitmask;
    }

    public static UN add(UN left, UN right) {
        long sum = left.value + right.value;
        long longerBitmask = longerBitmask(left, right);
        sum = crop(sum, longerBitmask);
        return new UN(sum, longerBitmask, Long.bitCount(longerBitmask));
    }

    public static UN sub(UN left, UN right) {
        long difference = left.value - right.value;
        long longerBitmask = longerBitmask(left, right);
        difference = crop(difference, longerBitmask);
        return new UN(difference, longerBitmask, Long.bitCount(longerBitmask));
    }

    public static UN mul(UN left, UN right) {
        long product = left.value * right.value;
        long longerBitmask = longerBitmask(left, right);
        product = crop(product, longerBitmask);
        return new UN(product, longerBitmask, Long.bitCount(longerBitmask));
    }

    public static UN div(UN left, UN right) {
        long quotient = left.value / right.value;
        long longerBitmask = longerBitmask(left, right);
        quotient = crop(quotient, longerBitmask);
        return new UN(quotient, longerBitmask, Long.bitCount(longerBitmask));
    }

    public static UN bitAnd(UN left, UN right) {
        return new UN(left.value & right.value, left.valueBitmask, left.bitCount);
    }

    public static UN bitOr(UN left, UN right) {
        return new UN(left.value | right.value, left.valueBitmask, left.bitCount);
    }

    public static UN bitXOr(UN left, UN right) {
        return new UN(left.value ^ right.value, left.valueBitmask, left.bitCount);
    }

    public static UN shiftLeft(UN value, UN amount) {
        return new UN(value.value << amount.value, value.valueBitmask, value.bitCount);
    }

    public static UN shiftRight(UN value, UN amount) {
        return new UN(value.value >> amount.value, value.valueBitmask, value.bitCount);
    }

    public static boolean eq(UN left, UN right) {
        return left.value == right.value;
    }

    public static boolean neq(UN left, UN right) {
        return left.value != right.value;
    }

    public static boolean lt(UN left, UN right) {
        return Long.compareUnsigned(left.value, right.value) < 0;
    }

    public static boolean lte(UN left, UN right) {
        return Long.compareUnsigned(left.value, right.value) <= 0;
    }

    public static boolean gt(UN left, UN right) {
        return Long.compareUnsigned(left.value, right.value) > 0;
    }

    public static boolean gte(UN left, UN right) {
        return Long.compareUnsigned(left.value, right.value) >= 0;
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(value);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof UN in)) return false;
        return value == in.value && valueBitmask == in.valueBitmask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueBitmask);
    }

}
