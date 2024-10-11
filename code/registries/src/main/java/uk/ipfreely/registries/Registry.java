package uk.ipfreely.registries;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class Registry<R> implements Iterable<R> {
    private final String title;
    private final String id;
    private final Collection<R> contents;

    Registry(String title, String id, List<R> contents) {
        this.title = title;
        this.id = id;
        this.contents = Colls.immutable(contents);
    }

    public String title() {
        return title;
    }

    public String id() {
        return id;
    }

    @Override
    public Iterator<R> iterator() {
        return contents.iterator();
    }

    public Stream<R> stream() {
        return contents.stream();
    }

    @Override
    public String toString() {
        return title;
    }
}
