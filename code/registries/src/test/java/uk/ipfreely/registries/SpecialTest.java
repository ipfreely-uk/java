package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Family;
import uk.ipfreely.V6;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpecialTest {

    @Test
    void registry() {
        assertNotNull(Special.registry(Family.v4()));
        assertNotNull(Special.registry(Family.v6()));
    }

    @Test
    void rules() {
        Record<V6> TEREDO = Special.registry(Family.v6())
                .stream()
                .flatMap(RecordSet::stream)
                .filter(r -> r.name().equals("TEREDO"))
                .findAny()
                .orElseThrow(AssertionError::new);

        Map<Special.Routing, Boolean> rules = Special.rules(TEREDO);
        assertTrue(rules.get(Special.Routing.SOURCE));
        assertTrue(rules.get(Special.Routing.DESTINATION));
        assertTrue(rules.get(Special.Routing.FORWARDABLE));
        assertNull(rules.get(Special.Routing.GLOBALLY_REACHABLE));
        assertFalse(rules.get(Special.Routing.RESERVED_BY_PROTOCOL));
    }
}
