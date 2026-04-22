package com.address.service;

import com.address.dto.AddressQueryResponse;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import com.address.repository.AddressQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientAddressQueryService {

    private final AddressQueryRepository queryRepository;

    @Autowired
    public ClientAddressQueryService(AddressQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public AddressQueryResponse queryPage(String clientNo, String addressType, int pageNum, int pageSize) {
        PageResult<CifAddress> pageResult = queryRepository.findPage(clientNo, addressType, pageNum, pageSize);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AddressQueryResponse.AddressItem> items = new ArrayList<>();
        for (CifAddress addr : pageResult.getList()) {
            AddressQueryResponse.AddressItem item = new AddressQueryResponse.AddressItem();
            item.setSeqNo(addr.getSeqNo());
            item.setAddressType(addr.getAddressType());
            item.setAddressDetail(addr.getAddressDetail());
            item.setLastChangeDate(addr.getLastChangeDate() != null ? sdf.format(addr.getLastChangeDate()) : null);
            item.setIsMailingAddress(addr.getIsMailingAddress());
            item.setIsNewest(addr.getIsNewest());
            items.add(item);
        }

        AddressQueryResponse response = new AddressQueryResponse();
        response.setClientNo(clientNo);
        response.setPageNum(pageResult.getPageNum());
        response.setPageSize(pageResult.getPageSize());
        response.setTotal(pageResult.getTotal());
        response.setTotalPages(pageResult.getTotalPages());
        response.setList(items);
        return response;
    }
}
