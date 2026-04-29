package com.address.dto;

public class UserCreateRequest {
    private String phone;
    private String password;
    private String status;
    private String userName;    // 用户名称
    private String email;       // 邮箱
    private String city;         // 省市区组合，如"广东省深圳市南山区"
    private String addrDetail;  // 详细地址，如"科技园1号路101室"
    private String hobby;       // 业余爱好

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
}
