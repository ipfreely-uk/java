package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;
import uk.ipfreely.registries.testing.RegistryTester;

import static org.junit.jupiter.api.Assertions.*;

class MulticastTest {

    @Test
    void registry() {
        {
            RegistrySet<V4> v4 = Multicast.registry(Family.v4());
            RegistryTester.verify(v4);
        }
        {
            RegistrySet<V6> v6 = Multicast.registry(Family.v6());
            assertNotNull(v6);
        }
    }
}