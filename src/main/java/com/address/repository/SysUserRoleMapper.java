package com.address.repository;

import com.address.model.SysUserRole;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserRoleMapper {
    @Insert("INSERT INTO sys_user_role(id, user_id, role_id) VALUES(#{id}, #{userId}, #{roleId})")
    void insert(SysUserRole userRole);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId}")
    java.util.List<SysUserRole> findByUserId(@Param("userId") Long userId);
}
