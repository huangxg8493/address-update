package com.address.repository;

import com.address.model.SysRolePermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRolePermissionMapper {
    @Insert("INSERT INTO sys_role_permission(id, role_id, permission_id) VALUES(#{id}, #{roleId}, #{permissionId})")
    void insert(SysRolePermission rolePermission);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId}")
    java.util.List<SysRolePermission> findByRoleId(@Param("roleId") Long roleId);
}
