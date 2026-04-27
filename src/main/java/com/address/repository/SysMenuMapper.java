package com.address.repository;

import com.address.model.SysMenu;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysMenuMapper {
    @Insert("INSERT INTO SYS_MENU(menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time) " +
            "VALUES(#{menuId}, #{menuName}, #{menuUrl}, #{icon}, #{sortOrder}, #{status}, #{isLeaf}, #{levelDepth}, #{component}, #{componentPath}, #{parentId}, #{delFlag}, #{createTime})")
    void insert(SysMenu menu);

    @Update("UPDATE SYS_MENU SET menu_name=#{menuName}, menu_url=#{menuUrl}, icon=#{icon}, sort_order=#{sortOrder}, status=#{status}, component=#{component}, component_path=#{componentPath}, parent_id=#{parentId}, is_leaf=#{isLeaf}, level_depth=#{levelDepth} WHERE menu_id=#{menuId}")
    void update(SysMenu menu);

    @Update("UPDATE SYS_MENU SET del_flag='Y' WHERE menu_id=#{menuId}")
    void deleteById(@Param("menuId") Long menuId);

    @Select("SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time FROM SYS_MENU WHERE menu_id = #{menuId} AND del_flag='N'")
    @Results({
        @Result(property = "menuId", column = "menu_id"),
        @Result(property = "menuName", column = "menu_name"),
        @Result(property = "menuUrl", column = "menu_url"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "status", column = "status"),
        @Result(property = "isLeaf", column = "is_leaf"),
        @Result(property = "levelDepth", column = "level_depth"),
        @Result(property = "component", column = "component"),
        @Result(property = "componentPath", column = "component_path"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "delFlag", column = "del_flag"),
        @Result(property = "createTime", column = "create_time")
    })
    SysMenu findById(@Param("menuId") Long menuId);

    @Select("SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time FROM SYS_MENU WHERE del_flag='N'")
    @Results({
        @Result(property = "menuId", column = "menu_id"),
        @Result(property = "menuName", column = "menu_name"),
        @Result(property = "menuUrl", column = "menu_url"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "status", column = "status"),
        @Result(property = "isLeaf", column = "is_leaf"),
        @Result(property = "levelDepth", column = "level_depth"),
        @Result(property = "component", column = "component"),
        @Result(property = "componentPath", column = "component_path"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "delFlag", column = "del_flag"),
        @Result(property = "createTime", column = "create_time")
    })
    List<SysMenu> findAll();

    @Select("SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time FROM SYS_MENU WHERE parent_id = #{parentId} AND del_flag='N'")
    @Results({
        @Result(property = "menuId", column = "menu_id"),
        @Result(property = "menuName", column = "menu_name"),
        @Result(property = "menuUrl", column = "menu_url"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "status", column = "status"),
        @Result(property = "isLeaf", column = "is_leaf"),
        @Result(property = "levelDepth", column = "level_depth"),
        @Result(property = "component", column = "component"),
        @Result(property = "componentPath", column = "component_path"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "delFlag", column = "del_flag"),
        @Result(property = "createTime", column = "create_time")
    })
    List<SysMenu> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time FROM SYS_MENU WHERE parent_id IS NULL AND del_flag='N'")
    @Results({
        @Result(property = "menuId", column = "menu_id"),
        @Result(property = "menuName", column = "menu_name"),
        @Result(property = "menuUrl", column = "menu_url"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "status", column = "status"),
        @Result(property = "isLeaf", column = "is_leaf"),
        @Result(property = "levelDepth", column = "level_depth"),
        @Result(property = "component", column = "component"),
        @Result(property = "componentPath", column = "component_path"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "delFlag", column = "del_flag"),
        @Result(property = "createTime", column = "create_time")
    })
    List<SysMenu> findRootMenus();

    @Select("<script>" +
            "SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time " +
            "FROM SYS_MENU m WHERE menu_id IN (" +
            "  SELECT DISTINCT rm.menu_id FROM SYS_ROLE_MENU rm WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "    #{id}" +
            "</foreach>" +
            ") AND del_flag='N' ORDER BY sort_order" +
            "</script>")
    @Results({
        @Result(property = "menuId", column = "menu_id"),
        @Result(property = "menuName", column = "menu_name"),
        @Result(property = "menuUrl", column = "menu_url"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "status", column = "status"),
        @Result(property = "isLeaf", column = "is_leaf"),
        @Result(property = "levelDepth", column = "level_depth"),
        @Result(property = "component", column = "component"),
        @Result(property = "componentPath", column = "component_path"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "delFlag", column = "del_flag"),
        @Result(property = "createTime", column = "create_time")
    })
    List<SysMenu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
}
