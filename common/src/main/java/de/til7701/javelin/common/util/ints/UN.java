package de.til7701.javelin.common.util.ints;

import de.til7701.javelin.common.NotImplementedException;
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
    private final int bitCount;

    public static UN of(long value, int bitCount) {
        if (bitCount > 64)
            throw new NotImplementedException();
        return new UN(value, createBitmask(bitCount), bitCount);
    }

    private static long createBitmask(int bitCount) {
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

    public static UN shift_left(UN value, UN amount) {
        return new UN(value.value << amount.value, value.valueBitmask, value.bitCount);
    }

    public static UN shift_right(UN value, UN amount) {
        return new UN(value.value >> amount.value, value.valueBitmask, value.bitCount);
    }

    @Override
    public String toString() {
        return Long.toString(value);
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
