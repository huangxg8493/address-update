package com.address.service;

import com.address.dto.UserCreateRequest;
import com.address.dto.UserQueryRequest;
import com.address.dto.UserResponse;
import com.address.dto.UserUpdateRequest;
import com.address.model.SysUser;
import com.address.model.SysUserRole;
import com.address.repository.SysUserMapper;
import com.address.repository.SysUserRoleMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> query(UserQueryRequest request) {
        List<SysUser> users = sysUserMapper.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (SysUser user : users) {
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(user.getStatus())) {
                continue;
            }
            responses.add(toResponse(user));
        }
        return responses;
    }

    public UserResponse create(UserCreateRequest request) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已存在");
        }
        SysUser user = new SysUser();
        user.setUserId(SnowflakeIdGenerator.getInstance().generate8DigitId());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setCity(request.getCity());
        user.setAddrDetail(request.getAddrDetail());
        user.setHobby(request.getHobby());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return toResponse(user);
    }

    public UserResponse update(UserUpdateRequest request) {
        SysUser user = sysUserMapper.findById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getAddrDetail() != null) {
            user.setAddrDetail(request.getAddrDetail());
        }
        if (request.getHobby() != null) {
            user.setHobby(request.getHobby());
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
        return toResponse(user);
    }

    public void delete(Long userId) {
        sysUserMapper.deleteById(userId);
    }

    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setId(SnowflakeIdGenerator.getInstance().nextId());
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    public void changePassword(String phone, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.findActiveByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在或已禁用");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("密码格式错误，至少6位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
    }

    public void resetPassword(Long userId, String newPassword) {
        SysUser user = sysUserMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("密码格式错误，至少6位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
    }

    private UserResponse toResponse(SysUser user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setCity(user.getCity());
        response.setAddrDetail(user.getAddrDetail());
        response.setHobby(user.getHobby());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }
}
