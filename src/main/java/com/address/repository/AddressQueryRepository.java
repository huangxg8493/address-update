package com.address.repository;

import com.address.dto.PageResult;
import com.address.model.CifAddress;

public interface AddressQueryRepository {
    PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize);
}
