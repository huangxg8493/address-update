package com.address.model;

/**
 * 角色数据范围关联实体
 */
public class SysRoleDataScope {
    private Long id;
    private Long roleId;
    private Long scopeId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getScopeId() { return scopeId; }
    public void setScopeId(Long scopeId) { this.scopeId = scopeId; }
}
