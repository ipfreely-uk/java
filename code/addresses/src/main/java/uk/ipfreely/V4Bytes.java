// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

final class V4Bytes {

    private V4Bytes() {}

    static int fromBytes(byte... bytes) {
        // assert bytes.length == Integer.SIZE / Byte.SIZE;

        return byteInt(bytes[0]) << 3 * Byte.SIZE
                | byteInt(bytes[1]) << 2 * Byte.SIZE
                | byteInt(bytes[2]) << Byte.SIZE
                | byteInt(bytes[3]);
    }

    static byte[] toBytes(final int n) {
        return new byte[]{
                toByte(n >>> 3 * Byte.SIZE),
                toByte(n >>> 2 * Byte.SIZE),
                toByte(n >>> Byte.SIZE),
                toByte(n),
        };
    }

    private static int byteInt(final byte b) {
        return b & 0xFF;
    }

    private static byte toByte(final int n) {
        return (byte) n;
    }
}
