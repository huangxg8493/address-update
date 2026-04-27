package com.address.repository;

import com.address.model.SysPermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysPermissionMapper {
    @Insert("INSERT INTO SYS_PERMISSION(permission_id, permission_code, permission_name, menu_url, create_time) " +
            "VALUES(#{permissionId}, #{permissionCode}, #{permissionName}, #{menuUrl}, #{createTime})")
    void insert(SysPermission permission);

    @Update("UPDATE SYS_PERMISSION SET permission_code=#{permissionCode}, permission_name=#{permissionName}, " +
            "menu_url=#{menuUrl} WHERE permission_id=#{permissionId}")
    void update(SysPermission permission);

    @Select("SELECT permission_id AS permissionId, permission_code AS permissionCode, permission_name AS permissionName, menu_url AS menuUrl, create_time AS createTime FROM SYS_PERMISSION WHERE permission_id = #{permissionId}")
    SysPermission findById(@Param("permissionId") Long permissionId);

    @Select("SELECT * FROM SYS_PERMISSION WHERE permission_code = #{permissionCode}")
    SysPermission findByCode(@Param("permissionCode") String permissionCode);

    @Select("SELECT * FROM SYS_PERMISSION")
    java.util.List<SysPermission> findAll();

    @Delete("DELETE FROM SYS_PERMISSION WHERE permission_id = #{permissionId}")
    void deleteById(@Param("permissionId") Long permissionId);
}
