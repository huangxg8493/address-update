# 客户地址维护 RESTful 接口设计

**日期:** 2026-04-22
**路径:** `/client/address/update`

---

## 1. 接口规格

### 请求

- **方法:** PUT
- **路径:** `/client/address/update`
- **Content-Type:** application/json

```json
{
  "clientNo": "C001",
  "addresses": [
    {
      "seqNo": "可选，有值=更新，无值=新增",
      "addressType": "地址类型编码",
      "addressDetail": "地址详情"
    }
  ]
}
```

### 响应（成功）

```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "seqNo": "701959449979523072",
      "clientNo": "C001",
      "addressType": "02",
      "addressDetail": "联系地址",
      "lastChangeDate": "2026-04-22T08:56:15",
      "isMailingAddress": "Y",
      "isNewest": "Y",
      "delFlag": "N"
    }
  ]
}
```

### 错误响应

```json
{
  "code": "400",
  "message": "客户号不能为空",
  "data": null
}
```

---

## 2. 组件结构

| 组件 | 包路径 | 职责 |
|------|--------|------|
| ClientAddressController | controller | 接收请求、参数校验、调用 Service、封装响应 |
| ApiResponse | common | 统一响应包装（code/message/data） |
| ClientAddressService | service | 复用现有 `updateAddresses()` 方法 |

---

## 3. 错误码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误（如客户号为空、地址列表为空） |
| 404 | 客户不存在 |
| 409 | 地址冲突（如并发更新） |
| 500 | 服务器异常 |

---

## 4. 参数校验规则

- `clientNo` 必填，不能为空
- `addresses` 必填，不能为空数组

---

## 5. 实现步骤（待规划）

1. 新建 `ApiResponse<T>` 统一响应类
2. 新建 `ClientAddressController` 接收 PUT 请求
3. 参数校验，返回 400 错误码
4. 调用 `ClientAddressService.updateAddresses()`
5. 包装响应返回