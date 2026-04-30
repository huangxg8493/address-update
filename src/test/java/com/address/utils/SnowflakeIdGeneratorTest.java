package com.address.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

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

    @Test
    public void testGenerate8DigitId() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        String id = generator.generate8DigitId();
        assertNotNull(id);
        assertEquals(8, id.length());
        assertTrue(id.matches("\\d{8}"));

        // 验证批量唯一性
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ids.add(generator.generate8DigitId());
        }
        assertEquals(100, ids.size(), "100 个 ID 应全部唯一");
    }
}
