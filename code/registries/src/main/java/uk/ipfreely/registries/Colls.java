package uk.ipfreely.registries;

import java.util.*;

final class Colls {
    private Colls() {}

    static <K extends Enum<K>, V> Map<K, V> immutable(Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new EnumMap<>(map));
    }

    static <T> List<T> immutable(List<T> c) {
        if (c.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(c));
    }
}
