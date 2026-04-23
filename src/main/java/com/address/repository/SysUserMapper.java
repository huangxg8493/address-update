package com.address.repository;

import com.address.model.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper {
    @Insert("INSERT INTO sys_user(user_id, phone, password, status, create_time, update_time) " +
            "VALUES(#{userId}, #{phone}, #{password}, #{status}, #{createTime}, #{updateTime})")
    void insert(SysUser user);

    @Update("UPDATE sys_user SET phone=#{phone}, password=#{password}, status=#{status}, update_time=#{updateTime} " +
            "WHERE user_id=#{userId}")
    void update(SysUser user);

    @Select("SELECT * FROM sys_user WHERE user_id = #{userId}")
    SysUser findById(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    SysUser findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND status = 'Y'")
    SysUser findActiveByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM sys_user")
    java.util.List<SysUser> findAll();

    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    void deleteById(@Param("userId") Long userId);
}
