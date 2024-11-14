package uk.ipfreely.registries.testing;

import uk.ipfreely.Address;
import uk.ipfreely.registries.Record;
import uk.ipfreely.registries.RecordSet;
import uk.ipfreely.registries.RegistrySet;

import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistryTester {
    private RegistryTester() {}

    public static <A extends Address<A>> void verify(RegistrySet<A> r) {
        verify(r, RegistryTester::notZero);
    }

    private static void notZero(int i) {
        assertTrue(i > 0);
    }

    public static <A extends Address<A>> void verify(RegistrySet<A> r, IntConsumer verifyRecordCount) {
        int count = 0;
        assertFalse(r.title().isEmpty());
        assertFalse(r.id().isEmpty());
        for (RecordSet<A> records : r) {
            assertFalse(records.title().isEmpty());
            assertFalse(records.id().isEmpty());
            for (Record<A> record : records) {
                assertFalse(record.name().isEmpty());
                count++;
            }
        }
        verifyRecordCount.accept(count);
    }
}
