# 单地址维护接口实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 `/client/address/single/update` 接口，支持单地址的修改和逻辑删除

**Architecture:** 新建 DTO 接收请求，Controller 接收并路由，Service 处理业务逻辑（删除优先），复用 Repository 的 update 方法

**Tech Stack:** Java8 + Spring Boot + MyBatis

---

## 文件结构

- Create: `src/main/java/com/address/dto/SingleAddressRequest.java`
- Create: `src/main/java/com/address/dto/SingleAddressResponse.java`
- Modify: `src/main/java/com/address/controller/ClientAddressController.java` - 添加新接口
- Modify: `src/main/java/com/address/service/ClientAddressService.java` - 添加 updateSingleAddress 方法

---

## Task 1: 创建 DTO

**Files:**
- Create: `src/main/java/com/address/dto/SingleAddressRequest.java`
- Create: `src/main/java/com/address/dto/SingleAddressResponse.java`

- [ ] **Step 1: 创建 SingleAddressRequest.java**

```java
package com.address.dto;

public class SingleAddressRequest {
    private String seqNo;
    private String clientNo;
    private String addressType;
    private String addressDetail;
    private String lastChangeDate;
    private String isMailingAddress;
    private String isNewest;
    private String delFlag;

    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(String lastChangeDate) { this.lastChangeDate = lastChangeDate; }
    public String getIsMailingAddress() { return isMailingAddress; }
    public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
    public String getIsNewest() { return isNewest; }
    public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
```

- [ ] **Step 2: 创建 SingleAddressResponse.java**

```java
package com.address.dto;

public class SingleAddressResponse {
    private String seqNo;
    private String clientNo;
    private String addressType;
    private String addressDetail;
    private String lastChangeDate;
    private String isMailingAddress;
    private String isNewest;
    private String delFlag;

    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(String lastChangeDate) { this.lastChangeDate = lastChangeDate; }
    public String getIsMailingAddress() { return isMailingAddress; }
    public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
    public String getIsNewest() { return isNewest; }
    public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/address/dto/SingleAddressRequest.java src/main/java/com/address/dto/SingleAddressResponse.java
git commit -m "feat: 添加单地址更新请求响应DTO"
```

---

## Task 2: Service 层添加业务方法

**Files:**
- Modify: `src/main/java/com/address/service/ClientAddressService.java`

- [ ] **Step 1: 添加 updateSingleAddress 方法**

在 ClientAddressService.java 末尾添加：

```java
public CifAddress updateSingleAddress(SingleAddressRequest request) {
    // 参数校验
    if (request.getSeqNo() == null || request.getSeqNo().trim().isEmpty()) {
        throw new RuntimeException("seqNo 不能为空");
    }
    if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
        throw new RuntimeException("clientNo 不能为空");
    }

    // 获取存量地址
    List<CifAddress> stock = repository.findByClientNo(request.getClientNo());
    CifAddress target = null;
    for (CifAddress addr : stock) {
        if (addr.getSeqNo().equals(request.getSeqNo())) {
            target = addr;
            break;
        }
    }

    if (target == null) {
        throw new RuntimeException("地址不存在");
    }

    // 删除优先
    if ("Y".equals(request.getDelFlag())) {
        target.setDelFlag("Y");
        target.setLastChangeDate(new Date());
        repository.update(target);
        return target;
    }

    // 修改
    target.setAddressType(request.getAddressType());
    target.setAddressDetail(request.getAddressDetail());
    target.setLastChangeDate(new Date());
    if (request.getIsMailingAddress() != null) {
        target.setIsMailingAddress(request.getIsMailingAddress());
    }
    if (request.getIsNewest() != null) {
        target.setIsNewest(request.getIsNewest());
    }
    repository.update(target);
    return target;
}
```

- [ ] **Step 2: 添加 import**

在 ClientAddressService.java 文件顶部添加：
```java
import com.address.dto.SingleAddressRequest;
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/address/service/ClientAddressService.java
git commit -m "feat: ClientAddressService添加updateSingleAddress方法"
```

---

## Task 3: Controller 层添加接口

**Files:**
- Modify: `src/main/java/com/address/controller/ClientAddressController.java`

- [ ] **Step 1: 添加 import**

在文件顶部添加：
```java
import com.address.dto.SingleAddressRequest;
import com.address.dto.SingleAddressResponse;
```

- [ ] **Step 2: 添加接口方法**

在 ClientAddressController.java 末尾添加：

```java
@PostMapping("/client/address/single/update")
public ApiResponse<SingleAddressResponse> updateSingleAddress(@RequestBody SingleAddressRequest request) {
    // 参数校验
    if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
        return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
    }
    if (request.getSeqNo() == null || request.getSeqNo().trim().isEmpty()) {
        return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "seqNo不能为空");
    }

    try {
        CifAddress result = clientAddressService.updateSingleAddress(request);

        // 转换为响应 DTO
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SingleAddressResponse response = new SingleAddressResponse();
        response.setSeqNo(result.getSeqNo());
        response.setClientNo(result.getClientNo());
        response.setAddressType(result.getAddressType());
        response.setAddressDetail(result.getAddressDetail());
        response.setLastChangeDate(result.getLastChangeDate() != null ? sdf.format(result.getLastChangeDate()) : null);
        response.setIsMailingAddress(result.getIsMailingAddress());
        response.setIsNewest(result.getIsNewest());
        response.setDelFlag(result.getDelFlag());

        return ApiResponse.success(response);
    } catch (RuntimeException e) {
        return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), e.getMessage());
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/address/controller/ClientAddressController.java
git commit -m "feat: 添加单地址更新接口 /client/address/single/update"
```

---

## Task 4: 编译验证

- [ ] **Step 1: 编译项目**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 2: 运行测试**

```bash
mvn test
```

Expected: 所有测试通过
