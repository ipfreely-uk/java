package uk.ipfreely;

final class IpMath {

    private IpMath() {}

    static final int IP6_SEGMENTS = 8;

    static final int BYTE_MASK = 0xFF;
    static final int SHORT_MASK = 0xFFFF;
    static final long INT_MASK = 0xFFFFFFFFL;

    static final int BYTE1 = Byte.SIZE;
    static final int BYTE2 = Byte.SIZE * 2;
    static final int BYTE3 = Byte.SIZE * 3;
    static final int BYTE4 = Byte.SIZE * 4;
    static final int BYTE5 = Byte.SIZE * 5;
    static final int BYTE6 = Byte.SIZE * 6;
    static final int BYTE7 = Byte.SIZE * 7;
}
