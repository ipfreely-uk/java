package uk.ipfreely.sets;

import uk.ipfreely.Addr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class AddressSetCollector<A extends Addr<A>> implements Collector<AddressSet<A>, Collection<AddressSet<A>>, AddressSet<A>> {
    private static final AddressSetCollector<?> IMPL = new AddressSetCollector<>();

    private AddressSetCollector() {}

    @SuppressWarnings("unchecked")
    static <A extends Addr<A>> AddressSetCollector<A> impl() {
        return (AddressSetCollector<A>) IMPL;
    }

    @Override
    public Supplier<Collection<AddressSet<A>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<Collection<AddressSet<A>>, AddressSet<A>> accumulator() {
        return AddressSetCollector::accumulate;
    }

    private static <A extends Addr<A>> void accumulate(Collection<AddressSet<A>> c, AddressSet<A> s) {
        c.add(s);
        compact(c);
    }

    @Override
    public BinaryOperator<Collection<AddressSet<A>>> combiner() {
        return AddressSetCollector::combine;
    }

    private static <A extends Addr<A>> Collection<AddressSet<A>> combine(Collection<AddressSet<A>> c0, Collection<AddressSet<A>> c1) {
        c0.addAll(c1);
        compact(c0);
        return c0;
    }

    private static <A extends Addr<A>> void compact(Collection<AddressSet<A>> c) {
        if (c.size() >= 512) {
            AddressSet<A> compact = AddressSets.from(c);
            c.clear();
            c.add(compact);
        }
    }

    @Override
    public Function<Collection<AddressSet<A>>, AddressSet<A>> finisher() {
        return AddressSets::from;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
