package com.address.repository;

import com.address.model.CifAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MemoryClientAddressRepositoryTest {

    private MemoryClientAddressRepository repo;

    @BeforeEach
    void setUp() {
        repo = new MemoryClientAddressRepository();
    }

    @Test
    void testSaveAndFind() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("A001", result.get(0).getSeqNo());
    }

    @Test
    void testFindByClientNoExcludesDeleted() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);
        repo.delete("A001");

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(0, result.size());
    }

    @Test
    void testUpdate() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);

        addr.setAddressDetail("北京市朝阳区");
        repo.update(addr);

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("北京市朝阳区", result.get(0).getAddressDetail());
    }
}
