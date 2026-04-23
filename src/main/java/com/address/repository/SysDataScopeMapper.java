package com.address.repository;

import com.address.model.SysDataScope;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysDataScopeMapper {
    @Insert("INSERT INTO sys_data_scope(scope_id, scope_code, scope_name, scope_type, create_time) " +
            "VALUES(#{scopeId}, #{scopeCode}, #{scopeName}, #{scopeType}, #{createTime})")
    void insert(SysDataScope dataScope);

    @Update("UPDATE sys_data_scope SET scope_code=#{scopeCode}, scope_name=#{scopeName}, " +
            "scope_type=#{scopeType} WHERE scope_id=#{scopeId}")
    void update(SysDataScope dataScope);

    @Select("SELECT scope_id AS scopeId, scope_code AS scopeCode, scope_name AS scopeName, scope_type AS scopeType, create_time AS createTime FROM sys_data_scope WHERE scope_id = #{scopeId}")
    SysDataScope findById(@Param("scopeId") Long scopeId);

    @Select("SELECT * FROM sys_data_scope")
    java.util.List<SysDataScope> findAll();

    @Delete("DELETE FROM sys_data_scope WHERE scope_id = #{scopeId}")
    void deleteById(@Param("scopeId") Long scopeId);
}
