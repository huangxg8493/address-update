# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

客户地址信息维护系统（Java8 + Maven），核心规则：
- 一个客户可有多个地址，按类型区分（居住地址、联系地址、单位地址等）
- 每种类型地址只能有一个"最新地址"（isNewest='Y'）
- 只能有一个"通讯地址"（isMailingAddress='Y'），且通讯地址必然是最新的
- 通讯地址按优先级选取：其他地址 > 联系地址 > 居住地址 > 单位地址 > 户籍地址 > 证件地址 > 营业地址 > 注册地址 > 办公地址 > 永久地址

## 常用命令

```bash
# Maven 编译
mvn compile

# 运行单个测试
mvn test -Dtest=测试类名

# 打包
mvn package
```

## Claude Code 命令执行规范 (绝对遵守)
### 禁止使用 cd 复合命令
**绝对禁止**生成类似 `cd <目录> && <命令>` 或 `cd <目录> ; <命令>` 的复合 Bash 命令。
这种格式会触发安全拦截机制，导致流程中断。
### Git 命令规范
- 当前工作目录始终被视为项目根目录，**不需要也不允许**使用 cd 切换目录。
- 直接执行 git 命令，例如：`git add .`、`git commit -m "xxx"`。
- 如果确实需要指定其他目录的 git 仓库，必须使用 git 自带的 `-C` 参数，例如：`git -C D:/other/repo status`。
### 其他命令规范
- 需要在特定目录执行脚本时，不要用 cd，直接传入绝对路径，例如：`node D:/AI/scripts/build.js`。

## 架构要点

### 核心算法（Service 层）
1. **合并阶段**：对上送数组和存量数组分别按"地址类型+地址详情"去重合并
2. **新增/更新标记**：上送数组中 seqNo 为空=新增，有值=更新
3. **地址规则应用**：挑选通讯地址和每种类型的最新地址
4. **批量入库**：新增地址生成 id 并 insert；更新地址按 seqNo update

### 数据模型
- 实体类 `CifAddress`：seqNo, clientNo, addressType, addressDetail, lastChangeDate, isMailingAddress, isNewest, del_flag
- 使用内存代替数据库，**不要使用 Map**

### 技术约束
- Java8 + Maven
- 不新建异常，使用 RuntimeException
- del_flag='Y' 的记录为逻辑删除，不参与业务规则
