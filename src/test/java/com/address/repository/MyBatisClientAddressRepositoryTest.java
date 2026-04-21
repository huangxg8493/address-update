package com.address.repository;

import com.address.model.CifAddress;
import com.address.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyBatisClientAddressRepositoryTest {

    private MyBatisClientAddressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MyBatisClientAddressRepository();
    }

    @AfterEach
    void tearDown() {
        List<CifAddress> addresses = repository.findByClientNo("TEST_CLIENT_001");
        for (CifAddress addr : addresses) {
            repository.delete(addr.getSeqNo());
        }
        List<CifAddress> addresses2 = repository.findByClientNo("TEST_CLIENT_002");
        for (CifAddress addr : addresses2) {
            repository.delete(addr.getSeqNo());
        }
        List<CifAddress> addresses3 = repository.findByClientNo("TEST_CLIENT_003");
        for (CifAddress addr : addresses3) {
            repository.delete(addr.getSeqNo());
        }
        List<CifAddress> addresses4 = repository.findByClientNo("TEST_CLIENT_004");
        for (CifAddress addr : addresses4) {
            repository.delete(addr.getSeqNo());
        }
        List<CifAddress> addresses5 = repository.findByClientNo("TEST_CLIENT_005");
        for (CifAddress addr : addresses5) {
            repository.delete(addr.getSeqNo());
        }
    }

    @Test
    void testSaveAndFindByClientNo() {
        CifAddress address = createAddress("TEST_CLIENT_001", "01", "北京市朝阳区XXX");
        repository.save(address);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_001");
        assertEquals(1, result.size());
        assertEquals("01", result.get(0).getAddressType());
        assertEquals("北京市朝阳区XXX", result.get(0).getAddressDetail());
    }

    @Test
    void testUpdate() {
        CifAddress address = createAddress("TEST_CLIENT_002", "01", "原地址");
        repository.save(address);

        List<CifAddress> addresses = repository.findByClientNo("TEST_CLIENT_002");
        CifAddress saved = addresses.get(0);
        saved.setAddressDetail("新地址");
        saved.setLastChangeDate(new Date());
        repository.update(saved);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_002");
        assertEquals("新地址", result.get(0).getAddressDetail());
    }

    @Test
    void testSaveAll() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createAddress("TEST_CLIENT_003", "01", "地址1"));
        addresses.add(createAddress("TEST_CLIENT_003", "02", "地址2"));
        repository.saveAll(addresses);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_003");
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateAll() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createAddress("TEST_CLIENT_004", "01", "地址1"));
        addresses.add(createAddress("TEST_CLIENT_004", "02", "地址2"));
        repository.saveAll(addresses);

        List<CifAddress> saved = repository.findByClientNo("TEST_CLIENT_004");
        for (CifAddress addr : saved) {
            addr.setAddressDetail("已更新");
            addr.setLastChangeDate(new Date());
        }
        repository.updateAll(saved);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_004");
        for (CifAddress addr : result) {
            assertEquals("已更新", addr.getAddressDetail());
        }
    }

    @Test
    void testDelete() {
        CifAddress address = createAddress("TEST_CLIENT_005", "01", "待删除地址");
        repository.save(address);

        List<CifAddress> before = repository.findByClientNo("TEST_CLIENT_005");
        assertEquals(1, before.size());

        repository.delete(before.get(0).getSeqNo());

        List<CifAddress> after = repository.findByClientNo("TEST_CLIENT_005");
        assertEquals(0, after.size());
    }

    private CifAddress createAddress(String clientNo, String addressType, String addressDetail) {
        CifAddress address = new CifAddress();
        address.setSeqNo(SnowflakeIdGenerator.getInstance().nextIdAsString());
        address.setClientNo(clientNo);
        address.setAddressType(addressType);
        address.setAddressDetail(addressDetail);
        address.setLastChangeDate(new Date());
        address.setIsMailingAddress("N");
        address.setIsNewest("N");
        address.setDelFlag("N");
        return address;
    }
}
