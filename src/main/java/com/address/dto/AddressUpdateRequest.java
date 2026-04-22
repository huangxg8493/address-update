package com.address.dto;

import com.address.model.CifAddress;
import java.util.List;

public class AddressUpdateRequest {
    private String clientNo;
    private List<CifAddress> addresses;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public List<CifAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CifAddress> addresses) { this.addresses = addresses; }
}