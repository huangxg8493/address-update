package com.address.service;

import com.address.dto.RoleCreateRequest;
import com.address.dto.RoleQueryRequest;
import com.address.dto.RoleResponse;
import com.address.dto.RoleUpdateRequest;
import com.address.model.SysRole;
import com.address.model.SysRoleDataScope;
import com.address.model.SysRolePermission;
import com.address.repository.SysRoleMapper;
import com.address.repository.SysRolePermissionMapper;
import com.address.repository.SysRoleDataScopeMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private SysRoleDataScopeMapper sysRoleDataScopeMapper;

    public List<RoleResponse> query(RoleQueryRequest request) {
        List<SysRole> roles = sysRoleMapper.findAll();
        List<RoleResponse> responses = new ArrayList<>();
        for (SysRole role : roles) {
            if (request.getRoleCode() != null && !request.getRoleCode().equals(role.getRoleCode())) {
                continue;
            }
            if (request.getRoleName() != null && !request.getRoleName().contains(role.getRoleName())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(role.getStatus())) {
                continue;
            }
            responses.add(toResponse(role));
        }
        return responses;
    }

    public RoleResponse create(RoleCreateRequest request) {
        SysRole existRole = sysRoleMapper.findByCode(request.getRoleCode());
        if (existRole != null) {
            throw new RuntimeException("角色代码已存在");
        }
        SysRole role = new SysRole();
        role.setRoleId(SnowflakeIdGenerator.getInstance().nextId());
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        return toResponse(role);
    }

    public RoleResponse update(RoleUpdateRequest request) {
        SysRole role = sysRoleMapper.findById(request.getRoleId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (request.getRoleCode() != null) {
            role.setRoleCode(request.getRoleCode());
        }
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        sysRoleMapper.update(role);
        return toResponse(role);
    }

    public void delete(Long roleId) {
        sysRoleMapper.deleteById(roleId);
    }

    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.deleteByRoleId(roleId);
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setId(SnowflakeIdGenerator.getInstance().nextId());
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            sysRolePermissionMapper.insert(rp);
        }
    }

    public void assignDataScopes(Long roleId, List<Long> scopeIds) {
        sysRoleDataScopeMapper.deleteByRoleId(roleId);
        for (Long scopeId : scopeIds) {
            SysRoleDataScope rds = new SysRoleDataScope();
            rds.setId(SnowflakeIdGenerator.getInstance().nextId());
            rds.setRoleId(roleId);
            rds.setScopeId(scopeId);
            sysRoleDataScopeMapper.insert(rds);
        }
    }

    private RoleResponse toResponse(SysRole role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getRoleId());
        response.setRoleCode(role.getRoleCode());
        response.setRoleName(role.getRoleName());
        response.setStatus(role.getStatus());
        response.setCreateTime(role.getCreateTime());
        return response;
    }
}
