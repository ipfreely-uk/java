package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *     Abstract registry container base type.
 * </p>
 * <p>
 *     Inheritance not supported outside this package.
 * </p>
 *
 * @param <R> contents type
 */
public abstract class Registry<A extends Address<A>, R extends Union<A>> implements Iterable<R>, Union<A> {
    private final String title;
    private final String id;
    private final Collection<R> contents;

    Registry(String title, String id, List<R> contents) {
        this.title = title;
        this.id = id;
        this.contents = Colls.immutable(contents);
    }

    /**
     * Contents of "registry/title" elements.
     *
     * @return natural language title
     */
    public String title() {
        return title;
    }

    /**
     * Derived from XML "registry" elements.
     *
     * @return identifier
     */
    public String id() {
        return id;
    }

    /**
     * The contents that contains address.
     *
     * @param address address to find
     * @return set
     */
    Set<R> containing(A address) {
        if (!addresses().contains(address)) {
            return Collections.emptySet();
        }
        return stream()
                .filter(r -> r.addresses().contains(address))
                .collect(Collectors.toSet());
    }

    @Override
    public Iterator<R> iterator() {
        return contents.iterator();
    }

    /**
     * @return contents as stream
     */
    public Stream<R> stream() {
        return contents.stream();
    }

    /**
     * Informational.
     *
     * @return title
     */
    @Override
    public String toString() {
        return title;
    }
}
