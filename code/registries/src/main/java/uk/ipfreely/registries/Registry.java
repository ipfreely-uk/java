package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

public final class Registry<A extends Address<A>> implements Iterable<Record<A>> {
    private final String title;
    private final String id;
    private final List<Record<A>> contents;

    Registry(String title, String id, List<Record<A>> contents) {
        this.title = title;
        this.id = id;
        this.contents = unmodifiableList(new ArrayList<>(contents));
    }

    public String title() {
        return title;
    }

    public String id() {
        return id;
    }

    @Override
    public Iterator<Record<A>> iterator() {
        return contents.iterator();
    }

    public Stream<Record<A>> stream() {
        return contents.stream();
    }
}
