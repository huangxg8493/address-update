package com.address.controller;

import com.address.model.CifAddress;
import com.address.service.ClientAddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClientAddressControllerTest {

    @Autowired
    private ClientAddressService service;

    @Test
    void updateAddresses_success() {
        // 构造请求
        List<CifAddress> addresses = new ArrayList<>();
        CifAddress addr = new CifAddress();
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        addresses.add(addr);

        // 调用 service（controller 逻辑直接委托给 service）
        List<CifAddress> result = service.updateAddresses("C001", addresses);

        // 验证返回非空
        assertNotNull(result);
    }
}