package com.address.dto;

import java.time.LocalDateTime;

public class DataScopeResponse {
    private Long scopeId;
    private String scopeCode;
    private String scopeName;
    private String scopeType;
    private LocalDateTime createTime;

    public Long getScopeId() { return scopeId; }
    public void setScopeId(Long scopeId) { this.scopeId = scopeId; }
    public String getScopeCode() { return scopeCode; }
    public void setScopeCode(String scopeCode) { this.scopeCode = scopeCode; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
    public String getScopeType() { return scopeType; }
    public void setScopeType(String scopeType) { this.scopeType = scopeType; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
