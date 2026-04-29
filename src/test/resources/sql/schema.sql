-- 创建 sys_user 表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT 'Y',
    user_name VARCHAR(100),
    email VARCHAR(100),
    province VARCHAR(100),
    city VARCHAR(200),
    district VARCHAR(100),
    addr_detail VARCHAR(255),
    hobby VARCHAR(500),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);

-- 创建 sys_role 表
CREATE TABLE IF NOT EXISTS sys_role (
    role_id BIGINT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT 'Y',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建 sys_user_role 表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

-- 创建 sys_permission 表
CREATE TABLE IF NOT EXISTS sys_permission (
    permission_id BIGINT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    menu_url VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建 sys_role_permission 表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL
);

-- 创建 sys_data_scope 表
CREATE TABLE IF NOT EXISTS sys_data_scope (
    scope_id BIGINT PRIMARY KEY,
    scope_code VARCHAR(50) NOT NULL,
    scope_name VARCHAR(100) NOT NULL,
    scope_type VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建 sys_role_data_scope 表
CREATE TABLE IF NOT EXISTS sys_role_data_scope (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    scope_id BIGINT NOT NULL
);

-- 创建 sys_menu 表
CREATE TABLE IF NOT EXISTS sys_menu (
    menu_id BIGINT PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    menu_url VARCHAR(255),
    icon VARCHAR(100),
    sort_order INT,
    status CHAR(1) DEFAULT 'Y',
    is_leaf CHAR(1) DEFAULT 'Y',
    level_depth INT DEFAULT 0,
    component VARCHAR(255),
    component_path VARCHAR(255),
    parent_id BIGINT,
    del_flag CHAR(1) DEFAULT 'N',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id)
);

-- 创建 sys_role_menu 表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id)
);

-- 创建 cif_address 表
CREATE TABLE IF NOT EXISTS CIF_ADDRESS (
    SEQ_NO VARCHAR(64) PRIMARY KEY,
    CLIENT_NO VARCHAR(32) NOT NULL,
    ADDRESS_TYPE VARCHAR(2) NOT NULL,
    ADDRESS_DETAIL VARCHAR(256) NOT NULL,
    LAST_CHANGE_DATE DATETIME,
    IS_MAILING_ADDRESS CHAR(1) DEFAULT 'N',
    IS_NEWEST CHAR(1) DEFAULT 'N',
    DEL_FLAG CHAR(1) DEFAULT 'N',
    INDEX idx_client_no (CLIENT_NO),
    INDEX idx_client_type (CLIENT_NO, ADDRESS_TYPE)
);