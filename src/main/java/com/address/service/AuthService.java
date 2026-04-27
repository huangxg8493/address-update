package com.address.service;

import com.address.common.ErrorCode;
import com.address.dto.*;
import com.address.model.*;
import com.address.repository.*;
import com.address.security.JwtUtil;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResult login(LoginRequest request) {
        SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
        if (user == null) {
            SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
            if (existUser == null) {
                return LoginResult.error(ErrorCode.USER_NOT_FOUND, "用户未注册");
            } else {
                return LoginResult.error(ErrorCode.USER_DISABLED, "用户已禁用");
            }
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResult.error(ErrorCode.PASSWORD_ERROR, "密码错误");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());

        // 获取用户角色
        List<RoleInfo> roles = getRolesByUserId(user.getUserId());

        // 获取用户菜单树
        List<MenuTreeDTO> menus = getMenusByUserId(user.getUserId());

        // 构建完整响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setPhone(user.getPhone());
        response.setUser(buildUserInfo(user));
        response.setRoles(roles);
        response.setMenus(menus);

        return LoginResult.success(response);
    }

    public LoginResult register(RegisterRequest request) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser != null) {
            return LoginResult.error(ErrorCode.PHONE_ALREADY_EXISTS, "手机号已注册");
        }
        if (!isValidPhone(request.getPhone())) {
            return LoginResult.error(ErrorCode.PHONE_FORMAT_ERROR, "手机号格式错误");
        }
        if (!isValidPassword(request.getPassword())) {
            return LoginResult.error(ErrorCode.PASSWORD_INVALID, "密码格式错误");
        }
        SysUser user = new SysUser();
        user.setUserId(SnowflakeIdGenerator.getInstance().nextId());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("Y");
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setProvince(request.getProvince());
        user.setCity(request.getCity());
        user.setDistrict(request.getDistrict());
        user.setHobby(request.getHobby());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return LoginResult.success(null, request.getPhone());
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private List<RoleInfo> getRolesByUserId(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.findByUserId(userId);
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<RoleInfo> result = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysRole role = sysRoleMapper.findById(roleId);
            if (role != null && "Y".equals(role.getStatus())) {
                RoleInfo info = new RoleInfo();
                info.setRoleId(role.getRoleId());
                info.setRoleCode(role.getRoleCode());
                info.setRoleName(role.getRoleName());
                info.setStatus(role.getStatus());
                result.add(info);
            }
        }
        return result;
    }

    private List<MenuTreeDTO> getMenusByUserId(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.findByUserId(userId);
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysMenu> menus = sysMenuMapper.findByRoleIds(roleIds);
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        return buildMenuTree(menus);
    }

    private List<MenuTreeDTO> buildMenuTree(List<SysMenu> flatMenus) {
        Map<Long, List<SysMenu>> childrenMap = new HashMap<>();
        List<SysMenu> roots = new ArrayList<>();
        for (SysMenu menu : flatMenus) {
            if (menu.getParentId() == null) {
                roots.add(menu);
            } else {
                childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
            }
        }
        List<MenuTreeDTO> result = new ArrayList<>();
        for (SysMenu root : roots) {
            result.add(buildMenuTreeNode(root, childrenMap));
        }
        return result;
    }

    private MenuTreeDTO buildMenuTreeNode(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
        MenuTreeDTO node = new MenuTreeDTO();
        node.setMenuId(menu.getMenuId());
        node.setMenuName(menu.getMenuName());
        node.setMenuUrl(menu.getMenuUrl());
        node.setIcon(menu.getIcon());
        node.setSortOrder(menu.getSortOrder());
        node.setIsLeaf(menu.getIsLeaf());
        List<SysMenu> children = childrenMap.get(menu.getMenuId());
        if (children != null && !children.isEmpty()) {
            List<MenuTreeDTO> childNodes = new ArrayList<>();
            children.sort(Comparator.comparing(SysMenu::getSortOrder));
            for (SysMenu child : children) {
                childNodes.add(buildMenuTreeNode(child, childrenMap));
            }
            node.setChildren(childNodes);
        }
        return node;
    }

    private UserInfo buildUserInfo(SysUser user) {
        UserInfo info = new UserInfo();
        info.setUserId(user.getUserId());
        info.setUserName(user.getUserName());
        info.setEmail(user.getEmail());
        info.setProvince(user.getProvince());
        info.setCity(user.getCity());
        info.setDistrict(user.getDistrict());
        info.setHobby(user.getHobby());
        info.setStatus(user.getStatus());
        return info;
    }
}
