package com.address.repository;

import com.address.model.SysPermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysPermissionMapper {
    @Insert("INSERT INTO sys_permission(permission_id, permission_code, permission_name, menu_url, create_time) " +
            "VALUES(#{permissionId}, #{permissionCode}, #{permissionName}, #{menuUrl}, #{createTime})")
    void insert(SysPermission permission);

    @Update("UPDATE sys_permission SET permission_code=#{permissionCode}, permission_name=#{permissionName}, " +
            "menu_url=#{menuUrl} WHERE permission_id=#{permissionId}")
    void update(SysPermission permission);

    @Select("SELECT * FROM sys_permission WHERE permission_id = #{permissionId}")
    SysPermission findById(@Param("permissionId") Long permissionId);

    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode}")
    SysPermission findByCode(@Param("permissionCode") String permissionCode);

    @Select("SELECT * FROM sys_permission")
    java.util.List<SysPermission> findAll();

    @Delete("DELETE FROM sys_permission WHERE permission_id = #{permissionId}")
    void deleteById(@Param("permissionId") Long permissionId);
}
