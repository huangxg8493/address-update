# sys_user 省市区字段合并设计

## 概述

将 `sys_user` 表的 `province`、`city`、`district` 三个独立字段合并为 `city`（省市区组合字段），并新增 `addr_detail` 详细地址字段。

## 数据库变更

### 字段变更

| 操作 | 字段 | 类型 | 说明 |
|------|------|------|------|
| 删除 | province | VARCHAR(50) | 省 |
| 删除 | city | VARCHAR(50) | 市 |
| 删除 | district | VARCHAR(50) | 区 |
| 新增 | city | VARCHAR(200) | 省市区组合，如 "广东省深圳市南山区" |
| 新增 | addr_detail | VARCHAR(255) | 详细地址，如 "科技园1号路101室" |

### 存量数据迁移

```sql
-- 将三字段合并为 city
UPDATE sys_user SET city = CONCAT(IFNULL(province,''), IFNULL(city,''), IFNULL(district,''));

-- 删除旧字段
ALTER TABLE sys_user DROP COLUMN province;
ALTER TABLE sys_user DROP COLUMN city;
ALTER TABLE sys_user DROP COLUMN district;

-- 添加新字段（迁移前先添加，避免丢失数据）
ALTER TABLE sys_user ADD COLUMN city VARCHAR(200) COMMENT '省市区组合';
ALTER TABLE sys_user ADD COLUMN addr_detail VARCHAR(255) COMMENT '详细地址';
```

## 涉及文件

### Model 层
- `SysUser.java` - 删除 province/city/district，新增 city/addrDetail

### Mapper 层
- `SysUserMapper.xml` - 更新 resultMap 和所有相关 SQL

### DTO 层
- `UserInfo.java` - 删除 province/city/district，新增 city/addrDetail
- `UserResponse.java` - 同上
- `UserCreateRequest.java` - 同上
- `UserUpdateRequest.java` - 同上

### Service 层
- `AuthService.java` - 修改 register()、buildUserInfo() 中的字段映射

### 测试文件
- `AuthServiceTest.java` - 更新相关测试用例
- `UserServiceTest.java` - 更新相关测试用例

### SQL 文件
- `schema.sql` - 更新建表语句

### 文档
- `docs/http/interface.md` - 更新接口文档

## 数据流向

1. 前端传入组合字符串如 "广东省深圳市南山区" 到 city 字段
2. 前端传入详细地址如 "科技园1号路101室" 到 addr_detail 字段
3. 后端直接存储，不做拼接处理
4. 存量数据通过 SQL 迁移脚本合并

## 测试验证

- 单元测试更新 `AuthServiceTest`、`UserServiceTest`
- 验证存量数据迁移正确性
- 验证新接口字段返回正确
