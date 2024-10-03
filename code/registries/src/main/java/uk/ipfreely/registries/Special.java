package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.sets.AddressSet;

public final class Special<A extends Address<A>> implements Record<A> {
    private final String name;
    private final AddressSet<A> set;
    private final State source;
    private final State destination;
    private final State forwardable;
    private final State globallyReachable;
    private final State reservedByProtocol;

    Special(String name, AddressSet<A> set, State source, State destination, State forwardable, State globallyReachable, State reservedByProtocol) {
        this.name = name;
        this.set = set;
        this.source = source;
        this.destination = destination;
        this.forwardable = forwardable;
        this.globallyReachable = globallyReachable;
        this.reservedByProtocol = reservedByProtocol;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public AddressSet<A> set() {
        return set;
    }

    public State getSource() {
        return source;
    }

    public State getDestination() {
        return destination;
    }

    public State getForwardable() {
        return forwardable;
    }

    public State getGloballyReachable() {
        return globallyReachable;
    }

    public State getReservedByProtocol() {
        return reservedByProtocol;
    }

    @Override
    public String toString() {
        return name + " " + set;
    }
}
