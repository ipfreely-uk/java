package uk.ipfreely.testing;

import uk.ipfreely.Family;
import uk.ipfreely.V4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Delme {

    public static void main(String[] args) throws UnknownHostException {
        InetAddress ia = InetAddress.getByName("127.0.0.1");
        V4 localhost = Family.v4().parse("127.0.0.1");
    }
}
