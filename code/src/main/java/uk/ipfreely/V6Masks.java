package uk.ipfreely;

import java.util.List;

final class V6Masks {
    static final List<V6> MASKS = new V6MaskList(V6::fromLongs);

    private V6Masks() {}
}
