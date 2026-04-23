CREATE TABLE sys_data_scope (
    scope_id BIGINT PRIMARY KEY COMMENT '数据范围ID',
    scope_code VARCHAR(50) NOT NULL COMMENT '范围代码',
    scope_name VARCHAR(100) NOT NULL COMMENT '范围名称',
    scope_type VARCHAR(20) NOT NULL COMMENT '范围类型 OWN-自有 DEPT-部门 ALL-全部',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_scope_code (scope_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据范围表';
