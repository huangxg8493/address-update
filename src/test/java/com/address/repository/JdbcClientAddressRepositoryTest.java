package com.address.repository;

import com.address.model.CifAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcClientAddressRepositoryTest {

    private JdbcClientAddressRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new JdbcClientAddressRepository();
        repository.clearTable();
    }

    @Test
    public void testSaveAndFind() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("北京市朝阳区", result.get(0).getAddressDetail());
    }

    @Test
    public void testUpdate() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        address.setAddressDetail("上海市浦东新区");
        repository.update(address);

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals("上海市浦东新区", result.get(0).getAddressDetail());
    }

    @Test
    public void testDelete() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        repository.delete(address.getSeqNo());

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals(0, result.size());
    }

    private CifAddress createAddress(String clientNo, String type, String detail) {
        CifAddress address = new CifAddress();
        address.setSeqNo("SN" + System.currentTimeMillis());
        address.setClientNo(clientNo);
        address.setAddressType(type);
        address.setAddressDetail(detail);
        address.setLastChangeDate(new Date());
        address.setIsMailingAddress("N");
        address.setIsNewest("N");
        address.setDelFlag("N");
        return address;
    }
}
