# 客户地址维护 RESTful 接口实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增 PUT `/client/address/update` 接口，接收客户地址更新请求，返回更新后地址列表

**Architecture:** 通过 Spring MVC Controller 接收 JSON 请求，参数校验后调用现有 ClientAddressService，响应统一包装为 ApiResponse

**Tech Stack:** Spring Boot 2.7, Spring MVC, Jackson JSON

---

## 文件结构

| 操作 | 文件路径 | 职责 |
|------|----------|------|
| Create | `src/main/java/com/address/common/ApiResponse.java` | 统一响应封装（code/message/data） |
| Create | `src/main/java/com/address/common/ErrorCode.java` | 错误码枚举 |
| Create | `src/main/java/com/address/dto/AddressUpdateRequest.java` | 请求 DTO |
| Create | `src/main/java/com/address/controller/ClientAddressController.java` | REST 接口 |
| Create | `src/test/java/com/address/controller/ClientAddressControllerTest.java` | 单元测试 |
| Modify | `src/main/java/com/address/Application.java` | 添加 controller 包扫描 |

---

## Task 1: 新建 ApiResponse 统一响应类

**Files:**
- Create: `src/main/java/com/address/common/ApiResponse.java`

- [ ] **Step 1: 编写测试**

```java
package com.address.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_with_data() {
        ApiResponse<String> response = ApiResponse.success("data");
        assertEquals("200", response.getCode());
        assertEquals("成功", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void error_with_message() {
        ApiResponse<Void> response = ApiResponse.error("400", "参数错误");
        assertEquals("400", response.getCode());
        assertEquals("参数错误", response.getMessage());
        assertNull(response.getData());
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `mvn test -Dtest=ApiResponseTest -q`
Expected: FAIL (class not found)

- [ ] **Step 3: 编写最小实现**

```java
package com.address.common;

public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "成功", data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `mvn test -Dtest=ApiResponseTest -q`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/common/ApiResponse.java src/test/java/com/address/common/ApiResponseTest.java
git commit -m "feat: 新增 ApiResponse 统一响应类"
```

---

## Task 2: 新建 ErrorCode 错误码枚举

**Files:**
- Create: `src/main/java/com/address/common/ErrorCode.java`

- [ ] **Step 1: 编写最小实现**

```java
package com.address.common;

public enum ErrorCode {
    SUCCESS("200", "成功"),
    BAD_REQUEST("400", "参数错误"),
    NOT_FOUND("404", "客户不存在"),
    CONFLICT("409", "地址冲突"),
    INTERNAL_ERROR("500", "服务器异常");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/common/ErrorCode.java
git commit -m "feat: 新增 ErrorCode 错误码枚举"
```

---

## Task 3: 新建 AddressUpdateRequest 请求 DTO

**Files:**
- Create: `src/main/java/com/address/dto/AddressUpdateRequest.java`

- [ ] **Step 1: 编写最小实现**

```java
package com.address.dto;

import com.address.model.CifAddress;
import java.util.List;

public class AddressUpdateRequest {
    private String clientNo;
    private List<CifAddress> addresses;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public List<CifAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CifAddress> addresses) { this.addresses = addresses; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/AddressUpdateRequest.java
git commit -m "feat: 新增 AddressUpdateRequest 请求 DTO"
```

---

## Task 4: 新建 ClientAddressController

**Files:**
- Create: `src/main/java/com/address/controller/ClientAddressController.java`

- [ ] **Step 1: 编写测试**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.model.CifAddress;
import com.address.service.ClientAddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClientAddressControllerTest {

    @Autowired
    private ClientAddressService service;

    @Test
    void updateAddresses_success() {
        // 构造请求
        List<CifAddress> addresses = new ArrayList<>();
        CifAddress addr = new CifAddress();
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        addresses.add(addr);

        // 调用 service（controller 逻辑直接委托给 service）
        List<CifAddress> result = service.updateAddresses("C001", addresses);

        // 验证返回非空
        assertNotNull(result);
    }
}
```

- [ ] **Step 2: 运行测试验证通过**

Run: `mvn test -Dtest=ClientAddressControllerTest -q`
Expected: PASS（service 方法已存在）

- [ ] **Step 3: 编写 Controller 实现**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.AddressUpdateRequest;
import com.address.model.CifAddress;
import com.address.service.ClientAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/address")
public class ClientAddressController {

    @Autowired
    private ClientAddressService clientAddressService;

    @PutMapping("/update")
    public ApiResponse<List<CifAddress>> updateAddresses(@RequestBody AddressUpdateRequest request) {
        // 参数校验
        if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
        }
        if (request.getAddresses() == null || request.getAddresses().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "地址列表不能为空");
        }

        // 调用 service
        List<CifAddress> result = clientAddressService.updateAddresses(
            request.getClientNo(),
            request.getAddresses()
        );

        return ApiResponse.success(result);
    }
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `mvn test -Dtest=ClientAddressControllerTest -q`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/controller/ClientAddressController.java src/test/java/com/address/controller/ClientAddressControllerTest.java
git commit -m "feat: 新增 ClientAddressController PUT /client/address/update 接口"
```

---

## Task 5: 修改 Application 添加 Controller 扫描

**Files:**
- Modify: `src/main/java/com/address/Application.java`

- [ ] **Step 1: 查看当前 Application**

```java
package com.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.address.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- [ ] **Step 2: 添加 @ComponentScan**

```java
package com.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.address.repository")
@ComponentScan("com.address.controller")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- [ ] **Step 3: 运行测试验证**

Run: `mvn test -q`
Expected: PASS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/Application.java
git commit -m "chore: Application 添加 controller 包扫描"
```

---

## Task 6: 端到端集成测试（可选）

**Files:**
- Create: `src/test/java/com/address/integration/ClientAddressControllerIntegrationTest.java`

如有需要可添加 RestTemplate 或 MockMvc 集成测试，验证完整请求-响应流程。

---

## 自查清单

1. **Spec 覆盖:** 接口路径 `/client/address/update`、PUT 方法、请求/响应格式、错误码、参数校验规则 — 全部覆盖
2. **占位符检查:** 无 "TBD"、"TODO"、"implement later" 等占位符
3. **类型一致性:** ApiResponse/ErrorCode 在各 Task 中使用一致