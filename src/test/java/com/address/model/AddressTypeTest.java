package com.address.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(10, AddressType.values().length);
    }

    @Test
    void testFromCode() {
        assertEquals(AddressType.CONTACT, AddressType.fromCode("02"));
        assertEquals(AddressType.RESIDENCE, AddressType.fromCode("03"));
    }

    @Test
    void testFromCodeInvalid() {
        try {
            AddressType.fromCode("99");
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("无效的地址类型编码"));
        }
    }
}
