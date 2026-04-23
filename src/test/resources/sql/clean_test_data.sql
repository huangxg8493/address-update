-- Clean test data for Phase 16 tests
DELETE FROM sys_user_role WHERE user_id > 1000000000000;
DELETE FROM sys_user WHERE user_id > 1000000000000;
DELETE FROM sys_role_permission WHERE role_id > 1000000000000;
DELETE FROM sys_role_data_scope WHERE role_id > 1000000000000;
DELETE FROM sys_role WHERE role_id > 1000000000000;
DELETE FROM sys_permission WHERE permission_id > 1000000000000;
DELETE FROM sys_data_scope WHERE scope_id > 1000000000000;
