package com.address.controller;

import com.address.dto.MenuCreateRequest;
import com.address.dto.MenuQueryRequest;
import com.address.dto.MenuResponse;
import com.address.dto.MenuUpdateRequest;
import com.address.service.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    public void testQueryMenus() throws Exception {
        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("测试菜单");
        response.setMenuUrl("/test");
        when(menuService.query(any(MenuQueryRequest.class))).thenReturn(Arrays.asList(response));

        mockMvc.perform(post("/api/menus/query")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].menuName").value("测试菜单"));
    }

    @Test
    @WithMockUser
    public void testCreateMenu() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuName("新菜单");
        request.setMenuUrl("/new");

        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("新菜单");
        response.setMenuUrl("/new");
        when(menuService.create(any(MenuCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/menus/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.menuName").value("新菜单"));
    }

    @Test
    @WithMockUser
    public void testUpdateMenu() throws Exception {
        MenuUpdateRequest request = new MenuUpdateRequest();
        request.setMenuId(1L);
        request.setMenuName("更新菜单");

        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("更新菜单");
        when(menuService.update(any(MenuUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/menus/update")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.menuName").value("更新菜单"));
    }

    @Test
    @WithMockUser
    public void testDeleteMenu() throws Exception {
        mockMvc.perform(post("/api/menus/delete")
                .with(csrf())
                .param("menuId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @WithMockUser
    public void testGetMenuTree() throws Exception {
        mockMvc.perform(post("/api/menus/tree")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }
}
