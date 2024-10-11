package uk.ipfreely.registries;

import uk.ipfreely.Address;

import java.util.List;

public final class RegistrySet<A extends Address<A>> extends Registry<RecordSet<A>> {
    RegistrySet(String title, String id, List<RecordSet<A>> contents) {
        super(title, id, contents);
    }
}
