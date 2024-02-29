package uk.ipfreely.testing;

import uk.ipfreely.Family;

public final class Addresses {

    private Addresses() {}

    public static String[] valid(Family<?> family) {
        if (family.equals(Family.v4())) {
            String[] v4 = {
                    "0.0.0.0",
                    "255.255.255.255",
                    "192.168.0.1",
                    "200.200.200.200",
                    "99.99.99.99",
                    "9.9.9.9",
            };
            return v4;
        }
        // TODO: embedded v4
        String[] v6 = {
                "::",
                "dead:beef:cafe:babe:deaf:f001:face:1ace",
                "0000:0000:0000:0000:0000:0000:0000:0000",
                "FE80::",
                "::1",
                "::1:1",
                "::1:1:1",
                "::1:1:1:1",
                "::1:1:1:1:1",
                "::1:1:1:1:1:1",
                "1::",
                "1:1::",
                "1:1:1::",
                "1:1:1:1::",
                "1:1:1:1:1::",
                "1:1:1:1:1:1::",
        };
        return v6;
    }

    public static String[] invalid(Family<?> family) {
        if (family.equals(Family.v4())) {
            String[] v4 = {
                    "0.0..0",
                    "255.255.0255.255",
                    "555.555.555.555",
                    "255.255.255.-1",
                    "255.255.255.256",
                    "::",
            };
            return v4;
        }
        String[] v6 = {
                "0.0.0.0",
                ":::",
                "fe::fe::fe",
                "0000000:0000:0000:0000:0000:0000:0000:0000",
                "0000:0000:0000:0000:0000:0000:0000",
                "G::",
        };
        return v6;
    }
}
