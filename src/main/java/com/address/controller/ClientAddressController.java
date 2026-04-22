package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.AddressUpdateRequest;
import com.address.model.CifAddress;
import com.address.service.ClientAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientAddressController {

    @Autowired
    private ClientAddressService clientAddressService;

    @PostMapping("/client/address/update")
    public ApiResponse<List<CifAddress>> updateAddresses(@RequestBody AddressUpdateRequest request) {
        // 参数校验
        if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
        }
        if (request.getAddresses() == null || request.getAddresses().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "地址列表不能为空");
        }

        // 调用 service
        List<CifAddress> result = clientAddressService.updateAddresses(
            request.getClientNo(),
            request.getAddresses()
        );

        return ApiResponse.success(result);
    }
}