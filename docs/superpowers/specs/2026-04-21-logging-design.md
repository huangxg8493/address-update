# 日志记录功能设计

## 概述

为客户地址信息维护系统添加日志记录功能，记录关键业务操作。

## 技术选型

- **日志 API**：SLF4J 2.x（接口层，解耦实现）
- **日志实现**：Logback 1.4（性能好，配置简洁）
- **输出目标**：文件日志

## 记录内容

| 操作 | 级别 | 日志内容 |
|------|------|----------|
| save | INFO | 保存单条地址，clientNo |
| saveAll | INFO | 批量保存地址，clientNo，数量 |
| update | INFO | 更新单条地址，clientNo |
| updateAll | INFO | 批量更新地址，clientNo，数量 |
| delete | INFO | 删除地址，seqNo |
| 查询异常 | ERROR | clientNo，异常信息 |
| 更新异常 | ERROR | seqNo，异常信息 |
| 保存异常 | ERROR | clientNo，异常信息 |

## 日志格式

```
时间 [线程名] 级别 类名 - 消息
例：2026-04-21 13:15:00.123 [http-nio-8080-exec-1] INFO  c.a.r.MyBatisClientAddressRepository - 保存地址 clientNo=123456
```

## 文件配置

- **路径**：`logs/app.log`
- **滚动策略**：按日期（每天一个新文件）
- **保留**：7 天自动清理
- **编码**：UTF-8

## 实现步骤

1. pom.xml 添加 slf4j-api 和 logback-classic 依赖
2. 创建 logback.xml 配置文件
3. Repository 层添加 Logger 并记录关键操作
4. 添加单元测试验证日志输出

## 相关文件

- `pom.xml` (修改)
- `src/main/resources/logback.xml` (新增)
- `src/main/java/com/address/repository/MyBatisClientAddressRepository.java` (修改)
- `src/main/java/com/address/repository/JdbcClientAddressRepository.java` (修改)
