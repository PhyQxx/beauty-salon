-- ================================================================
-- 美容沙龙管理系统 - 权限与日志扩展
-- 版本: V3__add_permission_and_log.sql
-- 描述: 添加权限表、角色权限关联表、操作日志表
-- ================================================================

USE beauty_salon;

-- ---------------------------------------------------------------
-- 13. 权限表 (sys_permission)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    code            VARCHAR(100)   NOT NULL COMMENT '权限编码',
    name            VARCHAR(50)    NOT NULL COMMENT '权限名称',
    type            TINYINT        NOT NULL COMMENT '类型: 1=菜单 2=按钮 3=接口',
    parent_id       BIGINT         DEFAULT 0 COMMENT '父权限ID',
    path            VARCHAR(200)   DEFAULT NULL COMMENT '路由/接口路径',
    icon            VARCHAR(100)   DEFAULT NULL COMMENT '图标',
    sort_order      INT            NOT NULL DEFAULT 0 COMMENT '排序',
    description     VARCHAR(200)   DEFAULT NULL COMMENT '描述',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用 1=启用',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ---------------------------------------------------------------
-- 14. 角色权限关联表 (sys_role_permission)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'ID',
    role_id         BIGINT         NOT NULL COMMENT '角色ID (对应sys_user.role)',
    permission_id   BIGINT         NOT NULL COMMENT '权限ID',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ---------------------------------------------------------------
-- 15. 操作日志表 (sys_oper_log)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    module              VARCHAR(50)    DEFAULT NULL COMMENT '操作模块',
    business_type       TINYINT        DEFAULT 0 COMMENT '业务类型: 0=其它 1=新增 2=修改 3=删除 4=授权 5=登录 6=登出 7=导出 8=导入',
    method              VARCHAR(100)   DEFAULT NULL COMMENT '请求方法',
    request_method      VARCHAR(10)    DEFAULT NULL COMMENT '请求方式: GET/POST/PUT/DELETE',
    operator_type       TINYINT        DEFAULT 1 COMMENT '操作人类型: 1=后台用户 2=顾客',
    operator_id         BIGINT         DEFAULT NULL COMMENT '操作人ID',
    operator_name       VARCHAR(50)    DEFAULT NULL COMMENT '操作人名称',
    request_url         VARCHAR(500)   DEFAULT NULL COMMENT '请求地址',
    request_params      TEXT           DEFAULT NULL COMMENT '请求参数',
    response_params     TEXT           DEFAULT NULL COMMENT '响应参数',
    status              TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=异常 1=正常',
    error_msg           TEXT           DEFAULT NULL COMMENT '错误信息',
    ip_address          VARCHAR(128)   DEFAULT NULL COMMENT 'IP地址',
    user_agent          VARCHAR(500)   DEFAULT NULL COMMENT '用户代理',
    operation_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    duration_ms         INT            DEFAULT NULL COMMENT '耗时(毫秒)',
    PRIMARY KEY (id),
    KEY idx_operator_id (operator_id),
    KEY idx_operation_time (operation_time),
    KEY idx_module (module),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ---------------------------------------------------------------
-- 16. 登录日志表 (sys_login_log)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id         BIGINT         DEFAULT NULL COMMENT '用户ID',
    username        VARCHAR(50)    NOT NULL COMMENT '用户名',
    ip_address      VARCHAR(128)   DEFAULT NULL COMMENT 'IP地址',
    login_location  VARCHAR(200)   DEFAULT NULL COMMENT '登录地点',
    browser         VARCHAR(100)   DEFAULT NULL COMMENT '浏览器',
    os              VARCHAR(100)   DEFAULT NULL COMMENT '操作系统',
    status          TINYINT        NOT NULL COMMENT '状态: 0=失败 1=成功',
    message         VARCHAR(200)    DEFAULT NULL COMMENT '提示信息',
    login_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ---------------------------------------------------------------
-- 17. 赠送金过期表 (gift_balance_expire) - 用于追踪赠送金有效期
-- ---------------------------------------------------------------
ALTER TABLE crm_customer ADD COLUMN gift_balance_expire DATETIME DEFAULT NULL COMMENT '赠送金过期时间';

-- ---------------------------------------------------------------
-- 初始化权限数据（基于现有角色: 1=管理员 2=技师 3=前台 4=经理）
-- ---------------------------------------------------------------

-- 管理员角色(1)拥有所有权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order, description) VALUES
-- 一级菜单
('system', '系统管理', 1, 0, '/system', 1, '系统管理模块'),
('crm', '客户管理', 1, 0, '/crm', 2, '客户管理模块'),
('appointment', '预约管理', 1, 0, '/appointment', 3, '预约管理模块'),
('pos', '收银管理', 1, 0, '/pos', 4, '收银管理模块'),
('marketing', '营销管理', 1, 0, '/marketing', 5, '营销管理模块'),
('beautician', '美容师管理', 1, 0, '/beautician', 6, '美容师管理模块'),
('service', '服务项目', 1, 0, '/service', 7, '服务项目管理'),
('data', '数据中心', 1, 0, '/data', 8, '数据中心');

-- 系统管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('system:user', '用户管理', 1, 1, '/system/user', 1),
('system:role', '角色管理', 1, 1, '/system/role', 2),
('system:permission', '权限管理', 1, 1, '/system/permission', 3),
('system:log:oper', '操作日志', 1, 1, '/system/log/oper', 4),
('system:log:login', '登录日志', 1, 1, '/system/log/login', 5);

-- 客户管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('crm:customer', '客户列表', 1, 2, '/crm/customer', 1),
('crm:customer:create', '新增客户', 2, 6, NULL, 1),
('crm:customer:update', '编辑客户', 2, 6, NULL, 2),
('crm:customer:delete', '删除客户', 2, 6, NULL, 3),
('crm:customer:export', '导出客户', 2, 6, NULL, 4);

-- 预约管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('appointment:list', '预约列表', 1, 3, '/appointment/list', 1),
('appointment:create', '创建预约', 2, 10, NULL, 1),
('appointment:update', '修改预约', 2, 10, NULL, 2),
('appointment:cancel', '取消预约', 2, 10, NULL, 3);

-- 收银管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('pos:order', '订单管理', 1, 4, '/pos/order', 1),
('pos:recharge', '充值管理', 1, 4, '/pos/recharge', 2),
('pos:refund', '退款操作', 2, 13, NULL, 1),
('pos:order:export', '导出订单', 2, 13, NULL, 2);

-- 营销管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('marketing:campaign', '活动管理', 1, 5, '/marketing/campaign', 1),
('marketing:coupon', '优惠券管理', 1, 5, '/marketing/coupon', 2),
('marketing:campaign:create', '创建活动', 2, 17, NULL, 1),
('marketing:coupon:create', '创建优惠券', 2, 18, NULL, 1);

-- 美容师管理子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('beautician:list', '美容师列表', 1, 6, '/beautician/list', 1),
('beautician:schedule', '排班管理', 1, 6, '/beautician/schedule', 2),
('beautician:create', '新增美容师', 2, 21, NULL, 1),
('beautician:update', '编辑美容师', 2, 21, NULL, 2);

-- 服务项目子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('service:list', '项目列表', 1, 7, '/service/list', 1),
('service:create', '新增项目', 2, 24, NULL, 1),
('service:update', '编辑项目', 2, 24, NULL, 2),
('service:delete', '删除项目', 2, 24, NULL, 3);

-- 数据中心子权限
INSERT INTO sys_permission (code, name, type, parent_id, path, sort_order) VALUES
('data:dashboard', '数据概览', 1, 8, '/data/dashboard', 1),
('data:report', '统计报表', 1, 8, '/data/report', 2),
('data:export', '导出数据', 2, 29, NULL, 1);

-- 赋予管理员(role=1)所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE deleted = 0;

-- 赋予前台(role=3)部分权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE code IN (
    'crm:customer', 'crm:customer:create', 'crm:customer:update',
    'appointment:list', 'appointment:create', 'appointment:update', 'appointment:cancel',
    'pos:order', 'pos:recharge',
    'beautician:list',
    'service:list',
    'data:dashboard'
) AND deleted = 0;

-- 赋予技师(role=2)部分权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE code IN (
    'appointment:list',
    'beautician:list', 'beautician:schedule',
    'service:list',
    'data:dashboard'
) AND deleted = 0;
