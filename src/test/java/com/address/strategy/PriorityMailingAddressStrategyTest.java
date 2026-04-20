package com.address.strategy;

import com.address.model.CifAddress;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PriorityMailingAddressStrategyTest {

    private PriorityMailingAddressStrategy strategy = new PriorityMailingAddressStrategy();

    @Test
    void testSelectMailingAddressWhenExists() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址1");
        addr1.setSeqNo("A001");
        addr1.setIsMailingAddress("Y");
        addr1.setLastChangeDate(new Date(1000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "03", "居住地址1");
        addr2.setSeqNo("A002");
        addr2.setIsMailingAddress("N");
        addr2.setLastChangeDate(new Date(2000));
        addresses.add(addr2);

        CifAddress result = strategy.select(addresses);
        assertEquals("A001", result.getSeqNo());
    }

    @Test
    void testSelectByPriorityWhenNoMailingAddress() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "01", "其他地址");
        addr1.setSeqNo("A001");
        addr1.setLastChangeDate(new Date(2000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "02", "联系地址");
        addr2.setSeqNo("A002");
        addr2.setLastChangeDate(new Date(1000));
        addresses.add(addr2);

        CifAddress result = strategy.select(addresses);
        assertEquals("A001", result.getSeqNo());
    }
}
