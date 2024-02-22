package uk.ipfreely.sets;

final class Compare {

    private Compare() {}

    static <C extends Comparable<C>> C least(C a, C b) {
        return a.compareTo(b) <= 0
                ? a
                : b;
    }

    static <C extends Comparable<C>> C greatest(C a, C b) {
        return a.compareTo(b) >= 0
                ? a
                : b;
    }

    static <C extends Comparable<C>> boolean lessOrEqual(C lesser, C bigger) {
        return lesser.compareTo(bigger) <= 0;
    }

    static <C extends Comparable<C>> boolean less(C lesser, C bigger) {
        return lesser.compareTo(bigger) < 0;
    }

    static <C extends Comparable<C>> boolean greater(C bigger, C lesser) {
        return lesser.compareTo(bigger) > 0;
    }
}
