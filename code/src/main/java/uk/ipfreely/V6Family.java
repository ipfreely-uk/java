// Copyright 2024 https://github.com/ipfreely-uk/java/blob/main/LICENSE
// SPDX-License-Identifier: Apache-2.0
package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

final class V6Family extends Family<V6> {

    private static final BigInteger SIZE = BigInteger.valueOf(2).pow(V6Consts.WIDTH);
    private static final BigInteger MAX_VALUE = SIZE.subtract(BigInteger.ONE);
    private static final String MAX_ASSERTION = "Maximum value is " + MAX_VALUE;

    static final Family<V6> INST = new V6Family();

    private V6Family() {}

    @Override
    public String toString() {
        return "IPv6";
    }

    @Override
    public V6 parse(long high, long low) {
        return V6.fromLongs(high, low);
    }

    @Override
    public V6 parse(int unsigned) {
        return V6.fromLongs(0, unsigned & IpMath.INT_MASK);
    }

    @Override
    public V6 parse(CharSequence ip) {
        // TODO: IPv4 embedded https://www.rfc-editor.org/rfc/rfc6052#section-2
        // TODO: just use CharSequence

        final String str = ip.toString();
        validate(!str.contains(":::"), "Invalid IPv6 address", str, ParseException::new);

        final int shortener = str.indexOf("::");
        final String head;
        final String tail;
        if (shortener < 0) {
            head = str;
            tail = "";
        } else {
            head = str.substring(0, shortener);
            tail = str.substring(shortener + 2);
        }

        int segments = 0;

        final byte[] bytes = new byte[16];
        if (!"".equals(head)) {
            final String[] arr = head.split(":");
            validate(arr.length <= IpMath.IP6_SEGMENTS, "Invalid number of IPv6 segments; max " + IpMath.IP6_SEGMENTS, ip, ParseException::new);
            segments += arr.length;
            for (int i = 0; i < arr.length; i++) {
                String segment = arr[i];
                validateSegmentSize(segment, ip);
                final int n = parseUintSafe(segment, 16);
                validate(n >= 0 && n <= IpMath.SHORT_MASK, "Invalid digit; Ip6 addresses are :: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", ip, ParseException::new);
                bytes[i * 2] = (byte) (n >> 8);
                bytes[i * 2 + 1] = (byte) n;
            }
        }
        if (!"".equals(tail)) {
            final String[] arr = tail.split(":");
            segments += arr.length;
            final int offset = bytes.length - (arr.length * 2);
            for (int i = 0; i < arr.length; i++) {
                String segment = arr[i];
                validateSegmentSize(segment, ip);
                final int n = parseUintSafe(segment, 16);
                validate(n >= 0 && n <= IpMath.SHORT_MASK, "Invalid digit; Ip6 addresses are :: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", ip, ParseException::new);
                bytes[offset + (i * 2)] = (byte) (n >> 8);
                bytes[offset + (i * 2) + 1] = (byte) n;
            }
        }

        validate(segments <= 8, "Invalid address; Ip6 addresses are :: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", ip, ParseException::new);
        validate(shortener >= 0 || segments == 8, "Invalid address; Ip6 addresses are :: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", ip, ParseException::new);

        return parse(bytes);
    }

    private static void validateSegmentSize(CharSequence segment, CharSequence ip) {
        int len = segment.length();
        validate(len != 0 && len <= 4, "Invalid digit; Ip6 addresses are :: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", ip, ParseException::new);
    }

    @Override
    public V6 parse(byte... ip) {
        validate(ip.length == 16, "Ip6 128 bit addresses are 16 bytes", ip, ParseException::new);
        return V6Bytes.fromBytes(V6::fromLongs, ip);
    }

    @Override
    public V6 parse(BigInteger ip) {
        validate(BigInteger.ZERO.compareTo(ip) <= 0, "Minimum value is 0", ip, ParseException::new);
        validate(MAX_VALUE.compareTo(ip) >= 0, MAX_ASSERTION, ip, ParseException::new);
        return V6BigIntegers.fromBigInteger(V6::fromLongs, ip);
    }

    @Override
    public List<V6> masks() {
        return V6MaskList.MASKS;
    }

    @Override
    public int maskBitsForBlock(V6 first, V6 last) {
        return V6Masking.maskSizeIfBlock(first.highBits(), first.lowBits(), last.highBits(), last.lowBits());
    }

    @Override
    public BigInteger maskAddressCount(int maskBits) {
        validate(maskBits >= 0 && maskBits <= width(), "Invalid mask size", maskBits, IllegalArgumentException::new);

        return InternedMaskSizes.v6(maskBits);
    }

    @Override
    public String regex() {
        // https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses
        String v4seg = "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])";
        String v4addr = "(" + v4seg + "\\.){3,3}" + v4seg;
        String v6seg = "[0-9a-fA-F]{1,4}";
        String v6addr = "(" + v6seg + ":){7,7}" + v6seg + "|";
        v6addr += "(" + v6seg  + ":){1,7}:|";
        v6addr += "(" + v6seg + ":){1,6}:" + v6seg + "|";
        v6addr += "(" + v6seg + ":){1,5}(:" + v6seg + "){1,2}|";
        v6addr += "(" + v6seg + ":){1,4}(:" + v6seg + "){1,3}|";
        v6addr += "(" + v6seg + ":){1,3}(:" + v6seg + "){1,4}|";
        v6addr += "(" + v6seg + ":){1,2}(:" + v6seg + "){1,5}|";
        v6addr += v6seg + ":((:" + v6seg + "){1,6})|";
        v6addr += ":((:" + v6seg + "){1,7}|:)|";
        v6addr += "::(ffff(:0{1,4}){0,1}:){0,1}" + v4addr + "|";
        v6addr += "(" + v6seg + ":){1,4}:" + v4addr;
        return v6addr;
    }

    @Override
    public int width() {
        return V6Consts.WIDTH;
    }

    @Override
    public Class<V6> type() {
        return V6.class;
    }

    private static int parseUintSafe(final String i, int radix) {
        try {
            return Integer.parseInt(i, radix);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
