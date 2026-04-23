package com.address.service;

import com.address.dto.DataScopeCreateRequest;
import com.address.dto.DataScopeResponse;
import com.address.dto.DataScopeUpdateRequest;
import com.address.model.SysDataScope;
import com.address.repository.SysDataScopeMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataScopeService {

    @Autowired
    private SysDataScopeMapper sysDataScopeMapper;

    public List<DataScopeResponse> query() {
        List<SysDataScope> dataScopes = sysDataScopeMapper.findAll();
        List<DataScopeResponse> responses = new ArrayList<>();
        for (SysDataScope dataScope : dataScopes) {
            responses.add(toResponse(dataScope));
        }
        return responses;
    }

    public DataScopeResponse create(DataScopeCreateRequest request) {
        SysDataScope dataScope = new SysDataScope();
        dataScope.setScopeId(SnowflakeIdGenerator.getInstance().nextId());
        dataScope.setScopeCode(request.getScopeCode());
        dataScope.setScopeName(request.getScopeName());
        dataScope.setScopeType(request.getScopeType());
        dataScope.setCreateTime(LocalDateTime.now());
        sysDataScopeMapper.insert(dataScope);
        return toResponse(dataScope);
    }

    public DataScopeResponse update(DataScopeUpdateRequest request) {
        SysDataScope dataScope = sysDataScopeMapper.findById(request.getScopeId());
        if (dataScope == null) {
            throw new RuntimeException("数据范围不存在");
        }
        if (request.getScopeCode() != null) {
            dataScope.setScopeCode(request.getScopeCode());
        }
        if (request.getScopeName() != null) {
            dataScope.setScopeName(request.getScopeName());
        }
        if (request.getScopeType() != null) {
            dataScope.setScopeType(request.getScopeType());
        }
        sysDataScopeMapper.update(dataScope);
        return toResponse(dataScope);
    }

    public void delete(Long scopeId) {
        sysDataScopeMapper.deleteById(scopeId);
    }

    private DataScopeResponse toResponse(SysDataScope dataScope) {
        if (dataScope == null) {
            return null;
        }
        DataScopeResponse response = new DataScopeResponse();
        response.setScopeId(dataScope.getScopeId());
        response.setScopeCode(dataScope.getScopeCode());
        response.setScopeName(dataScope.getScopeName());
        response.setScopeType(dataScope.getScopeType());
        response.setCreateTime(dataScope.getCreateTime());
        return response;
    }
}
