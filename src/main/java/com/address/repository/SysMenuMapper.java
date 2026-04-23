package com.address.repository;

import com.address.model.SysMenu;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysMenuMapper {
    @Insert("INSERT INTO sys_menu(menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time) " +
            "VALUES(#{menuId}, #{menuName}, #{menuUrl}, #{icon}, #{sortOrder}, #{status}, #{isLeaf}, #{levelDepth}, #{component}, #{componentPath}, #{parentId}, #{delFlag}, #{createTime})")
    void insert(SysMenu menu);

    @Update("UPDATE sys_menu SET menu_name=#{menuName}, menu_url=#{menuUrl}, icon=#{icon}, sort_order=#{sortOrder}, status=#{status}, component=#{component}, component_path=#{componentPath}, parent_id=#{parentId}, is_leaf=#{isLeaf}, level_depth=#{levelDepth} WHERE menu_id=#{menuId}")
    void update(SysMenu menu);

    @Update("UPDATE sys_menu SET del_flag='Y' WHERE menu_id=#{menuId}")
    void deleteById(@Param("menuId") Long menuId);

    @Select("SELECT * FROM sys_menu WHERE menu_id = #{menuId} AND del_flag='N'")
    SysMenu findById(@Param("menuId") Long menuId);

    @Select("SELECT * FROM sys_menu WHERE del_flag='N'")
    java.util.List<SysMenu> findAll();

    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND del_flag='N'")
    java.util.List<SysMenu> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_menu WHERE parent_id IS NULL AND del_flag='N'")
    java.util.List<SysMenu> findRootMenus();
}
