package com.address.repository;

import com.address.constants.Constants;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@Primary
public class MyBatisAddressQueryRepository implements AddressQueryRepository {

    private final CifAddressMapper cifAddressMapper;

    @Autowired
    public MyBatisAddressQueryRepository(CifAddressMapper cifAddressMapper) {
        this.cifAddressMapper = cifAddressMapper;
    }

    @Override
    public PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize) {
        // 1. 先查总数
        long total = cifAddressMapper.countPage(clientNo, addressType, Constants.NO);
        // 2. 校验页码：若总数>0 且页码超过总页数，返回空列表
        if (total > 0) {
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (pageNum > totalPages) {
                return new PageResult<>(clientNo, pageNum, pageSize, total, Collections.emptyList());
            }
        }
        // 3. 分页查询
        int offset = (pageNum - 1) * pageSize;
        List<CifAddress> list = cifAddressMapper.findPage(clientNo, addressType, Constants.NO, offset, pageSize);
        return new PageResult<>(clientNo, pageNum, pageSize, total, list);
    }
}
