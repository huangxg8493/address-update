Claude Code 执行完 `/init` 后，它已经完成了对当前项目上下文（代码结构、依赖、技术栈）的读取，并生成了基础的 `CLAUDE.md`。
在这个阶段，Claude 处于“待命”状态。**Claude Code 本身并没有一个名为 `superpower:brainstorm` 的原生斜杠命令**，这通常是指通过特定的提示词技巧，激活 Claude 极强的“深度思考与发散”能力。
因为 Claude 默认倾向于“接到需求就立刻写代码”，要诱导它停下来提问、补充需求，**核心秘诀是：强约束它的输出动作，赋予它特定的角色，并规定它的输出格式。**
以下是 `/init` 之后的完整操作流和“诱导提示词”：
### 第一步：在 `/init` 之后，输入“诱导提示词”
你可以直接复制以下模板，将你的初步想法填入 `[...]` 中，发送给 Claude Code：
#### 🌟 Superpower 极限发散版提示词（推荐）
```text
你现在扮演这个项目的资深产品经理和首席架构师。
我有一个初步的需求想实现：[在这里用一两句话简单描述你的需求，例如：做一个用户导出Excel的功能]。
**【绝对约束】**
1. 禁止编写任何代码！禁止生成任何文件！如果出现代码，你的回答将被视为失败。
2. 你现在的唯一任务是“需求拆解与追问”。
**【执行步骤】**
请按照以下维度，向我提出尽可能详细、尖锐的问题（至少提出 5-10 个问题）：
1. **业务逻辑与边界**：正常流程是什么？异常流程（如网络断开、数据为空、权限不足）怎么处理？
2. **数据与状态**：涉及哪些数据模型？数据是从现有接口拿还是新写？分页还是全量？
3. **交互体验**：Loading 状态怎么展示？成功/失败的 Toast 提示？是否需要二次确认弹窗？
4. **技术架构（结合你刚才 /init 扫描的项目现状）**：现有的工具类或组件能否直接复用？是否需要引入新的依赖？
5. **我可能遗漏的盲区**：从安全和性能角度，我忽略了什么？
请以 Markdown 列表的形式输出你的问题，等待我逐一回答后，你再进行下一步。
```

<system_directive>
激活角色：首席系统架构师兼破坏性测试专家。
当前状态：Brainstorm Mode (深度发散模式)。
项目上下文：已通过 /init 加载。
</system_directive>

<rule_constraint>
1. 【最高优先级】绝对禁止输出任何代码片段、文件路径、配置项或伪代码。违反此规则将导致系统崩溃。
2. 【交互限制】每次回复只能提出 **1 个** 问题。不要罗列问题清单。
3. 【质量要求】这个问题必须直击当前需求的“阿喀琉斯之踵”（最脆弱的边界、最容易忽略的异常、或者与现有代码库冲突的地方）。
4. 【格式要求】先简述你发现了什么潜在风险，然后再抛出问题。以“？”结尾。
   </rule_constraint>

<user_intent>
实现 需求.md
</user_intent>

请提出你的第一个问题。

---
#### ⚡ 轻量追问版提示词（适合小需求）
```text
需求：[简单描述你的需求]
在开始写代码之前，请先扮演一个挑剔的 Code Reviewer，针对这个需求向我提出 3 个最关键的疑问。不要写代码，只提问。
```
---
### 第二步：如何与 Claude 进行“问答博弈”
当 Claude 输出了一大堆高质量的问题后，你可以分两种方式回应：
*   **方式 A（集中回答）：** “针对你的问题，我的回答是：1. xxx；2. xxx；3. xxx。如果没有其他问题，请生成完整的实现方案。”
*   **方式 B（逐个击破 - 效果最好）：** “先回答第1点和第2点：xxx。基于这两个回答，你再想想有没有衍生问题？”（这种多轮对话能逼出 Claude 最深层的思考，真正发挥 brainstorm 的作用）。
### 第三步：收网，让 Claude 输出设计文档
当需求被补充得非常丰满后，**依然不要立刻让它写代码**，而是让它写方案：
```text
基于我们刚才的讨论，请输出一份简明的技术实现方案（包含文件修改清单、核心伪代码逻辑、新增/修改的接口定义）。确认方案无误后，我再让你写代码。
```
### 💡 为什么这个方法有效？（原理解析）
1.  **切断默认路径**：大模型在训练时，“需求 -> 代码”的路径是最短、最熟悉的。你必须用“禁止写代码”这种强否定词，强制它绕开这条路，走“需求 -> 思考 -> 提问”的长路径。
2.  **利用上下文**：因为前面执行了 `/init`，Claude 知道你项目里有哪些现成的组件和工具。在提问时，它会结合你的代码库问出非常专业的问题（比如：“项目里已经有 `utils/format.js`，新需求的格式化是复用这个还是单独写？”），而不是泛泛而谈。
3.  **角色设定**：产品经理和架构师的 System Prompt 能有效改变 Claude 的注意力机制，让它从“实现者”视角切换到“规划者”视角。
    **总结口诀**：先 `/init` 建立上下文，再**“立规矩不许写码”**，接着**“戴帽子扮演专家”**，最后**“逼它按维度提问”**。这样就能完美触发 Claude 的 brainstorm 超能力。




*************************************************************************
# Role
Java 后端开发专家。

# Context
技术栈：java8

# Task
写一个方法接受 身份证号和性别码值，实现客户性别校验

# Requirements & Constraints
- 前提：身份证为18位身份证，
- 判断依据：身份证号码的第17位数字，
- 判断规则：
    - 男性：第17位为奇数（1, 3, 5, 7, 9）
    - 女性：第17位为偶数（2, 4, 6, 8）
      现有码值：0-未知，1-女，2-男，9-未说明
- 输出：如果接口输入的性别和身份证不一致，接口提示：性别输入错误

# Output
请提供 IdAndSexValidator类和 validate(String id,String sex) 方法。
*************************************************************************
Claude Code 使用
1. 安装
   winget install Anthropic.ClaudeCode

注：Install-Module -Name Microsoft.WinGet.Client
Repair-WinGetPackageManager
2. 改配置
   ~/.claude.json
   "hasCompletedOnboarding": true,
3. 使用 CC Switch 配置Api key
   记得每次更新 model 前 备份 C:\Users\99470\.claude\settings.json
4. 安装 插件

winget upgrade Anthropic.ClaudeCode --source winget
winget list Anthropic.ClaudeCode
winget source update
winget upgrade Anthropic.ClaudeCode --version 2.1.118 --source winget

# 项目开发规则
当进行多步骤开发任务（如新增功能、重构）时：
1. 自动调用 /planning-with-files 建立任务追踪文件。
2. 使用 Superpowers 的工作流进行规划和开发。
3. 在执行每一步前，读取 task_plan.md 确认当前任务。

## 工作流规则
当你处理多步骤的开发任务时：
1. 使用 @planning-with-files 建立任务追踪文件。
2. 在开始执行前，检查 `docs/superpowers/plans/` 目录下是否有最新的计划文件。
3. 如果存在新的计划文件，将其内容以 `- [ ]` 任务清单的形式，同步到项目根目录下的 `task_plan.md` 中。

# 1. 错误被自动记录后，使用 Superpowers 的技能进行调试
请使用 Superpowers 的 root-cause-tracing 技能，根据 findings.md 中记录的错误，对认证接口 500 错误进行根因分析

请用200字以内总结我们这个对话里确定的所有技术决策和已完成的实现细节，我会把这段摘要带到下一个对话继续。

请读取docs/superpowers/plans/2026-04-24-login-error-code-plan.md 文件，将其中的任务步骤，追加写入到项目根目录下的 task_plan.md,findings.md,progress.md 文件中，完成后提交，然后使用 Inline Execution - 在当前session执行，批量执行带检查点
请读取 Superpowers 生成的计划，并将其结构化地更新到 planning-with-files 的 task_plan.md,findings.md,progress.md 文件中，将每个任务拆解为可勾选的待办事项


梳理 @src/main/java/com/hxg/address/service/CifAddressService.java 的执行逻辑，用简短的语言，要层次分明，逻辑严谨，表达清晰，无歧义

## 从文件中恢复上下文
请读取 task_plan.md 和 progress.md，告诉我当前的开发进度，并从中断处继续执行

基于superpowers的规划进行编码实现

所有任务以完成，将task_plan.md,findings.md,progress.md中所有任务标记为已完成
根据

对所有测试案例进行分类，看看都有那些业务场景

禁止做任何的假说


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

  "permissions": {
  "allow": [
  "Bash(cd *)",
  "Bash(git status:*)",
  "Bash(git add:*)",
  "Bash(git commit:*)",
  "Bash(git diff:*)",
  "Bash(git log:*)",
  "Bash(git push:*)",
  "Bash(git pull:*)",
  "Bash(mvn test *)",
  "Bash(mvn clean *)"
  ],
  "deny": [
  "Bash(git reset --hard:*)",
  "Bash(git clean:*)",
  "Bash(rm -rf:*)"
  ]
  }
  
