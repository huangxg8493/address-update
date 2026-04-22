# 单地址维护接口设计

## 1. 接口概述

**URL:** `POST /client/address/single/update`

**功能:** 支持单个地址的修改和逻辑删除

## 2. 请求格式

```json
{
    "seqNo": "702055748011692032",
    "clientNo": "C001",
    "addressType": "03",
    "addressDetail": "北京市朝阳区建国路88号",
    "lastChangeDate": "2026-04-22 15:18:54",
    "isMailingAddress": "Y",
    "isNewest": "Y",
    "delFlag": "N"
}
```

## 3. 响应格式

```json
{
    "code": "00000",
    "message": "成功",
    "data": {
        "seqNo": "702055748011692032",
        "clientNo": "C001",
        "addressType": "03",
        "addressDetail": "北京市朝阳区建国路88号",
        "lastChangeDate": "2026-04-22 16:00:00",
        "isMailingAddress": "Y",
        "isNewest": "Y",
        "delFlag": "N"
    }
}
```

## 4. 业务规则

| 条件 | 操作 |
|------|------|
| delFlag='Y' | 逻辑删除（设置 delFlag='Y'），忽略其他字段 |
| seqNo 有值且 delFlag='N' | 修改地址，更新 lastChangeDate 为当前时间 |
| seqNo 为空 | 返回错误：seqNo 不能为空 |

## 5. 参数校验

- `clientNo` 不能为空
- `seqNo` 不能为空（删除和修改都需要）

## 6. 实现要点

- 新建 `SingleAddressRequest` DTO，字段与 CifAddress 一致
- 在 `ClientAddressController` 添加新接口
- 在 `ClientAddressService` 添加 `updateSingleAddress` 方法
- 复用现有 Repository 的 save/update 方法
