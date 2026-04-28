package com.address.repository;

import com.address.model.SysUserRole;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserRoleMapper {
    @Insert("INSERT INTO SYS_USER_ROLE(id, user_id, role_id) VALUES(#{id}, #{userId}, #{roleId})")
    void insert(SysUserRole userRole);

    @Delete("DELETE FROM SYS_USER_ROLE WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Select("SELECT id, user_id, role_id FROM SYS_USER_ROLE WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "roleId", column = "role_id")
    })
    java.util.List<SysUserRole> findByUserId(@Param("userId") Long userId);
}
