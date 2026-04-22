package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.AddressQueryRequest;
import com.address.dto.AddressQueryResponse;
import com.address.service.ClientAddressQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientAddressQueryController {

    @Autowired
    private ClientAddressQueryService queryService;

    @PostMapping("/client/address/query")
    public ApiResponse<AddressQueryResponse> queryAddresses(@RequestBody AddressQueryRequest request) {
        if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
        }

        int pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        AddressQueryResponse response = queryService.queryPage(
            request.getClientNo(),
            request.getAddressType(),
            pageNum,
            pageSize
        );

        return ApiResponse.success(response);
    }
}
