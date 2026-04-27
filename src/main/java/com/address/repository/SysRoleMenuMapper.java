package com.address.repository;

import com.address.model.SysRoleMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper {

    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Insert("<script>" +
            "INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.roleId}, #{item.menuId}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<SysRoleMenu> roleMenus);

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
}