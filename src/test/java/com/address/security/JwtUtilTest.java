package com.address.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerateAndParseToken() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForUnitTestMustBeAtLeast32Characters");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);

        Long userId = 123456789L;
        String phone = "13800138000";

        String token = jwtUtil.generateToken(userId, phone);

        assertNotNull(token);
        assertTrue(token.length() > 0);

        Long extractedUserId = jwtUtil.getUserIdFromToken(token);
        String extractedPhone = jwtUtil.getPhoneFromToken(token);

        assertEquals(userId, extractedUserId);
        assertEquals(phone, extractedPhone);
    }

    @Test
    public void testValidateToken() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForUnitTestMustBeAtLeast32Characters");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);

        String token = jwtUtil.generateToken(123456789L, "13800138000");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    public void testValidateInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    public void testValidateNullToken() {
        assertFalse(jwtUtil.validateToken(null));
    }
}
