package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import static org.junit.jupiter.api.Assertions.*;

class UnicastTest {

    @Test
    void registry() {
        {
            RegistrySet<V4> actual = Unicast.registry(Family.v4());
            assertFalse(actual.iterator().hasNext());
        }
        {
            RegistrySet<V6> actual = Unicast.registry(Family.v6());
            assertTrue(actual.iterator().hasNext());
        }
    }
}