package uk.ipfreely;

import java.math.BigInteger;
import java.util.List;

import static uk.ipfreely.Validation.validate;

final class V4Family extends Family<V4> {

    private static final BigInteger MAX_VALUE = BigInteger.valueOf(2).pow(V4Consts.WIDTH).subtract(BigInteger.ONE);
    private static final String MAX_ASSERTION = "Maximum value is " + MAX_VALUE;

    static final Family<V4> INST = new V4Family();

    private final byte version = 4;

    private V4Family() {}

    @Override
    public String toString() {
        return "IPv4";
    }

    @Override
    public V4 parse(long highBits, long lowBits) {
        validate(highBits == 0, "Out of range: high bits must be zero", highBits, ParseException::new);
        validate(lowBits >= 0 && lowBits <= 0xFFFFFFFFL, "Out of range: max value is " + 0xFFFFFFFFL, lowBits, ParseException::new);
        return V4.fromInt((int) lowBits);
    }

    @Override
    public V4 parse(CharSequence ip) {
        // TODO: efficiency
        final CharSequence[] arr = Chars.split(ip, '.');
        validate(arr.length == 4, "Invalid address; Ip4 addresses are 0.0.0.0 to 255.255.255.255", ip, ParseException::new);
        final byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            final int n = parseUintSafe(arr[i].toString(), 10);
            validate(n >= 0 && n <= IpMath.BYTE_MASK, "Invalid digit; Ip4 addresses are 0.0.0.0 to 255.255.255.255", ip, ParseException::new);
            bytes[i] = (byte) n;
        }
        return parse(bytes);
    }

    @Override
    public V4 parse(byte... ip) {
        validate(ip.length == 4, "Invalid address; Ip4 32 bit addresses are 4 bytes", ip, ParseException::new);
        final int uint32 = V4Bytes.fromBytes(ip);
        return V4.fromInt(uint32);
    }

    @Override
    public V4 parse(BigInteger ip) {
        validate(BigInteger.ZERO.compareTo(ip) <= 0, "Minimum value is 0", ip, ParseException::new);
        validate(MAX_VALUE.compareTo(ip) >= 0, MAX_ASSERTION, ip, ParseException::new);
        return V4.fromInt(ip.intValue());
    }

    @Override
    public V4 fromUint(int low) {
        return V4.fromInt(low);
    }

    @Override
    public int bitWidth() {
        return V4Consts.WIDTH;
    }

    @Override
    public Class<V4> ipType() {
        return V4.class;
    }

    @Override
    public List<V4> masks() {
        return V4MaskList.MASKS;
    }

    @Override
    public int maskBitsForBlock(V4 first, V4 last) {
        return V4.maskSizeIfBlock(first, last);
    }

    @Override
    public BigInteger maskAddressCount(int maskBits) {
        validate(maskBits >= 0 && maskBits <= bitWidth(), "Invalid mask size", maskBits, IllegalArgumentException::new);

        return InternedMaskSizes.v4(maskBits);
    }

    @Override
    public String regex() {
        // https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses
        String v4seg = "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])";
        return  "(" + v4seg + "\\.){3,3}" + v4seg;
    }

    private static int parseUintSafe(final String i, int radix) {
        try {
            return Integer.parseInt(i, radix);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
