package com.address.service;

import com.address.dto.AddressQueryResponse;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import com.address.repository.AddressQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientAddressQueryServiceTest {

    @Mock
    private AddressQueryRepository queryRepository;

    @InjectMocks
    private ClientAddressQueryService queryService;

    private CifAddress createTestAddress(String seqNo, String addressType) {
        CifAddress addr = new CifAddress();
        addr.setSeqNo(seqNo);
        addr.setClientNo("C001");
        addr.setAddressType(addressType);
        addr.setAddressDetail("测试地址");
        addr.setLastChangeDate(new Date());
        addr.setIsMailingAddress("N");
        addr.setIsNewest("Y");
        addr.setDelFlag("N");
        return addr;
    }

    @Test
    void queryPage_returnsCorrectResponse() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createTestAddress("1", "02"));
        addresses.add(createTestAddress("2", "03"));

        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 2, addresses);
        when(queryRepository.findPage(eq("C001"), isNull(), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", null, 1, 10);

        assertEquals("C001", response.getClientNo());
        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPageSize());
        assertEquals(2, response.getTotal());
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getList().size());
    }

    @Test
    void queryPage_withAddressType_filtersCorrectly() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createTestAddress("1", "02"));

        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 1, addresses);
        when(queryRepository.findPage(eq("C001"), eq("02"), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", "02", 1, 10);

        assertEquals(1, response.getList().size());
        assertEquals("02", response.getList().get(0).getAddressType());
    }

    @Test
    void queryPage_emptyResult_returnsEmptyList() {
        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 0, new ArrayList<>());
        when(queryRepository.findPage(eq("C001"), isNull(), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", null, 1, 10);

        assertEquals(0, response.getTotal());
        assertEquals(0, response.getList().size());
    }
}
