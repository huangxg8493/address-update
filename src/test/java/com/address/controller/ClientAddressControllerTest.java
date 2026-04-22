package com.address.controller;

import com.address.dto.AddressUpdateRequest;
import com.address.dto.AddressUpdateRequest.AddressItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateAddresses_success() throws Exception {
        List<AddressItem> addresses = new ArrayList<>();
        AddressItem addr = new AddressItem();
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        addresses.add(addr);

        AddressUpdateRequest request = new AddressUpdateRequest();
        request.setClientNo("C001");
        request.setAddresses(addresses);

        mockMvc.perform(post("/client/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("成功"));
    }

    @Test
    void updateAddresses_emptyClientNo_returns400() throws Exception {
        List<AddressItem> addresses = new ArrayList<>();
        AddressItem addr = new AddressItem();
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        addresses.add(addr);

        AddressUpdateRequest request = new AddressUpdateRequest();
        request.setClientNo("");
        request.setAddresses(addresses);

        mockMvc.perform(post("/client/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }

    @Test
    void updateAddresses_nullClientNo_returns400() throws Exception {
        List<AddressItem> addresses = new ArrayList<>();
        AddressItem addr = new AddressItem();
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        addresses.add(addr);

        AddressUpdateRequest request = new AddressUpdateRequest();
        request.setClientNo(null);
        request.setAddresses(addresses);

        mockMvc.perform(post("/client/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }

    @Test
    void updateAddresses_emptyAddresses_returns400() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest();
        request.setClientNo("C001");
        request.setAddresses(new ArrayList<>());

        mockMvc.perform(post("/client/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("地址列表不能为空"));
    }

    @Test
    void updateAddresses_nullAddresses_returns400() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest();
        request.setClientNo("C001");
        request.setAddresses(null);

        mockMvc.perform(post("/client/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("地址列表不能为空"));
    }
}