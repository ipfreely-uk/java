package uk.ipfreely.registries;

public enum Unicast {
    /** Can be used as source address */
    SOURCE,
    /** Can be used as destination address */
    DESTINATION,
    /** Can be forwarded by router */
    FORWARDABLE,
    /** Can be forwarded beyond administrative domain */
    GLOBALLY_REACHABLE,
    /** Requires special handling */
    RESERVED_BY_PROTOCOL
}
