package com.address.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuTreeDTO {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String isLeaf;
    private List<MenuTreeDTO> children = new ArrayList<>();

    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuUrl() { return menuUrl; }
    public void setMenuUrl(String menuUrl) { this.menuUrl = menuUrl; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public List<MenuTreeDTO> getChildren() { return children; }
    public void setChildren(List<MenuTreeDTO> children) { this.children = children; }
}