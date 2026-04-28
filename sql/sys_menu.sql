CREATE TABLE IF NOT EXISTS sys_menu (
    menu_id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    menu_type VARCHAR(20) DEFAULT 'MENU',
    menu_name VARCHAR(100) NOT NULL,
    menu_url VARCHAR(255),
    icon VARCHAR(100),
    sort_order INT DEFAULT 0,
    status CHAR(1) DEFAULT 'Y',
    is_leaf CHAR(1) DEFAULT 'Y',
    level_depth INT DEFAULT 1,
    component VARCHAR(255),
    component_path VARCHAR(255),
    del_flag CHAR(1) DEFAULT 'N',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_del_flag (del_flag)
);
