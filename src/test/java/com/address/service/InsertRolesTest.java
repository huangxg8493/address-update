package com.address.service;

import com.address.model.SysRole;
import com.address.repository.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class InsertRolesTest {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void testInsertRoles() {
        // 管理员角色
        SysRole admin = new SysRole();
        admin.setRoleId(1L);
        admin.setRoleCode("admin");
        admin.setRoleName("系统管理员");
        admin.setStatus("Y");
        admin.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(admin);
        System.out.println("插入角色: admin");

        // 普通用户角色
        SysRole user = new SysRole();
        user.setRoleId(2L);
        user.setRoleCode("user");
        user.setRoleName("普通用户");
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(user);
        System.out.println("插入角色: user");

        // 访客角色
        SysRole guest = new SysRole();
        guest.setRoleId(3L);
        guest.setRoleCode("guest");
        guest.setRoleName("访客");
        guest.setStatus("Y");
        guest.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(guest);
        System.out.println("插入角色: guest");

        System.out.println("角色插入完成，共 3 条记录");
    }
}