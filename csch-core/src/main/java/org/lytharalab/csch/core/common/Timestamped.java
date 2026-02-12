package org.lytharalab.csch.core.common;

import java.time.Instant;

public interface Timestamped {
    Instant getTimestamp();
    
    default long getTimestampMillis() {
        return getTimestamp().toEpochMilli();
    }
    
    default long getAgeMillis() {
        return Instant.now().toEpochMilli() - getTimestamp().toEpochMilli();
    }
}
