package uk.ipfreely.sets;

import org.junit.jupiter.api.Test;
import uk.ipfreely.V4;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ipfreely.Family.v4;

class AddressSetBuilderTest {

    @Test
    void build() {
        V4 zero = v4().min();
        V4 thirteen = v4().parse(13);
        V4 tenThousand = v4().parse(10_000);
        var all = AddressSets.range(zero, tenThousand);
        var prefix = AddressSets.range(zero, thirteen.prev());
        var suffix = AddressSets.range(thirteen.next(), tenThousand);
        var expected = AddressSets.of(prefix, suffix);
        var builder = new AddressSetBuilder<V4>();
        for (var a : all) {
            if (!a.equals(thirteen)) {
                builder.add(a);
            }
        }
        var actual = builder.build();
        assertEquals(expected, actual);
    }

    @Test
    void clear() {
        var builder = new AddressSetBuilder<V4>();
        V4 zero = v4().min();
        var actual = builder.add(zero)
                .clear()
                .build();
        assertEquals(AddressSets.of(), actual);
    }
}