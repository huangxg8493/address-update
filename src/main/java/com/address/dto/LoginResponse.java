package com.address.dto;

import java.util.ArrayList;
import java.util.List;

public class LoginResponse {
    private String token;
    private String phone;
    private UserInfo user;
    private List<RoleInfo> roles = new ArrayList<>();
    private List<MenuTreeDTO> menus = new ArrayList<>();

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public List<RoleInfo> getRoles() { return roles; }
    public void setRoles(List<RoleInfo> roles) { this.roles = roles; }
    public List<MenuTreeDTO> getMenus() { return menus; }
    public void setMenus(List<MenuTreeDTO> menus) { this.menus = menus; }
}
