package com.address.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SnowflakeIdGeneratorTest {

    @Test
    public void testNextId_ReturnsPositiveLong() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        long id = generator.nextId();
        assertTrue(id > 0);
    }

    @Test
    public void testNextId_ReturnsUniqueIds() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNextIdAsString_ReturnsStringRepresentation() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        String idStr = generator.nextIdAsString();
        assertNotNull(idStr);
        assertTrue(Long.parseLong(idStr) > 0);
    }
}
