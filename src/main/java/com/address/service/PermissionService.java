package com.address.service;

import com.address.dto.PermissionCreateRequest;
import com.address.dto.PermissionResponse;
import com.address.dto.PermissionUpdateRequest;
import com.address.model.SysPermission;
import com.address.repository.SysPermissionMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    public List<PermissionResponse> query() {
        List<SysPermission> permissions = sysPermissionMapper.findAll();
        List<PermissionResponse> responses = new ArrayList<>();
        for (SysPermission permission : permissions) {
            responses.add(toResponse(permission));
        }
        return responses;
    }

    public PermissionResponse create(PermissionCreateRequest request) {
        SysPermission exist = sysPermissionMapper.findByCode(request.getPermissionCode());
        if (exist != null) {
            throw new RuntimeException("权限代码已存在");
        }
        SysPermission permission = new SysPermission();
        permission.setPermissionId(SnowflakeIdGenerator.getInstance().nextId());
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setMenuUrl(request.getMenuUrl());
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionMapper.insert(permission);
        return toResponse(permission);
    }

    public PermissionResponse update(PermissionUpdateRequest request) {
        SysPermission permission = sysPermissionMapper.findById(request.getPermissionId());
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        if (request.getPermissionCode() != null) {
            permission.setPermissionCode(request.getPermissionCode());
        }
        if (request.getPermissionName() != null) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (request.getMenuUrl() != null) {
            permission.setMenuUrl(request.getMenuUrl());
        }
        sysPermissionMapper.update(permission);
        return toResponse(permission);
    }

    public void delete(Long permissionId) {
        sysPermissionMapper.deleteById(permissionId);
    }

    private PermissionResponse toResponse(SysPermission permission) {
        if (permission == null) {
            return null;
        }
        PermissionResponse response = new PermissionResponse();
        response.setPermissionId(permission.getPermissionId());
        response.setPermissionCode(permission.getPermissionCode());
        response.setPermissionName(permission.getPermissionName());
        response.setMenuUrl(permission.getMenuUrl());
        response.setCreateTime(permission.getCreateTime());
        return response;
    }
}
