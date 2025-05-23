// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely.examples;

import uk.ipfreely.Addr;

/**
 * Utility type for IP math with overflow/underflow checks that raise {@link ArithmeticException}.
 */
public class StrictAddressMath {

    private StrictAddressMath() {}

    /**
     * Raises {@link ArithmeticException} if the result would be greater than {@link uk.ipfreely.Family#max()}.
     *
     * @param augend summand
     * @param addend summand
     * @param <A>    address type
     * @return sum
     * @throws ArithmeticException on overflow
     */
    public static <A extends Addr<A>> A add(A augend, A addend) {
        A result = augend.add(addend);
        if (augend.compareTo(result) > 0 && addend.compareTo(result) > 0) {
            throw new ArithmeticException(msg(augend, " + ", addend));
        }
        return result;
    }

    /**
     * Raises {@link ArithmeticException} if the result would be less than {@link uk.ipfreely.Family#min()}.
     *
     * @param minuend   value to subtract from
     * @param subtraend value to subtract
     * @param <A>       address type
     * @return difference
     * @throws ArithmeticException on underflow
     */
    public static <A extends Addr<A>> A subtract(A minuend, A subtraend) {
        if (minuend.compareTo(subtraend) < 0) {
            throw new ArithmeticException(msg(minuend, " - ", subtraend));
        }
        return minuend.subtract(subtraend);
    }

    /**
     * Raises {@link ArithmeticException} if the result would be greater than {@link uk.ipfreely.Family#max()}.
     *
     * @param multiplier   factor
     * @param multiplicand factor
     * @param <A>          address type
     * @return product
     * @throws ArithmeticException on overflow
     */
    public static <A extends Addr<A>> A multiply(A multiplier, A multiplicand) {
        A zero = multiplier.family().min();
        if (multiplicand.equals(zero) || multiplier.equals(zero)) {
            return zero;
        }
        A max = multiplier.family().max();
        if (multiplier.compareTo(max.divide(multiplicand)) > 0) {
            throw new ArithmeticException(msg(multiplier, " * ", multiplicand));
        }
        return multiplier.multiply(multiplicand);
    }

    private static <A extends Addr<A>> String msg(A a, String op, A b) {
        return "Out of range: " + a + op + b;
    }
}
