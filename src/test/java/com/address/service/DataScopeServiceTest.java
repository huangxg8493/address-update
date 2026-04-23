package com.address.service;

import com.address.dto.DataScopeCreateRequest;
import com.address.dto.DataScopeResponse;
import com.address.dto.DataScopeUpdateRequest;
import com.address.model.SysDataScope;
import com.address.repository.SysDataScopeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataScopeServiceTest {

    @Autowired
    private DataScopeService dataScopeService;

    @Autowired
    private SysDataScopeMapper sysDataScopeMapper;

    @Test
    public void testCreate() {
        DataScopeCreateRequest request = new DataScopeCreateRequest();
        request.setScopeCode("SCOPE_" + System.currentTimeMillis());
        request.setScopeName("测试数据范围");
        request.setScopeType("OWN");

        DataScopeResponse response = dataScopeService.create(request);

        assertNotNull(response);
        assertNotNull(response.getScopeId());
        assertEquals("测试数据范围", response.getScopeName());
        assertEquals("OWN", response.getScopeType());
    }

    @Test
    public void testQuery() {
        SysDataScope dataScope = new SysDataScope();
        dataScope.setScopeId(System.currentTimeMillis());
        dataScope.setScopeCode("SCOPE_QUERY_" + System.currentTimeMillis());
        dataScope.setScopeName("查询测试数据范围");
        dataScope.setScopeType("DEPT");
        dataScope.setCreateTime(LocalDateTime.now());
        sysDataScopeMapper.insert(dataScope);

        List<DataScopeResponse> responses = dataScopeService.query();

        assertFalse(responses.isEmpty());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        SysDataScope dataScope = new SysDataScope();
        dataScope.setScopeId(System.currentTimeMillis());
        dataScope.setScopeCode("SCOPE_UPDATE_" + System.currentTimeMillis());
        dataScope.setScopeName("更新前数据范围");
        dataScope.setScopeType("OWN");
        dataScope.setCreateTime(LocalDateTime.now());
        sysDataScopeMapper.insert(dataScope);

        DataScopeUpdateRequest request = new DataScopeUpdateRequest();
        request.setScopeId(dataScope.getScopeId());
        request.setScopeName("更新后数据范围");
        request.setScopeType("ALL");

        DataScopeResponse response = dataScopeService.update(request);

        assertEquals("更新后数据范围", response.getScopeName());
        assertEquals("ALL", response.getScopeType());
    }

    @Test
    public void testDelete() {
        SysDataScope dataScope = new SysDataScope();
        dataScope.setScopeId(System.currentTimeMillis());
        dataScope.setScopeCode("SCOPE_DELETE_" + System.currentTimeMillis());
        dataScope.setScopeName("删除测试数据范围");
        dataScope.setScopeType("OWN");
        dataScope.setCreateTime(LocalDateTime.now());
        sysDataScopeMapper.insert(dataScope);

        dataScopeService.delete(dataScope.getScopeId());

        SysDataScope deleted = sysDataScopeMapper.findById(dataScope.getScopeId());
        assertNull(deleted);
    }
}
