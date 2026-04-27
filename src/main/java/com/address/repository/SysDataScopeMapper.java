package com.address.repository;

import com.address.model.SysDataScope;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysDataScopeMapper {
    @Insert("INSERT INTO SYS_DATA_SCOPE(scope_id, scope_code, scope_name, scope_type, create_time) " +
            "VALUES(#{scopeId}, #{scopeCode}, #{scopeName}, #{scopeType}, #{createTime})")
    void insert(SysDataScope dataScope);

    @Update("UPDATE SYS_DATA_SCOPE SET scope_code=#{scopeCode}, scope_name=#{scopeName}, " +
            "scope_type=#{scopeType} WHERE scope_id=#{scopeId}")
    void update(SysDataScope dataScope);

    @Select("SELECT scope_id AS scopeId, scope_code AS scopeCode, scope_name AS scopeName, scope_type AS scopeType, create_time AS createTime FROM SYS_DATA_SCOPE WHERE scope_id = #{scopeId}")
    SysDataScope findById(@Param("scopeId") Long scopeId);

    @Select("SELECT * FROM SYS_DATA_SCOPE")
    java.util.List<SysDataScope> findAll();

    @Delete("DELETE FROM SYS_DATA_SCOPE WHERE scope_id = #{scopeId}")
    void deleteById(@Param("scopeId") Long scopeId);
}
