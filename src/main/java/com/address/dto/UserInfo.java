package com.address.dto;

public class UserInfo {
    private Long userId;
    private String userName;
    private String email;
    private String city;
    private String addrDetail;
    private String hobby;
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddrDetail() { return addrDetail; }
    public void setAddrDetail(String addrDetail) { this.addrDetail = addrDetail; }
    public String getHobby() { return hobby; }
    public void setHobby(String hobby) { this.hobby = hobby; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
