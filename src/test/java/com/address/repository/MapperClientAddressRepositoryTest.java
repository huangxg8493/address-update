package com.address.repository;

import com.address.model.CifAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MapperClientAddressRepositoryTest {

    @Autowired
    private MapperClientAddressRepository repository;

    @Test
    void testSaveAndFind() {
        String clientNo = "C101";
        CifAddress addr = new CifAddress();
        addr.setSeqNo("S101");
        addr.setClientNo(clientNo);
        addr.setAddressType("02");
        addr.setAddressDetail("北京市朝阳区");
        addr.setIsMailingAddress("Y");
        addr.setIsNewest("Y");
        addr.setDelFlag("N");
        repository.save(addr);

        List<CifAddress> result = repository.findByClientNo(clientNo);
        assertEquals(1, result.size());
        assertEquals("S101", result.get(0).getSeqNo());
        assertEquals("北京市朝阳区", result.get(0).getAddressDetail());
    }

    @Test
    void testUpdate() {
        String clientNo = "C102";
        CifAddress addr = new CifAddress();
        addr.setSeqNo("S102");
        addr.setClientNo(clientNo);
        addr.setAddressType("02");
        addr.setAddressDetail("原地址");
        addr.setIsNewest("Y");
        addr.setDelFlag("N");
        repository.save(addr);

        addr.setAddressDetail("新地址");
        repository.update(addr);

        List<CifAddress> result = repository.findByClientNo(clientNo);
        assertEquals(1, result.size());
        assertEquals("新地址", result.get(0).getAddressDetail());
    }

    @Test
    void testBatchUpdate() {
        String clientNo = "C103";
        // 准备两条存量数据
        CifAddress addr1 = new CifAddress();
        addr1.setSeqNo("B101");
        addr1.setClientNo(clientNo);
        addr1.setAddressType("02");
        addr1.setAddressDetail("地址1");
        addr1.setIsNewest("Y");
        addr1.setDelFlag("N");
        repository.save(addr1);

        CifAddress addr2 = new CifAddress();
        addr2.setSeqNo("B102");
        addr2.setClientNo(clientNo);
        addr2.setAddressType("03");
        addr2.setAddressDetail("地址2");
        addr2.setIsNewest("N");
        addr2.setDelFlag("N");
        repository.save(addr2);

        // 构造更新请求
        List<CifAddress> updates = new ArrayList<>();
        CifAddress up1 = new CifAddress();
        up1.setSeqNo("B101");
        up1.setAddressDetail("更新后的地址1");
        up1.setIsNewest("Y");
        updates.add(up1);

        CifAddress up2 = new CifAddress();
        up2.setSeqNo("B102");
        up2.setAddressDetail("更新后的地址2");
        up2.setIsNewest("Y");
        updates.add(up2);

        repository.updateAll(updates);

        // 验证更新结果
        List<CifAddress> result = repository.findByClientNo(clientNo);
        assertEquals(2, result.size());

        CifAddress r1 = result.stream().filter(a -> a.getSeqNo().equals("B101")).findFirst().get();
        assertEquals("更新后的地址1", r1.getAddressDetail());
        assertEquals("Y", r1.getIsNewest());

        CifAddress r2 = result.stream().filter(a -> a.getSeqNo().equals("B102")).findFirst().get();
        assertEquals("更新后的地址2", r2.getAddressDetail());
        assertEquals("Y", r2.getIsNewest());
    }

    @Test
    void testBatchUpdatePartialFields() {
        String clientNo = "C104";
        // 只更新部分字段，其他字段应保持不变
        CifAddress addr = new CifAddress();
        addr.setSeqNo("B103");
        addr.setClientNo(clientNo);
        addr.setAddressType("02");
        addr.setAddressDetail("原始地址");
        addr.setIsMailingAddress("Y");
        addr.setIsNewest("N");
        addr.setDelFlag("N");
        repository.save(addr);

        // 只更新 addressDetail 和 isNewest
        List<CifAddress> updates = new ArrayList<>();
        CifAddress up = new CifAddress();
        up.setSeqNo("B103");
        up.setAddressDetail("仅更新地址详情");
        up.setIsNewest("Y");
        updates.add(up);

        repository.updateAll(updates);

        CifAddress result = repository.findByClientNo(clientNo).get(0);
        assertEquals("仅更新地址详情", result.getAddressDetail());
        assertEquals("Y", result.getIsNewest());
        // 未更新的字段应保持原值
        assertEquals("02", result.getAddressType());
        assertEquals("Y", result.getIsMailingAddress());
    }

    @Test
    void testDelete() {
        String clientNo = "C105";
        CifAddress addr = new CifAddress();
        addr.setSeqNo("S103");
        addr.setClientNo(clientNo);
        addr.setAddressType("02");
        addr.setAddressDetail("待删除地址");
        addr.setDelFlag("N");
        repository.save(addr);

        repository.delete("S103");

        List<CifAddress> result = repository.findByClientNo(clientNo);
        assertEquals(0, result.size());
    }

    @Test
    void testSaveAll() {
        String clientNo = "C106";
        List<CifAddress> addresses = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CifAddress addr = new CifAddress();
            addr.setSeqNo("SA1" + i);
            addr.setClientNo(clientNo);
            addr.setAddressType("0" + (i + 1));
            addr.setAddressDetail("批量地址" + i);
            addr.setDelFlag("N");
            addresses.add(addr);
        }

        repository.saveAll(addresses);

        List<CifAddress> result = repository.findByClientNo(clientNo);
        assertEquals(3, result.size());
    }
}
