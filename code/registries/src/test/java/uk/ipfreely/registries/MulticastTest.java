package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import static org.junit.jupiter.api.Assertions.*;

class MulticastTest {

    @Test
    void all() {
        {
            Register<V4> v4 = Multicast.all(Family.v4());
            assertNotNull(v4);
        }
        {
            Register<V6> v6 = Multicast.all(Family.v6());
            assertNotNull(v6);
        }
    }
}