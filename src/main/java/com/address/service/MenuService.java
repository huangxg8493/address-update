package com.address.service;

import com.address.dto.*;
import com.address.model.SysMenu;
import com.address.repository.SysMenuMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    public List<MenuResponse> query(MenuQueryRequest request) {
        List<SysMenu> menus = sysMenuMapper.findAll();
        List<MenuResponse> responses = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (request.getMenuName() != null && !request.getMenuName().equals(menu.getMenuName())) {
                continue;
            }
            if (request.getMenuUrl() != null && !request.getMenuUrl().equals(menu.getMenuUrl())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(menu.getStatus())) {
                continue;
            }
            if (request.getParentId() != null && !request.getParentId().equals(menu.getParentId())) {
                continue;
            }
            responses.add(toResponse(menu));
        }
        return responses;
    }

    public MenuResponse getById(Long menuId) {
        SysMenu menu = sysMenuMapper.findById(menuId);
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        return toResponse(menu);
    }

    public MenuResponse create(MenuCreateRequest request) {
        if (request.getMenuName() == null || request.getMenuName().trim().isEmpty()) {
            throw new RuntimeException("菜单名称不能为空");
        }
        if (request.getMenuUrl() == null || request.getMenuUrl().trim().isEmpty()) {
            throw new RuntimeException("菜单URL不能为空");
        }
        SysMenu menu = new SysMenu();
        menu.setMenuId(SnowflakeIdGenerator.getInstance().nextId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuUrl(request.getMenuUrl());
        menu.setIcon(request.getIcon());
        menu.setSortOrder(request.getSortOrder());
        menu.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        menu.setComponent(request.getComponent());
        menu.setComponentPath(request.getComponentPath());
        menu.setParentId(request.getParentId());
        menu.setDelFlag("N");
        menu.setCreateTime(LocalDateTime.now());
        menu.setLevelDepth(calculateLevelDepth(request.getParentId()));
        menu.setIsLeaf("Y");
        sysMenuMapper.insert(menu);
        updateParentLeafStatus(request.getParentId());
        return toResponse(menu);
    }

    public MenuResponse update(MenuUpdateRequest request) {
        SysMenu menu = sysMenuMapper.findById(request.getMenuId());
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        if (request.getMenuName() != null) {
            menu.setMenuName(request.getMenuName());
        }
        if (request.getMenuUrl() != null) {
            menu.setMenuUrl(request.getMenuUrl());
        }
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            menu.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            menu.setStatus(request.getStatus());
        }
        if (request.getComponent() != null) {
            menu.setComponent(request.getComponent());
        }
        if (request.getComponentPath() != null) {
            menu.setComponentPath(request.getComponentPath());
        }
        if (request.getParentId() != null) {
            menu.setParentId(request.getParentId());
            menu.setLevelDepth(calculateLevelDepth(request.getParentId()));
        }
        sysMenuMapper.update(menu);
        return toResponse(menu);
    }

    public void delete(Long menuId) {
        SysMenu menu = sysMenuMapper.findById(menuId);
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        sysMenuMapper.deleteById(menuId);
        updateParentLeafStatus(menu.getParentId());
    }

    public List<MenuTreeResponse> getTree() {
        List<SysMenu> allMenus = sysMenuMapper.findAll();
        return buildTree(allMenus, null);
    }

    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
    }

    private int calculateLevelDepth(Long parentId) {
        if (parentId == null) {
            return 1;
        }
        SysMenu parent = sysMenuMapper.findById(parentId);
        if (parent == null) {
            return 1;
        }
        return parent.getLevelDepth() + 1;
    }

    private void updateParentLeafStatus(Long parentId) {
        if (parentId == null) {
            return;
        }
        List<SysMenu> children = sysMenuMapper.findByParentId(parentId);
        SysMenu parent = sysMenuMapper.findById(parentId);
        if (parent != null) {
            parent.setIsLeaf(children.isEmpty() ? "Y" : "N");
            sysMenuMapper.update(parent);
        }
    }

    private List<MenuTreeResponse> buildTree(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
            .filter(m -> (parentId == null && m.getParentId() == null) || (parentId != null && parentId.equals(m.getParentId())))
            .map(m -> {
                MenuTreeResponse response = toTreeResponse(m);
                List<MenuTreeResponse> children = buildTree(allMenus, m.getMenuId());
                response.setChildren(children.isEmpty() ? null : children);
                return response;
            })
            .collect(Collectors.toList());
    }

    private MenuResponse toResponse(SysMenu menu) {
        MenuResponse response = new MenuResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setMenuUrl(menu.getMenuUrl());
        response.setIcon(menu.getIcon());
        response.setSortOrder(menu.getSortOrder());
        response.setStatus(menu.getStatus());
        response.setIsLeaf(menu.getIsLeaf());
        response.setLevelDepth(menu.getLevelDepth());
        response.setComponent(menu.getComponent());
        response.setComponentPath(menu.getComponentPath());
        response.setParentId(menu.getParentId());
        response.setDelFlag(menu.getDelFlag());
        response.setCreateTime(menu.getCreateTime());
        return response;
    }

    private MenuTreeResponse toTreeResponse(SysMenu menu) {
        MenuTreeResponse response = new MenuTreeResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setMenuUrl(menu.getMenuUrl());
        response.setIcon(menu.getIcon());
        response.setSortOrder(menu.getSortOrder());
        response.setStatus(menu.getStatus());
        response.setIsLeaf(menu.getIsLeaf());
        response.setLevelDepth(menu.getLevelDepth());
        response.setComponent(menu.getComponent());
        response.setComponentPath(menu.getComponentPath());
        response.setParentId(menu.getParentId());
        return response;
    }
}
