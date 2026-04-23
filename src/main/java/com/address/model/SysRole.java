package com.address.model;

import java.time.LocalDateTime;

/**
 * 系统角色实体
 */
public class SysRole {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;  // Y-启用，N-禁用
    private LocalDateTime createTime;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
