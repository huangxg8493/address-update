# 地址查询接口实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 POST /client/address/query 分页查询接口

**Architecture:** 遵循现有分层架构 Controller -> Service -> Repository，使用 MyBatis RowBounds 分页

**Tech Stack:** Java8 + Maven + Spring Boot + MyBatis

---

## 文件结构

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| Create | src/main/java/com/address/dto/AddressQueryRequest.java | 查询请求参数 |
| Create | src/main/java/com/address/dto/PageResult.java | 分页结果封装 |
| Create | src/main/java/com/address/dto/AddressQueryResponse.java | 响应结构 |
| Create | src/main/java/com/address/repository/AddressQueryRepository.java | 查询接口 |
| Create | src/main/java/com/address/repository/MyBatisAddressQueryRepository.java | MyBatis 实现 |
| Create | src/main/java/com/address/service/ClientAddressQueryService.java | 查询服务 |
| Create | src/main/java/com/address/controller/ClientAddressQueryController.java | 查询控制器 |
| Modify | src/main/java/com/address/repository/CifAddressMapper.java | 新增分页方法 |
| Modify | src/main/resources/mapper/CifAddressMapper.xml | 新增分页 SQL |
| Create | src/test/java/com/address/controller/ClientAddressQueryControllerTest.java | 控制器测试 |
| Create | src/test/java/com/address/service/ClientAddressQueryServiceTest.java | 服务测试 |

---

### Task 1: 创建 AddressQueryRequest DTO

**Files:**
- Create: `src/main/java/com/address/dto/AddressQueryRequest.java`

- [ ] **Step 1: 创建 AddressQueryRequest.java**

```java
package com.address.dto;

public class AddressQueryRequest {
    private String clientNo;
    private String addressType;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/AddressQueryRequest.java
git commit -m "feat: 新增 AddressQueryRequest 查询请求参数 DTO"
```

---

### Task 2: 创建 PageResult DTO

**Files:**
- Create: `src/main/java/com/address/dto/PageResult.java`

- [ ] **Step 1: 创建 PageResult.java**

```java
package com.address.dto;

import java.util.List;

public class PageResult<T> {
    private String clientNo;
    private int pageNum;
    private int pageSize;
    private long total;
    private int totalPages;
    private List<T> list;

    public PageResult() {}

    public PageResult(String clientNo, int pageNum, int pageSize, long total, List<T> list) {
        this.clientNo = clientNo;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    public String getClientNo() { return clientNo; }
    public int getPageNum() { return pageNum; }
    public int getPageSize() { return pageSize; }
    public long getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
    public List<T> getList() { return list; }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/PageResult.java
git commit -m "feat: 新增 PageResult 分页结果封装 DTO"
```

---

### Task 3: 创建 AddressQueryResponse DTO

**Files:**
- Create: `src/main/java/com/address/dto/AddressQueryResponse.java`

- [ ] **Step 1: 创建 AddressQueryResponse.java**

```java
package com.address.dto;

import java.util.List;

public class AddressQueryResponse {
    private String clientNo;
    private int pageNum;
    private int pageSize;
    private long total;
    private int totalPages;
    private List<AddressItem> list;

    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public List<AddressItem> getList() { return list; }
    public void setList(List<AddressItem> list) { this.list = list; }

    public static class AddressItem {
        private String seqNo;
        private String addressType;
        private String addressDetail;
        private String lastChangeDate;
        private String isMailingAddress;
        private String isNewest;

        public String getSeqNo() { return seqNo; }
        public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
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
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/AddressQueryResponse.java
git commit -m "feat: 新增 AddressQueryResponse 查询响应 DTO"
```

---

### Task 4: 创建 AddressQueryRepository 接口

**Files:**
- Create: `src/main/java/com/address/repository/AddressQueryRepository.java`

- [ ] **Step 1: 创建 AddressQueryRepository.java**

```java
package com.address.repository;

import com.address.dto.PageResult;
import com.address.model.CifAddress;

public interface AddressQueryRepository {
    PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize);
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/AddressQueryRepository.java
git commit -m "feat: 新增 AddressQueryRepository 查询接口"
```

---

### Task 5: 修改 CifAddressMapper 添加分页方法

**Files:**
- Modify: `src/main/java/com/address/repository/CifAddressMapper.java:22`

- [ ] **Step 1: 添加分页查询方法到 CifAddressMapper.java**

在 `batchUpdate` 方法后添加:

```java
List<CifAddress> findPage(@Param("clientNo") String clientNo,
                          @Param("addressType") String addressType,
                          @Param("delFlag") String delFlag,
                          @Param("offset") int offset,
                          @Param("limit") int limit);

long countPage(@Param("clientNo") String clientNo,
               @Param("addressType") String addressType,
               @Param("delFlag") String delFlag);
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/CifAddressMapper.java
git commit -m "feat: CifAddressMapper 新增分页查询方法"
```

---

### Task 6: 修改 CifAddressMapper.xml 添加分页 SQL

**Files:**
- Modify: `src/main/resources/mapper/CifAddressMapper.xml`

- [ ] **Step 1: 在 `</mapper>` 前添加分页 SQL**

```xml
    <!-- 分页查询 -->
    <select id="findPage" resultMap="CifAddressResultMap">
        SELECT
            SEQ_NO,
            CLIENT_NO,
            ADDRESS_TYPE,
            ADDRESS_DETAIL,
            LAST_CHANGE_DATE,
            IS_MAILING_ADDRESS,
            IS_NEWEST,
            DEL_FLAG
        FROM CIF_ADDRESS
        <where>
            <if test="clientNo != null and clientNo != ''">
                AND CLIENT_NO = #{clientNo,jdbcType=VARCHAR}
            </if>
            <if test="addressType != null and addressType != ''">
                AND ADDRESS_TYPE = #{addressType,jdbcType=VARCHAR}
            </if>
            <if test="delFlag != null and delFlag != ''">
                AND DEL_FLAG = #{delFlag,jdbcType=CHAR}
            </if>
        </where>
        ORDER BY LAST_CHANGE_DATE DESC
        LIMIT #{offset,jdbcType=INTEGER} OFFSET #{limit,jdbcType=INTEGER}
    </select>

    <!-- 分页计数 -->
    <select id="countPage" resultType="long">
        SELECT COUNT(*)
        FROM CIF_ADDRESS
        <where>
            <if test="clientNo != null and clientNo != ''">
                AND CLIENT_NO = #{clientNo,jdbcType=VARCHAR}
            </if>
            <if test="addressType != null and addressType != ''">
                AND ADDRESS_TYPE = #{addressType,jdbcType=VARCHAR}
            </if>
            <if test="delFlag != null and delFlag != ''">
                AND DEL_FLAG = #{delFlag,jdbcType=CHAR}
            </if>
        </where>
    </select>
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/mapper/CifAddressMapper.xml
git commit -m "feat: CifAddressMapper.xml 新增分页查询 SQL"
```

---

### Task 7: 创建 MyBatisAddressQueryRepository 实现

**Files:**
- Create: `src/main/java/com/address/repository/MyBatisAddressQueryRepository.java`

- [ ] **Step 1: 创建 MyBatisAddressQueryRepository.java**

```java
package com.address.repository;

import com.address.constants.Constants;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisAddressQueryRepository implements AddressQueryRepository {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisAddressQueryRepository.class);

    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public MyBatisAddressQueryRepository(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            List<CifAddress> list = mapper.findPage(clientNo, addressType, Constants.NO, offset, pageSize);
            long total = mapper.countPage(clientNo, addressType, Constants.NO);
            return new PageResult<>(clientNo, pageNum, pageSize, total, list);
        }
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/MyBatisAddressQueryRepository.java
git commit -m "feat: 新增 MyBatisAddressQueryRepository 分页查询实现"
```

---

### Task 8: 创建 ClientAddressQueryService

**Files:**
- Create: `src/main/java/com/address/service/ClientAddressQueryService.java`

- [ ] **Step 1: 创建 ClientAddressQueryService.java**

```java
package com.address.service;

import com.address.dto.AddressQueryResponse;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import com.address.repository.AddressQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientAddressQueryService {

    private final AddressQueryRepository queryRepository;

    @Autowired
    public ClientAddressQueryService(AddressQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public AddressQueryResponse queryPage(String clientNo, String addressType, int pageNum, int pageSize) {
        PageResult<CifAddress> pageResult = queryRepository.findPage(clientNo, addressType, pageNum, pageSize);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AddressQueryResponse.AddressItem> items = new ArrayList<>();
        for (CifAddress addr : pageResult.getList()) {
            AddressQueryResponse.AddressItem item = new AddressQueryResponse.AddressItem();
            item.setSeqNo(addr.getSeqNo());
            item.setAddressType(addr.getAddressType());
            item.setAddressDetail(addr.getAddressDetail());
            item.setLastChangeDate(addr.getLastChangeDate() != null ? sdf.format(addr.getLastChangeDate()) : null);
            item.setIsMailingAddress(addr.getIsMailingAddress());
            item.setIsNewest(addr.getIsNewest());
            items.add(item);
        }

        AddressQueryResponse response = new AddressQueryResponse();
        response.setClientNo(clientNo);
        response.setPageNum(pageResult.getPageNum());
        response.setPageSize(pageResult.getPageSize());
        response.setTotal(pageResult.getTotal());
        response.setTotalPages(pageResult.getTotalPages());
        response.setList(items);
        return response;
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/service/ClientAddressQueryService.java
git commit -m "feat: 新增 ClientAddressQueryService 查询服务"
```

---

### Task 9: 创建 ClientAddressQueryController

**Files:**
- Create: `src/main/java/com/address/controller/ClientAddressQueryController.java`

- [ ] **Step 1: 创建 ClientAddressQueryController.java**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.AddressQueryRequest;
import com.address.dto.AddressQueryResponse;
import com.address.service.ClientAddressQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientAddressQueryController {

    @Autowired
    private ClientAddressQueryService queryService;

    @PostMapping("/client/address/query")
    public ApiResponse<AddressQueryResponse> queryAddresses(@RequestBody AddressQueryRequest request) {
        if (request.getClientNo() == null || request.getClientNo().trim().isEmpty()) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "客户号不能为空");
        }

        int pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        AddressQueryResponse response = queryService.queryPage(
            request.getClientNo(),
            request.getAddressType(),
            pageNum,
            pageSize
        );

        return ApiResponse.success(response);
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -q`
Expected: SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/controller/ClientAddressQueryController.java
git commit -m "feat: 新增 ClientAddressQueryController 查询控制器"
```

---

### Task 10: 创建 ClientAddressQueryControllerTest

**Files:**
- Create: `src/test/java/com/address/controller/ClientAddressQueryControllerTest.java`

- [ ] **Step 1: 创建 ClientAddressQueryControllerTest.java**

```java
package com.address.controller;

import com.address.dto.AddressQueryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientAddressQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void queryAddresses_success() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("C001");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.clientNo").value("C001"))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void queryAddresses_withAddressType() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("C001");
        request.setAddressType("02");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    void queryAddresses_emptyClientNo_returns400() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo("");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }

    @Test
    void queryAddresses_nullClientNo_returns400() throws Exception {
        AddressQueryRequest request = new AddressQueryRequest();
        request.setClientNo(null);

        mockMvc.perform(post("/client/address/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("客户号不能为空"));
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `mvn test -Dtest=ClientAddressQueryControllerTest`
Expected: SUCCESS (4 tests passed)

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/controller/ClientAddressQueryControllerTest.java
git commit -m "test: 新增 ClientAddressQueryControllerTest 查询控制器测试"
```

---

### Task 11: 创建 ClientAddressQueryServiceTest

**Files:**
- Create: `src/test/java/com/address/service/ClientAddressQueryServiceTest.java`

- [ ] **Step 1: 创建 ClientAddressQueryServiceTest.java**

```java
package com.address.service;

import com.address.dto.AddressQueryResponse;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import com.address.repository.AddressQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientAddressQueryServiceTest {

    @Mock
    private AddressQueryRepository queryRepository;

    @InjectMocks
    private ClientAddressQueryService queryService;

    private CifAddress createTestAddress(String seqNo, String addressType) {
        CifAddress addr = new CifAddress();
        addr.setSeqNo(seqNo);
        addr.setClientNo("C001");
        addr.setAddressType(addressType);
        addr.setAddressDetail("测试地址");
        addr.setLastChangeDate(new Date());
        addr.setIsMailingAddress("N");
        addr.setIsNewest("Y");
        addr.setDelFlag("N");
        return addr;
    }

    @Test
    void queryPage_returnsCorrectResponse() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createTestAddress("1", "02"));
        addresses.add(createTestAddress("2", "03"));

        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 2, addresses);
        when(queryRepository.findPage(eq("C001"), isNull(), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", null, 1, 10);

        assertEquals("C001", response.getClientNo());
        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPageSize());
        assertEquals(2, response.getTotal());
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getList().size());
    }

    @Test
    void queryPage_withAddressType_filtersCorrectly() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createTestAddress("1", "02"));

        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 1, addresses);
        when(queryRepository.findPage(eq("C001"), eq("02"), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", "02", 1, 10);

        assertEquals(1, response.getList().size());
        assertEquals("02", response.getList().get(0).getAddressType());
    }

    @Test
    void queryPage_emptyResult_returnsEmptyList() {
        PageResult<CifAddress> pageResult = new PageResult<>("C001", 1, 10, 0, new ArrayList<>());
        when(queryRepository.findPage(eq("C001"), isNull(), eq(1), eq(10))).thenReturn(pageResult);

        AddressQueryResponse response = queryService.queryPage("C001", null, 1, 10);

        assertEquals(0, response.getTotal());
        assertEquals(0, response.getList().size());
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `mvn test -Dtest=ClientAddressQueryServiceTest`
Expected: SUCCESS (3 tests passed)

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/service/ClientAddressQueryServiceTest.java
git commit -m "test: 新增 ClientAddressQueryServiceTest 查询服务测试"
```

---

### Task 12: 最终验证

- [ ] **Step 1: 运行全部测试**

Run: `mvn test`
Expected: SUCCESS (所有测试通过)

- [ ] **Step 2: 检查 git 状态**

Run: `git status`
Expected: 工作区干净

---

## 自检清单

- [ ] 所有新增 DTO 字段名与设计文档一致
- [ ] PageResult 使用 long 存储 total，避免 int 溢出
- [ ] 分页计算 offset = (pageNum - 1) * pageSize 正确
- [ ] 仅返回 del_flag='N' 的数据
- [ ] lastChangeDate 格式化为 yyyy-MM-dd HH:mm:ss
- [ ] 响应结构包含 clientNo, pageNum, pageSize, total, totalPages, list
