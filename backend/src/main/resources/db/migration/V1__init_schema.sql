-- ================================================================
-- 美容沙龙管理系统 - 初始化数据库脚本
-- 版本: V1__init_schema.sql
-- 描述: 创建7张核心业务表
-- ================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS beauty_salon DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE beauty_salon;

-- ---------------------------------------------------------------
-- 1. 用户表 (sys_user)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)    NOT NULL COMMENT '用户名',
    password    VARCHAR(100)   NOT NULL COMMENT '密码（加密存储）',
    real_name   VARCHAR(50)    DEFAULT NULL COMMENT '真实姓名',
    phone       VARCHAR(20)    DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100)   DEFAULT NULL COMMENT '邮箱',
    avatar      VARCHAR(255)   DEFAULT NULL COMMENT '头像URL',
    role        TINYINT        NOT NULL DEFAULT 1 COMMENT '角色: 1=管理员 2=技师 3=前台 4=经理',
    status      TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用 1=启用',
    deleted     TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------------------------------------------------------------
-- 2. 客户表 (crm_customer)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS crm_customer;
CREATE TABLE crm_customer (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    name            VARCHAR(50)    NOT NULL COMMENT '客户姓名',
    phone           VARCHAR(20)    NOT NULL COMMENT '手机号',
    gender          TINYINT        DEFAULT NULL COMMENT '性别: 0=女 1=男 2=未知',
    birthday        DATE           DEFAULT NULL COMMENT '生日',
    wechat         VARCHAR(50)    DEFAULT NULL COMMENT '微信号',
    email           VARCHAR(100)   DEFAULT NULL COMMENT '邮箱',
    member_level    TINYINT        NOT NULL DEFAULT 1 COMMENT '会员等级: 1=普通 2=银卡 3=金卡 4=钻石',
    member_points   INT            NOT NULL DEFAULT 0 COMMENT '会员积分',
    balance         DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    allergy_info    VARCHAR(500)   DEFAULT NULL COMMENT '过敏信息',
    remark          VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=流失 1=活跃 2=休眠',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_phone (phone),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- ---------------------------------------------------------------
-- 3. 服务项目表 (pos_service)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_service;
CREATE TABLE pos_service (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '服务项目ID',
    code            VARCHAR(20)    NOT NULL COMMENT '项目编码',
    name            VARCHAR(100)   NOT NULL COMMENT '项目名称',
    category        VARCHAR(50)    NOT NULL COMMENT '项目分类: 护肤/美发/美甲/化妆/按摩等',
    description     VARCHAR(500)   DEFAULT NULL COMMENT '项目描述',
    duration        INT            NOT NULL DEFAULT 60 COMMENT '预计时长（分钟）',
    price           DECIMAL(10,2)  NOT NULL COMMENT '标准价格',
    cost            DECIMAL(10,2)  DEFAULT 0.00 COMMENT '成本',
    image_url       VARCHAR(255)   DEFAULT NULL COMMENT '项目图片',
    is_active       TINYINT        NOT NULL DEFAULT 1 COMMENT '是否上架: 0=下架 1=上架',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务项目表';

-- ---------------------------------------------------------------
-- 4. 会员卡/套餐表 (pos_membership_card)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_membership_card;
CREATE TABLE pos_membership_card (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '会员卡ID',
    code            VARCHAR(20)    NOT NULL COMMENT '卡编码',
    name            VARCHAR(100)   NOT NULL COMMENT '卡名称',
    type            TINYINT        NOT NULL COMMENT '卡类型: 1=充值卡 2=次卡 3=时间卡',
    price           DECIMAL(10,2)  NOT NULL COMMENT '售价',
    face_value      DECIMAL(10,2)  NOT NULL COMMENT '面值/次数',
    duration_days   INT            DEFAULT NULL COMMENT '有效期（天）',
    description     VARCHAR(500)   DEFAULT NULL COMMENT '卡说明',
    benefit_desc    VARCHAR(500)   DEFAULT NULL COMMENT '权益说明',
    is_active       TINYINT        NOT NULL DEFAULT 1 COMMENT '是否上架: 0=下架 1=上架',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员卡/套餐表';

-- ---------------------------------------------------------------
-- 5. 预约记录表 ( appointment)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS appointment;
CREATE TABLE appointment (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    customer_id     BIGINT         NOT NULL COMMENT '客户ID',
    service_id      BIGINT         NOT NULL COMMENT '服务项目ID',
    beautician_id   BIGINT         NOT NULL COMMENT '技师ID',
    appointment_no  VARCHAR(32)    NOT NULL COMMENT '预约单号',
    appointment_time DATETIME      NOT NULL COMMENT '预约时间',
    end_time        DATETIME       NOT NULL COMMENT '预计结束时间',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 1=已预约 2=已确认 3=已到店 4=服务中 5=已完成 6=已取消 7=超时',
    source          TINYINT        NOT NULL DEFAULT 1 COMMENT '来源: 1=线上 2=电话 3=现场',
    remark          VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    cancel_reason   VARCHAR(200)   DEFAULT NULL COMMENT '取消原因',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_appointment_no (appointment_no),
    KEY idx_customer_id (customer_id),
    KEY idx_appointment_time (appointment_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';

-- ---------------------------------------------------------------
-- 6. 订单表 (pos_order)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_order;
CREATE TABLE pos_order (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no        VARCHAR(32)    NOT NULL COMMENT '订单号',
    customer_id     BIGINT         NOT NULL COMMENT '客户ID',
    beautician_id   BIGINT         DEFAULT NULL COMMENT '服务技师ID',
    order_type      TINYINT        NOT NULL COMMENT '订单类型: 1=服务订单 2=商品订单 3=充值订单 4=套餐订单',
    total_amount    DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    pay_amount      DECIMAL(10,2)  NOT NULL COMMENT '实付金额',
    points_used     INT            NOT NULL DEFAULT 0 COMMENT '使用积分',
    points_gained   INT            NOT NULL DEFAULT 0 COMMENT '获得积分',
    pay_type        TINYINT        NOT NULL COMMENT '支付方式: 1=现金 2=微信 3=支付宝 4=银行卡 5=会员卡',
    pay_status      TINYINT        NOT NULL DEFAULT 0 COMMENT '支付状态: 0=待支付 1=已支付 2=已退款',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '订单状态: 1=进行中 2=已完成 3=已取消 4=已退款',
    remark          VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_customer_id (customer_id),
    KEY idx_order_type (order_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ---------------------------------------------------------------
-- 7. 订单明细表 (pos_order_item)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_order_item;
CREATE TABLE pos_order_item (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
    order_id        BIGINT         NOT NULL COMMENT '订单ID',
    item_type       TINYINT        NOT NULL COMMENT '明细类型: 1=服务 2=商品 3=套餐',
    item_id         BIGINT         NOT NULL COMMENT '项目/商品/套餐ID',
    item_name       VARCHAR(100)   NOT NULL COMMENT '项目/商品/套餐名称',
    quantity        INT            NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price      DECIMAL(10,2)  NOT NULL COMMENT '单价',
    subtotal        DECIMAL(10,2)  NOT NULL COMMENT '小计金额',
    discount_rate   DECIMAL(5,2)   DEFAULT 100.00 COMMENT '折扣率(%)',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ================================================================
-- 初始化管理员账号 (密码: admin123)
-- 密码为 BCrypt 加密
-- ================================================================
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800138000', 1, 1);

-- ================================================================
-- 初始化测试服务项目
-- ================================================================
INSERT INTO pos_service (code, name, category, description, duration, price, cost) VALUES
('SVC001', '面部深层清洁', '护肤', '采用专业导出液和清洁仪器，深层清洁毛孔内的污垢和油脂', 60, 198.00, 50.00),
('SVC002', '玻尿酸补水导入', '护肤', '利用超声波导入玻尿酸精华，补水锁水，改善肌肤干燥', 45, 268.00, 80.00),
('SVC003', '精油按摩推背', '按摩', '使用天然植物精油，配合专业手法舒缓背部肌肉疲劳', 60, 258.00, 70.00),
('SVC004', '日系精致美甲', '美甲', '修剪甲型、去死皮、上色、封层，提供日系风格设计', 90, 168.00, 40.00),
('SVC005', '时尚烫发', '美发', '采用进口烫发药水，打造自然蓬松卷发造型', 180, 598.00, 180.00);
