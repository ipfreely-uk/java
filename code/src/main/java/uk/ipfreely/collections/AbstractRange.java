package uk.ipfreely.collections;

import uk.ipfreely.Address;

/**
 * Base type for implementing contract-compliant {@link Range} types.
 *
 * @param <A> address type
 */
abstract class AbstractRange<A extends Address<A>> implements Range<A> {

    /**
     * Contract implementation.
     *
     * @param obj the value to test
     * @return true when argument is a range and first and last are equal
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Range) {
            Range<?> r = (Range<?>) obj;
            return r.first().equals(first())
                    && r.last().equals(last());
        }
        return false;
    }

    /**
     * Contract implementation.
     *
     * @return first().hashCode() * 31 + last().hashCode()
     */
    @Override
    public final int hashCode() {
        return first().hashCode() * 31 + last().hashCode();
    }

    /**
     * Informational.
     *
     * @return debug string
     */
    @Override
    public String toString() {
        return first() + "-" + last();
    }
}
