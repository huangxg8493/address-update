package com.address.repository;

import com.address.model.SysRoleDataScope;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRoleDataScopeMapper {
    @Insert("INSERT INTO SYS_ROLE_DATA_SCOPE(id, role_id, scope_id) VALUES(#{id}, #{roleId}, #{scopeId})")
    void insert(SysRoleDataScope roleDataScope);

    @Delete("DELETE FROM SYS_ROLE_DATA_SCOPE WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM SYS_ROLE_DATA_SCOPE WHERE role_id = #{roleId}")
    java.util.List<SysRoleDataScope> findByRoleId(@Param("roleId") Long roleId);
}
