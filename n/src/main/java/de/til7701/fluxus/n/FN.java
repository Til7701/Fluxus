package de.til7701.fluxus.n;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FN {

    @Getter
    private final double value;
    @Getter
    private final long bitCount;

    public static FN of(double value, long bitCount) {
        double v;
        if (bitCount == 64) v = value;
        else if (bitCount == 32) v = (float) value;
        else throw new UnsupportedOperationException();
        return new FN(v, bitCount);
    }

    private static double crop(double value, long bitCount) {
        if (bitCount == 32)
            return (float) value;
        else
            return value;
    }

    private static long largerBitCount(FN left, FN right) {
        return Math.max(left.getBitCount(), right.getBitCount());
    }

    public static FN add(FN left, FN right) {
        double sum = left.value + right.value;
        long largerBitCount = largerBitCount(left, right);
        sum = crop(sum, largerBitCount);
        return new FN(sum, largerBitCount);
    }


    public static FN sub(FN left, FN right) {
        double difference = left.value - right.value;
        long largerBitCount = largerBitCount(left, right);
        difference = crop(difference, largerBitCount);
        return new FN(difference, largerBitCount);
    }

    public static FN mul(FN left, FN right) {
        double product = left.value * right.value;
        long largerBitCount = largerBitCount(left, right);
        product = crop(product, largerBitCount);
        return new FN(product, largerBitCount);
    }

    public static FN div(FN left, FN right) {
        double quotient = left.value / right.value;
        long largerBitCount = largerBitCount(left, right);
        quotient = crop(quotient, largerBitCount);
        return new FN(quotient, largerBitCount);
    }

    public static boolean eq(FN left, FN right) {
        return left.value == right.value;
    }

    public static boolean neq(FN left, FN right) {
        return left.value != right.value;
    }

    public static boolean gt(FN left, FN right) {
        return left.value > right.value;
    }

    public static boolean gte(FN left, FN right) {
        return left.value >= right.value;
    }

    public static boolean lt(FN left, FN right) {
        return left.value < right.value;
    }

    public static boolean lte(FN left, FN right) {
        return left.value <= right.value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof FN in)) return false;
        return value == in.value && bitCount == in.bitCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, bitCount);
    }

}
