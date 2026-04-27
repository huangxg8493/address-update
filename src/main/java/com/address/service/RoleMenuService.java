package com.address.service;

import com.address.model.SysMenu;
import com.address.model.SysRoleMenu;
import com.address.repository.SysRoleMenuMapper;
import com.address.repository.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            rm.setCreateTime(LocalDateTime.now());
            roleMenus.add(rm);
        }
        sysRoleMenuMapper.insertBatch(roleMenus);
    }

    public List<SysMenu> getMenusByRoleId(Long roleId) {
        List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysMenu> menus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysMenu menu = sysMenuMapper.findById(menuId);
            if (menu != null && !"Y".equals(menu.getDelFlag())) {
                menus.add(menu);
            }
        }
        return menus;
    }
}