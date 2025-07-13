package uk.ipfreely;

import java.util.List;

final class V4Masks {
    static List<V4> MASKS = V4MaskList.from(V4::fromInt);

    private V4Masks() {}
}
