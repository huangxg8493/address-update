package com.address.dto;

import java.util.List;

public class AddressQueryResponse {
    private String clientNo;
    private int pageNum;
    private int pageSize;
    private long total;
    private int totalPages;
    private List<AddressItem> list;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public List<AddressItem> getList() { return list; }
    public void setList(List<AddressItem> list) { this.list = list; }

    public static class AddressItem {
        private String seqNo;
        private String addressType;
        private String addressDetail;
        private String lastChangeDate;
        private String isMailingAddress;
        private String isNewest;

        public String getSeqNo() { return seqNo; }
        public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
        public String getAddressType() { return addressType; }
        public void setAddressType(String addressType) { this.addressType = addressType; }
        public String getAddressDetail() { return addressDetail; }
        public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
        public String getLastChangeDate() { return lastChangeDate; }
        public void setLastChangeDate(String lastChangeDate) { this.lastChangeDate = lastChangeDate; }
        public String getIsMailingAddress() { return isMailingAddress; }
        public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
        public String getIsNewest() { return isNewest; }
        public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    }
}
