package uk.ipfreely.registries;

import org.junit.jupiter.api.Test;
import uk.ipfreely.Address;
import uk.ipfreely.Family;
import uk.ipfreely.V4;
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

    @Test
    void member() {
        Address<V4> localhost = Family.v4().parse("127.0.0.1");
        boolean isSpecial = Special.registry(localhost.family())
                .addresses()
                .contains(localhost);
        assertTrue(isSpecial);
    }
}
