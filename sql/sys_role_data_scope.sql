CREATE TABLE sys_role_data_scope (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    scope_id BIGINT NOT NULL COMMENT '数据范围ID',
    INDEX idx_role_id (role_id),
    INDEX idx_scope_id (scope_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据范围关联表';
