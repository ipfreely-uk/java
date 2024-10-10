package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

public final class Register<A extends Address<A>> implements Iterable<Registry<A>> {
    private final String title;
    private final String id;
    private final Collection<Registry<A>> contents;

    Register(String title, String id, Collection<Registry<A>> contents) {
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
    public Iterator<Registry<A>> iterator() {
        return contents.iterator();
    }

    public Stream<Registry<A>> stream() {
        return contents.stream();
    }
}
