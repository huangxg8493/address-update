package com.address.controller;

import com.address.dto.AddressQueryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientAddressQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void queryAddresses_success() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("C001");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.clientNo").value("C001"))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void queryAddresses_withAddressType() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("C001");
        request.setAddressType("02");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    void queryAddresses_emptyClientNo_returns400() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }

    @Test
    void queryAddresses_nullClientNo_returns400() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo(null);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }
}
