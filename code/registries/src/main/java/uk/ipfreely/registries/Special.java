package uk.ipfreely.registries;

import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import java.util.Objects;

public final class Special {
    private Special() {}

    @SuppressWarnings("unchecked")
    public static <A extends Address<A>> Register<A> all(Family<A> f) {
        Objects.requireNonNull(f);
        return f == Family.v4()
                ? (Register<A>) F4.R
                : (Register<A>) F6.R;
    }

    public static <A extends Address<A>> Record<A> loopback(Family<A> f) {
        return all(f).stream()
                .flatMap(Registry::stream)
                .filter(r -> r.name().startsWith("Loopback"))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

    private static final class F4 {
        static final Register<V4> R = new Xml().load(Family.v4(), IanaIpv4SpecialRegistry.bytes());
    }

    private static final class F6 {
        static final Register<V6> R = new Xml().load(Family.v6(), IanaIpv6SpecialRegistry.bytes());
    }
}
