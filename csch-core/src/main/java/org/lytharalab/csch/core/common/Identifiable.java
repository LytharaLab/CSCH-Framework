package org.lytharalab.csch.core.common;

import java.util.UUID;

public interface Identifiable {
    String getId();
    
    default String generateId() {
        return UUID.randomUUID().toString();
    }
}
