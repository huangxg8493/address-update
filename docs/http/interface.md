# 接口文档

## 注册接口

### 请求

- **URL**: `POST /api/auth/register`
- **Content-Type**: `application/json`

**请求体**:
```json
{
    "phone": "13900000001",
    "password": "password123",
    "userName": "张三",
    "email": "zhangsan@example.com",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "hobby": "篮球"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位，1开头 |
| password | String | 是 | 密码，最少6位 |
| userName | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| province | String | 否 | 省份 |
| city | String | 否 | 城市 |
| district | String | 否 | 区县 |
| hobby | String | 否 | 爱好 |

### 响应

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```

**错误响应** (HTTP 200):
```json
{
    "code": "101004",
    "message": "手机号已注册",
    "data": null
}
```

### 错误码

| 错误码 | 说明 |
|--------|------|
| 000000 | 成功 |
| 101004 | 手机号已注册 |
| 101005 | 手机号格式错误 |
| 101006 | 密码格式错误 |

### 错误码含义

| 错误码 | 中文含义 |
|--------|----------|
| 000000 | 成功 |
| 101004 | 手机号已注册 |
| 101005 | 手机号格式错误 |
| 101006 | 密码格式错误 |