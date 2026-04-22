package com.address.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_with_data() {
        ApiResponse<String> response = ApiResponse.success("data");
        assertEquals("200", response.getCode());
        assertEquals("成功", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void error_with_message() {
        ApiResponse<Void> response = ApiResponse.error("400", "参数错误");
        assertEquals("400", response.getCode());
        assertEquals("参数错误", response.getMessage());
        assertNull(response.getData());
    }
}