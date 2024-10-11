package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.Objects;

public final class Multicast {
    private Multicast() {}

    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> RegistrySet<A> all(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (RegistrySet<A>) F4.R
                : (RegistrySet<A>) F6.R;
    }

    private static final class F4 {
        static final RegistrySet<V4> R = new Xml().load(Family.v4(), MulticastAddresses.bytes());
    }

    private static final class F6 {
        static final RegistrySet<V6> R = new Xml().load(Family.v6(), Ipv6MulticastAddresses.bytes());
    }
}
