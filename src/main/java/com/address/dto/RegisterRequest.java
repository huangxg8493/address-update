package com.address.dto;

public class RegisterRequest {
    private String phone;
    private String password;
    private String userName;
    private String email;
    private String city;
    private String addrDetail;
    private String hobby;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
