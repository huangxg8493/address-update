package com.address.service;

import com.address.common.ErrorCode;
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
                return LoginResult.error(ErrorCode.USER_NOT_FOUND, "用户未注册");
            } else {
                return LoginResult.error(ErrorCode.USER_DISABLED, "用户已禁用");
            }
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResult.error(ErrorCode.PASSWORD_ERROR, "密码错误");
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
        return LoginResult.success(token, user.getPhone());
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
}
