package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.MenuQueryRequest;
import com.address.dto.MenuResponse;
import com.address.dto.MenuTreeResponse;
import com.address.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private MenuResponse testMenuResponse;

    @BeforeEach
    void setUp() {
        testMenuResponse = new MenuResponse();
        testMenuResponse.setMenuId(1L);
        testMenuResponse.setMenuName("系统管理");
        testMenuResponse.setMenuUrl("/admin");
        testMenuResponse.setStatus("Y");
        testMenuResponse.setIsLeaf("Y");
        testMenuResponse.setLevelDepth(1);
    }

    @Test
    void testQueryMenus() {
        when(menuService.query(any(MenuQueryRequest.class))).thenReturn(Arrays.asList(testMenuResponse));

        MenuQueryRequest request = new MenuQueryRequest();
        ApiResponse<List<MenuResponse>> response = menuController.query(request);

        assertNotNull(response);
        assertEquals("200", response.getCode());
        assertEquals(1, response.getData().size());
        assertEquals("系统管理", response.getData().get(0).getMenuName());
    }

    @Test
    void testGetById() {
        when(menuService.getById(1L)).thenReturn(testMenuResponse);

        ApiResponse<MenuResponse> response = menuController.getById(1L);

        assertNotNull(response);
        assertEquals("200", response.getCode());
        assertEquals("系统管理", response.getData().getMenuName());
    }

    @Test
    void testGetTree() {
        when(menuService.getTree()).thenReturn(Arrays.asList());

        ApiResponse<List<MenuTreeResponse>> response = menuController.getTree();

        assertNotNull(response);
        assertEquals("200", response.getCode());
    }

    @Test
    void testDeleteMenu() {
        ApiResponse<Void> response = menuController.delete(1L);

        assertNotNull(response);
        assertEquals("200", response.getCode());
    }

    @Test
    void testCreateMenu() {
        com.address.dto.MenuCreateRequest createRequest = new com.address.dto.MenuCreateRequest();
        createRequest.setMenuName("系统管理");
        createRequest.setMenuUrl("/admin");

        when(menuService.create(any(com.address.dto.MenuCreateRequest.class))).thenReturn(testMenuResponse);

        ApiResponse<MenuResponse> response = menuController.create(createRequest);

        assertNotNull(response);
        assertEquals("200", response.getCode());
    }

    @Test
    void testUpdateMenu() {
        com.address.dto.MenuUpdateRequest updateRequest = new com.address.dto.MenuUpdateRequest();
        updateRequest.setMenuId(1L);
        updateRequest.setMenuName("系统设置");

        MenuResponse updatedResponse = new MenuResponse();
        updatedResponse.setMenuId(1L);
        updatedResponse.setMenuName("系统设置");

        when(menuService.update(any(com.address.dto.MenuUpdateRequest.class))).thenReturn(updatedResponse);

        ApiResponse<MenuResponse> response = menuController.update(updateRequest);

        assertNotNull(response);
        assertEquals("200", response.getCode());
    }
}
