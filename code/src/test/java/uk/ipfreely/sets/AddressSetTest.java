package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.ipfreely.Family.v4;

class AddressSetTest {

    @Test
    void spliterator() {
        var small = AddressSets.of(
                AddressSets.parseCidr(v4(), "192.168.0.0/24"),
                AddressSets.parseCidr(v4(), "10.168.0.0/24")
        );
        assertNotNull(small.spliterator());
    }
}
