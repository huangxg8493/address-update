package com.address.dto;

import java.util.List;

public class AddressUpdateRequest {
    private String clientNo;
    private List<AddressItem> addresses;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public List<AddressItem> getAddresses() { return addresses; }
    public void setAddresses(List<AddressItem> addresses) { this.addresses = addresses; }

    public static class AddressItem {
        private String seqNo;
        private String addressType;
        private String addressDetail;
        private String isMailingAddress;
        private String isNewest;

        public String getSeqNo() { return seqNo; }
        public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
        public String getAddressType() { return addressType; }
        public void setAddressType(String addressType) { this.addressType = addressType; }
        public String getAddressDetail() { return addressDetail; }
        public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
        public String getIsMailingAddress() { return isMailingAddress; }
        public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
        public String getIsNewest() { return isNewest; }
        public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    }
}