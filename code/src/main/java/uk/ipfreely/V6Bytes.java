// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import static uk.ipfreely.Consts.*;

final class V6Bytes {

    private V6Bytes() {}

    static byte[] toBytes(long high, long low) {
        return new byte[]{
                toByte(high >>> BYTE7),
                toByte(high >>> BYTE6),
                toByte(high >>> BYTE5),
                toByte(high >>> BYTE4),
                toByte(high >>> BYTE3),
                toByte(high >>> BYTE2),
                toByte(high >>> BYTE1),
                toByte(high),
                toByte(low >>> BYTE7),
                toByte(low >>> BYTE6),
                toByte(low >>> BYTE5),
                toByte(low >>> BYTE4),
                toByte(low >>> BYTE3),
                toByte(low >>> BYTE2),
                toByte(low >>> BYTE1),
                toByte(low),
        };
    }

    private static byte toByte(final long l) {
        return (byte) l;
    }

    static <T> T fromBytes(final V6Function<T> factory, final byte... bytes) {
        final int longBytes = Long.SIZE / Byte.SIZE;

        final long high = toLong(0, longBytes, bytes);
        final long low = toLong(Long.SIZE / Byte.SIZE, longBytes, bytes);
        return factory.apply(high, low);
    }

    static long toLong(final int offset, final int len, final byte... bytes) {
        int off = offset;
        return switch (len) {
            case 8 -> toLong(bytes[off++]) << BYTE7
                    | toLong(bytes[off++]) << BYTE6
                    | toLong(bytes[off++]) << BYTE5
                    | toLong(bytes[off++]) << BYTE4
                    | toLong(bytes[off++]) << BYTE3
                    | toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 7 -> toLong(bytes[off++]) << BYTE6
                    | toLong(bytes[off++]) << BYTE5
                    | toLong(bytes[off++]) << BYTE4
                    | toLong(bytes[off++]) << BYTE3
                    | toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 6 -> toLong(bytes[off++]) << BYTE5
                    | toLong(bytes[off++]) << BYTE4
                    | toLong(bytes[off++]) << BYTE3
                    | toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 5 -> toLong(bytes[off++]) << BYTE4
                    | toLong(bytes[off++]) << BYTE3
                    | toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 4 -> toLong(bytes[off++]) << BYTE3
                    | toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 3 -> toLong(bytes[off++]) << BYTE2
                    | toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 2 -> toLong(bytes[off++]) << BYTE1
                    | toLong(bytes[off]);
            case 1 -> toLong(bytes[off]);
            default -> throw new AssertionError("len==" + len);
        };
    }

    private static long toLong(final byte b) {
        return b & BYTE_MASK;
    }
}
