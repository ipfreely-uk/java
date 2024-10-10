package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;

import static org.junit.jupiter.api.Assertions.*;

class SpecialTest {

    @Test
    void all() {
        assertNotNull(Special.all(Family.v4()));
        assertNotNull(Special.all(Family.v6()));
    }

    @Test
    void loopback() {
        assertNotNull(Special.loopback(Family.v4()));
        assertNotNull(Special.loopback(Family.v6()));
    }
}