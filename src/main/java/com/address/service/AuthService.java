package com.address.service;

import com.address.common.AuthErrorCode;
import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.LoginResult;
import com.address.dto.RegisterRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import com.address.security.JwtUtil;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResult login(LoginRequest request) {
        SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
        if (user == null) {
            SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
            if (existUser == null) {
                return LoginResult.error(AuthErrorCode.USER_NOT_FOUND, "用户未注册");
            } else {
                return LoginResult.error(AuthErrorCode.USER_DISABLED, "用户已禁用");
            }
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResult.error(AuthErrorCode.PASSWORD_ERROR, "密码错误");
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
        return LoginResult.success(token, user.getPhone());
    }

    public void register(RegisterRequest request) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }
        SysUser user = new SysUser();
        user.setUserId(SnowflakeIdGenerator.getInstance().nextId());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }
}
