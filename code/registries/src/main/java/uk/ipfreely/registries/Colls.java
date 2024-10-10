package uk.ipfreely.registries;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

final class Colls {
    private Colls() {}

    static <K extends Enum<K>, V> Map<K, V> immutable(Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return new EnumMap<>(map);
    }
}
