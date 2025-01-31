package uk.ipfreely;

/**
 * Implemented by types that belong to a specific {@link Family}.
 *
 * @param <A> address family
 */
public interface Familial<A extends Addr<A>> {
    /**
     * Either {@link Family#v4()} or {@link Family#v6()}.
     *
     * @return the family
     */
    Family<A> family();
}
