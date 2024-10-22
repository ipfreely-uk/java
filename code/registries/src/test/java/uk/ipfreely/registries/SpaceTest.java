package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
import uk.ipfreely.V6;

import static org.junit.jupiter.api.Assertions.*;

class SpaceTest {

    @Test
    void registry() {
        {
            RegistrySet<V4> registry = Space.registry(Family.v4());
            assertNotNull(registry);
        }
        {
            RegistrySet<V6> registry = Space.registry(Family.v6());
            assertNotNull(registry);
        }
    }
}