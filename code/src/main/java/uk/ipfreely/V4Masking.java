package uk.ipfreely;

final class V4Masking {

    private V4Masking() {}

    /**
     * @param first the first IP as an int
     * @param last  the last IP as an int
     * @return the mask size if this range can be a CIDR block or -1
     */
    static int maskSizeIfBlock(final int first, final int last) {
        assert Integer.compareUnsigned(first, last) <= 0;

        int xor = first ^ last;

        if ((xor & first) != 0) {
            return -1;
        }
        if ((xor & last) != xor) {
            return -1;
        }

        int index = 0;
        while ((xor & 1) == 1) {
            xor >>>= 1;
            index++;
        }
        return (xor == 0) ? (V4Consts.WIDTH - index) : -1;
    }
}
