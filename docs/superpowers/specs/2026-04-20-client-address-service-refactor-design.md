# ClientAddressService 重构设计

## 架构

单方法 Service，通过 Repository 接口操作存储，策略接口处理地址选择规则。

## 核心流程

```
1. 获取存量数组 stock
2. 合并上送地址 → mergedIncoming（去重）
3. 标记存量删除项 → mergedStock（del_flag=Y）
4. 遍历 mergedIncoming：
   - 在 mergedStock 中找匹配（addressType+addressDetail）
   - 匹配到 → 设置 seqNo=存量seqNo，收集到更新来源
   - 未匹配 → seqNo=null，insertList
5. 收集 allActive（两个数组中 del_flag≠Y 的有效地址）
6. 从 allActive 中挑选 mailing 和 newestByType（仅收集结果，不设置标识）
7. 重置两个数组所有标识为 N
8. 将 mailing 标识设置为 Y
9. 将 newest 标识设置为 Y（排除已是 mailing 的）
10. 批量 insert（insertList）
11. 遍历 mergedStock：用收集到的更新来源覆盖存量数据，批量 update
```

## 数据流向

- 上送地址匹配存量地址成功 → 用上送数据更新对应存量
- 上送地址匹配存量地址失败 → 新增，生成 id

## 依赖

- `ClientAddressRepository`: findByClientNo, saveAll, update
- `AddressMerger`: mergeIncoming, mergeStock
- `MailingAddressStrategy`: select
- `NewestAddressStrategy`: selectByType

## 关键约束

- del_flag=Y 的地址不参与业务规则
- 通讯地址必然是最新地址
- 批量 update 针对所有存量地址
