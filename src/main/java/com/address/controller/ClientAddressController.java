package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.AddressUpdateRequest;
import com.address.dto.AddressUpdateResponse;
import com.address.model.CifAddress;
import com.address.service.ClientAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ClientAddressController {

    @Autowired
    private ClientAddressService clientAddressService;

    @PostMapping("/client/address/update")
    public ApiResponse<AddressUpdateResponse> updateAddresses(@RequestBody AddressUpdateRequest request) {
        // 参数校验
        if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
        }
        if (request.getAddresses() == null || request.getAddresses().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "地址列表不能为空");
        }

        // DTO 转换为 CifAddress
        List<CifAddress> cifAddresses = new ArrayList<>();
        for (com.address.dto.AddressUpdateRequest.AddressItem item : request.getAddresses()) {
            CifAddress addr = new CifAddress();
            addr.setClientNo(request.getClientNo());
            addr.setSeqNo(item.getSeqNo());
            addr.setAddressType(item.getAddressType());
            addr.setAddressDetail(item.getAddressDetail());
            addr.setIsMailingAddress(item.getIsMailingAddress());
            addr.setIsNewest(item.getIsNewest());
            cifAddresses.add(addr);
        }

        // 调用 service
        List<CifAddress> result = clientAddressService.updateAddresses(
            request.getClientNo(),
            cifAddresses
        );

        // 转换为响应 DTO
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AddressUpdateResponse response = new AddressUpdateResponse();
        response.setClientNo(request.getClientNo());
        List<com.address.dto.AddressUpdateResponse.AddressItem> responseItems = new ArrayList<>();
        for (CifAddress addr : result) {
            com.address.dto.AddressUpdateResponse.AddressItem item = new com.address.dto.AddressUpdateResponse.AddressItem();
            item.setSeqNo(addr.getSeqNo());
            item.setAddressType(addr.getAddressType());
            item.setAddressDetail(addr.getAddressDetail());
            item.setLastChangeDate(addr.getLastChangeDate() != null ? sdf.format(addr.getLastChangeDate()) : null);
            item.setIsMailingAddress(addr.getIsMailingAddress());
            item.setIsNewest(addr.getIsNewest());
            responseItems.add(item);
        }
        response.setAddresses(responseItems);

        return ApiResponse.success(response);
    }
}