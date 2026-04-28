package com.address.repository;

import com.address.model.SysRole;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRoleMapper {
    @Insert("INSERT INTO SYS_ROLE(role_id, role_code, role_name, status, create_time) " +
            "VALUES(#{roleId}, #{roleCode}, #{roleName}, #{status}, #{createTime})")
    void insert(SysRole role);

    @Update("UPDATE SYS_ROLE SET role_code=#{roleCode}, role_name=#{roleName}, status=#{status} WHERE role_id=#{roleId}")
    void update(SysRole role);

    @Select("SELECT * FROM SYS_ROLE WHERE role_id = #{roleId}")
    SysRole findById(@Param("roleId") Long roleId);

    @Select("SELECT * FROM SYS_ROLE WHERE role_code = #{roleCode}")
    SysRole findByCode(@Param("roleCode") String roleCode);

    @Select("SELECT * FROM SYS_ROLE")
    java.util.List<SysRole> findAll();

    @Select("<script>SELECT role_id, role_code, role_name, status, create_time FROM SYS_ROLE WHERE role_id IN <foreach collection='roleIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    @Results({
        @Result(property = "roleId", column = "role_id"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time")
    })
    java.util.List<SysRole> findByIds(@Param("roleIds") java.util.List<Long> roleIds);

    @Delete("DELETE FROM SYS_ROLE WHERE role_id = #{roleId}")
    void deleteById(@Param("roleId") Long roleId);
}
