package uk.ipfreely;

import java.util.List;

final class V4Masks {
    static List<V4> MASKS = new V4MaskList(V4::fromInt);

    private V4Masks() {}
}
