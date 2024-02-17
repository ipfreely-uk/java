package uk.ipfreely.collections;

import uk.ipfreely.Address;

/**
 * <p>
 *     Indicates that iteration has exceeded a given guard range.
 * </p>
 * <p>
 *     IP address ranges can be very large, particularly in the IPv6 family.
 *     Use guarded types to prevent what are effectively infinite loops.
 * </p>
 * <p>
 *     {@link java.util.Iterator},
 *     {@link java.util.Spliterator}, or
 *     {@link java.util.stream.Stream}
 *     types produced by a guarded type
 *     throw this exception when the number of iterations
 *     exceeds a limit.
 * </p>
 *
 * @see Ranges#guarded(Block, Address)
 * @see Ranges#guarded(Range, Address)
 */
public class ExcessiveIterationException extends RuntimeException {
}
