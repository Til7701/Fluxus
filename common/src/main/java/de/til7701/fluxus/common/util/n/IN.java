package de.til7701.fluxus.common.util.n;

import de.til7701.fluxus.common.NotImplementedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IN {

    private static final long ONES = 0xffffffffffffffffL;

    @Getter
    private final long value;
    private final long valueBitmask;
    @Getter
    private final long bitCount;

    public static IN of(long value, long bitCount) {
        if (bitCount > 64)
            throw new NotImplementedException();
        return new IN(value, createBitmask(bitCount), bitCount);
    }

    private static long createBitmask(long bitCount) {
        long bitmask = 0;
        for (int i = 0; i < bitCount - 1; i++) {
            bitmask = bitmask << 1;
            bitmask |= 1;
        }
        return bitmask;
    }

    private static long longerBitmask(IN in1, IN in2) {
        long longerBitmask = in1.valueBitmask;
        if (Long.bitCount(longerBitmask) < Long.bitCount(in2.valueBitmask))
            longerBitmask = in2.valueBitmask;
        return longerBitmask;
    }

    private static long crop(long valueToCrop, long bitmask) {
        long signBitmask = Long.highestOneBit(bitmask) << 1;
        boolean sign = Long.bitCount(valueToCrop & signBitmask) > 0;
        long cropped = valueToCrop & bitmask;
        if (!sign) {
            return cropped;
        } else {
            long signAndRest = ONES & ~bitmask;
            return cropped | signAndRest;
        }
    }

    public static IN add(IN left, IN right) {
        long sum = left.value + right.value;
        long longerBitmask = longerBitmask(left, right);
        sum = crop(sum, longerBitmask);
        return new IN(sum, longerBitmask, Long.bitCount(longerBitmask) + 1);
    }

    public static IN sub(IN left, IN right) {
        long difference = left.value - right.value;
        long longerBitmask = longerBitmask(left, right);
        difference = crop(difference, longerBitmask);
        return new IN(difference, longerBitmask, Long.bitCount(longerBitmask) + 1);
    }

    public static IN mul(IN left, IN right) {
        long product = left.value * right.value;
        long longerBitmask = longerBitmask(left, right);
        product = crop(product, longerBitmask);
        return new IN(product, longerBitmask, Long.bitCount(longerBitmask) + 1);
    }

    public static IN div(IN left, IN right) {
        long quotient = left.value / right.value;
        long longerBitmask = longerBitmask(left, right);
        quotient = crop(quotient, longerBitmask);
        return new IN(quotient, longerBitmask, Long.bitCount(longerBitmask) + 1);
    }

    public static IN bitAnd(IN left, IN right) {
        return new IN(left.value & right.value, left.valueBitmask, left.bitCount);
    }

    public static IN bitOr(IN left, IN right) {
        return new IN(left.value | right.value, left.valueBitmask, left.bitCount);
    }

    public static IN bitXOr(IN left, IN right) {
        return new IN(left.value ^ right.value, left.valueBitmask, left.bitCount);
    }

    public static IN shiftLeft(IN value, IN amount) {
        return new IN(value.value << amount.value, value.valueBitmask, value.bitCount);
    }

    public static IN shiftRight(IN value, IN amount) {
        return new IN(value.value >> amount.value, value.valueBitmask, value.bitCount);
    }

    public static boolean eq(IN left, IN right) {
        return left.value == right.value;
    }

    public static boolean neq(IN left, IN right) {
        return left.value != right.value;
    }

    public static boolean gt(IN left, IN right) {
        return left.value > right.value;
    }

    public static boolean gte(IN left, IN right) {
        return left.value >= right.value;
    }

    public static boolean lt(IN left, IN right) {
        return left.value < right.value;
    }

    public static boolean lte(IN left, IN right) {
        return left.value <= right.value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof IN in)) return false;
        return value == in.value && valueBitmask == in.valueBitmask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueBitmask);
    }

}
