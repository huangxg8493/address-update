# 地址查询接口设计

## 1. 接口定义

### 请求

```
POST /client/address/query
Content-Type: application/json
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| clientNo | String | 是 | 客户号 |
| addressType | String | 否 | 地址类型，不传则返回所有类型 |
| pageNum | Integer | 否 | 页码，从1开始，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

### 响应

```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "clientNo": "C001",
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "totalPages": 3,
    "list": [
      {
        "seqNo": "xxx",
        "addressType": "02",
        "addressDetail": "xxx",
        "lastChangeDate": "2026-04-22 12:00:00",
        "isMailingAddress": "Y",
        "isNewest": "Y"
      }
    ]
  }
}
```

## 2. 新增组件

| 组件 | 包路径 | 说明 |
|------|--------|------|
| AddressQueryRequest | com.address.dto | 查询请求参数 |
| AddressQueryResponse | com.address.dto | 分页响应结构 |
| PageResult | com.address.dto | 分页结果封装 |
| AddressQueryRepository | com.address.repository | 查询接口 |
| MyBatisAddressQueryRepository | com.address.repository | MyBatis 分页查询实现 |
| ClientAddressQueryService | com.address.service | 查询逻辑 |
| ClientAddressQueryController | com.address.controller | 查询入口 |

## 3. 架构

```
ClientAddressQueryController
    └── ClientAddressQueryService
            └── AddressQueryRepository（独立接口）
                    └── MyBatisAddressQueryRepository
```

## 4. Repository 方法设计

```java
public interface AddressQueryRepository {
    PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize);
}
```

## 5. 实现要点

- 使用 MyBatis 的 RowBounds 进行分页
- 仅返回 del_flag='N' 的数据
- lastChangeDate 格式化为 yyyy-MM-dd HH:mm:ss
- 与现有更新接口使用相同的响应 DTO 结构
