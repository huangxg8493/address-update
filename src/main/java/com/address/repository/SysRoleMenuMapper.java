package com.address.repository;

import com.address.model.SysRoleMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper {

    @Delete("DELETE FROM SYS_ROLE_MENU WHERE ROLE_ID = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Insert("<script>" +
            "INSERT INTO SYS_ROLE_MENU (ROLE_ID, MENU_ID, CREATE_TIME) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.roleId}, #{item.menuId}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<SysRoleMenu> roleMenus);

    @Select("SELECT MENU_ID FROM SYS_ROLE_MENU WHERE ROLE_ID = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
}