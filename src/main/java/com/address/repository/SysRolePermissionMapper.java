package com.address.repository;

import com.address.model.SysRolePermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRolePermissionMapper {
    @Insert("INSERT INTO SYS_ROLE_PERMISSION(id, role_id, permission_id) VALUES(#{id}, #{roleId}, #{permissionId})")
    void insert(SysRolePermission rolePermission);

    @Delete("DELETE FROM SYS_ROLE_PERMISSION WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT id, role_id, permission_id FROM SYS_ROLE_PERMISSION WHERE role_id = #{roleId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleId", column = "role_id"),
        @Result(property = "permissionId", column = "permission_id")
    })
    java.util.List<SysRolePermission> findByRoleId(@Param("roleId") Long roleId);
}
