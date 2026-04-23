package com.address.security;

import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.findActiveByPhone(phone);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在或已禁用");
        }
        return new User(sysUser.getPhone(), sysUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public UserDetails loadUserByUserId(Long userId) {
        SysUser sysUser = sysUserMapper.findById(userId);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new User(sysUser.getPhone(), sysUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
