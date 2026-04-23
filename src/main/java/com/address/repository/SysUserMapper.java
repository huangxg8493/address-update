package com.address.repository;

import com.address.model.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper {
    @Insert("INSERT INTO sys_user(user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time) " +
            "VALUES(#{userId}, #{phone}, #{password}, #{status}, #{userName}, #{email}, #{province}, #{city}, #{district}, #{hobby}, #{createTime}, #{updateTime})")
    void insert(SysUser user);

    @Update("UPDATE sys_user SET phone=#{phone}, password=#{password}, status=#{status}, " +
            "user_name=#{userName}, email=#{email}, province=#{province}, city=#{city}, district=#{district}, hobby=#{hobby}, " +
            "update_time=#{updateTime} WHERE user_id=#{userId}")
    void update(SysUser user);

    @Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user WHERE user_id = #{userId}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "province", column = "province"),
        @Result(property = "city", column = "city"),
        @Result(property = "district", column = "district"),
        @Result(property = "hobby", column = "hobby"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    SysUser findById(@Param("userId") Long userId);

    @Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user WHERE phone = #{phone}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "province", column = "province"),
        @Result(property = "city", column = "city"),
        @Result(property = "district", column = "district"),
        @Result(property = "hobby", column = "hobby"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    SysUser findByPhone(@Param("phone") String phone);

    @Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user WHERE phone = #{phone} AND status = 'Y'")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "province", column = "province"),
        @Result(property = "city", column = "city"),
        @Result(property = "district", column = "district"),
        @Result(property = "hobby", column = "hobby"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    SysUser findActiveByPhone(@Param("phone") String phone);

    @Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "province", column = "province"),
        @Result(property = "city", column = "city"),
        @Result(property = "district", column = "district"),
        @Result(property = "hobby", column = "hobby"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    java.util.List<SysUser> findAll();

    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    void deleteById(@Param("userId") Long userId);
}
