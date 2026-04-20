package com.address.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CifAddressTest {

    @Test
    void testConstructor() {
        CifAddress address = new CifAddress("C001", "01", "北京市朝阳区");
        assertEquals("C001", address.getClientNo());
        assertEquals("01", address.getAddressType());
        assertEquals("北京市朝阳区", address.getAddressDetail());
        assertEquals("N", address.getIsMailingAddress());
        assertEquals("N", address.getIsNewest());
        assertEquals("N", address.getDelFlag());
    }

    @Test
    void testSettersAndGetters() {
        CifAddress address = new CifAddress();
        address.setSeqNo("A001");
        address.setClientNo("C001");
        address.setAddressType("01");
        address.setAddressDetail("北京市朝阳区");
        address.setIsMailingAddress("Y");
        address.setIsNewest("Y");
        address.setDelFlag("N");

        assertEquals("A001", address.getSeqNo());
        assertEquals("C001", address.getClientNo());
        assertEquals("01", address.getAddressType());
        assertEquals("北京市朝阳区", address.getAddressDetail());
        assertEquals("Y", address.getIsMailingAddress());
        assertEquals("Y", address.getIsNewest());
    }
}
