package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.Objects;

public final class Multicast {
    private Multicast() {}

    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> Register<A> all(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (Register<A>) F4.R
                : (Register<A>) F6.R;
    }

    private static final class F4 {
        static final Register<V4> R = new Xml().load(Family.v4(), MulticastAddresses.bytes());
    }

    private static final class F6 {
        static final Register<V6> R = new Xml().load(Family.v6(), Ipv6MulticastAddresses.bytes());
    }
}
