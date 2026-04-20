package com.address.model;

import java.util.Date;

public class CifAddress {
    private String seqNo;
    private String clientNo;
    private String addressType;
    private String addressDetail;
    private Date lastChangeDate;
    private String isMailingAddress;
    private String isNewest;
    private String delFlag;

    public CifAddress() {
    }

    public CifAddress(String clientNo, String addressType, String addressDetail) {
        this.clientNo = clientNo;
        this.addressType = addressType;
        this.addressDetail = addressDetail;
        this.isMailingAddress = "N";
        this.isNewest = "N";
        this.delFlag = "N";
    }

    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public Date getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(Date lastChangeDate) { this.lastChangeDate = lastChangeDate; }
    public String getIsMailingAddress() { return isMailingAddress; }
    public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
    public String getIsNewest() { return isNewest; }
    public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return "CifAddress{" +
                "seqNo='" + seqNo + '\'' +
                ", clientNo='" + clientNo + '\'' +
                ", addressType='" + addressType + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", isMailingAddress='" + isMailingAddress + '\'' +
                ", isNewest='" + isNewest + '\'' +
                '}';
    }
}
