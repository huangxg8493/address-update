package com.address.model;

import java.time.LocalDateTime;

/**
 * 系统菜单实体
 */
public class SysMenu {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String isLeaf;
    private Integer levelDepth;
    private String component;
    private String componentPath;
    private Long parentId;
    private String delFlag;
    private LocalDateTime createTime;
    private String menuType;  // 菜单类型：MENU-菜单，CATALOG-目录，BUTTON-按钮

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public Integer getLevelDepth() { return levelDepth; }
    public void setLevelDepth(Integer levelDepth) { this.levelDepth = levelDepth; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
}
