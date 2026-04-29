-- ================================================================
-- 美容沙龙管理系统 - 扩展表结构
-- 版本: V2__add_beautician_campaign_recharge_coupon.sql
-- 描述: 添加美容师、活动、充值、优惠券表
-- ================================================================

USE beauty_salon;

-- ---------------------------------------------------------------
-- 8. 美容师表 (beautician)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS beautician;
CREATE TABLE beautician (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '美容师ID',
    code            VARCHAR(20)    NOT NULL COMMENT '工号',
    name            VARCHAR(50)    NOT NULL COMMENT '姓名',
    phone           VARCHAR(20)    NOT NULL COMMENT '手机号',
    avatar          VARCHAR(255)   DEFAULT NULL COMMENT '头像URL',
    gender          TINYINT        DEFAULT 0 COMMENT '性别: 0=女 1=男 2=未知',
    specialty       VARCHAR(200)   DEFAULT NULL COMMENT '擅长领域',
    introduction    VARCHAR(500)   DEFAULT NULL COMMENT '个人简介',
    level           TINYINT        NOT NULL DEFAULT 1 COMMENT '等级: 1=初级 2=中级 3=高级 4=首席',
    rating          DECIMAL(3,2)   NOT NULL DEFAULT 5.00 COMMENT '评分',
    service_count   INT            NOT NULL DEFAULT 0 COMMENT '服务次数',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=离职 1=在职 2=休假',
    join_date       DATE           DEFAULT NULL COMMENT '入职日期',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_phone (phone),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='美容师表';

-- ---------------------------------------------------------------
-- 9. 活动表 (pos_campaign)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_campaign;
CREATE TABLE pos_campaign (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    code            VARCHAR(20)    NOT NULL COMMENT '活动编码',
    name            VARCHAR(100)   NOT NULL COMMENT '活动名称',
    type            TINYINT        NOT NULL COMMENT '活动类型: 1=折扣 2=满减 3=赠品 4=套餐',
    start_date      DATE           NOT NULL COMMENT '开始日期',
    end_date        DATE           NOT NULL COMMENT '结束日期',
    discount_type   TINYINT        DEFAULT 1 COMMENT '优惠类型: 1=折扣率 2=固定金额',
    discount_value  DECIMAL(10,2)  DEFAULT NULL COMMENT '优惠值(折扣率或固定金额)',
    min_amount      DECIMAL(10,2)  DEFAULT 0.00 COMMENT '最低消费金额',
    description     VARCHAR(500)   DEFAULT NULL COMMENT '活动描述',
    image_url       VARCHAR(255)   DEFAULT NULL COMMENT '活动图片',
    target_type     TINYINT        DEFAULT 1 COMMENT '适用对象: 1=全部 2=新客 3=会员',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=未启用 1=进行中 2=已结束',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_status (status),
    KEY idx_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- ---------------------------------------------------------------
-- 10. 充值记录表 (pos_recharge)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_recharge;
CREATE TABLE pos_recharge (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '充值ID',
    recharge_no     VARCHAR(32)    NOT NULL COMMENT '充值单号',
    customer_id     BIGINT         NOT NULL COMMENT '客户ID',
    amount          DECIMAL(10,2)  NOT NULL COMMENT '充值金额',
    gift_amount     DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
    pay_type        TINYINT        NOT NULL COMMENT '支付方式: 1=现金 2=微信 3=支付宝 4=银行卡',
    operator_id     BIGINT         NOT NULL COMMENT '操作员ID',
    before_balance  DECIMAL(10,2)  NOT NULL COMMENT '充值前余额',
    after_balance   DECIMAL(10,2)  NOT NULL COMMENT '充值后余额',
    campaign_id     BIGINT         DEFAULT NULL COMMENT '参与活动ID',
    remark          VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_recharge_no (recharge_no),
    KEY idx_customer_id (customer_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- ---------------------------------------------------------------
-- 11. 优惠券表 (pos_coupon)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_coupon;
CREATE TABLE pos_coupon (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    code            VARCHAR(32)    NOT NULL COMMENT '优惠券码',
    name            VARCHAR(100)   NOT NULL COMMENT '优惠券名称',
    type            TINYINT        NOT NULL COMMENT '优惠券类型: 1=满减券 2=折扣券 3=兑换券',
    discount_type   TINYINT        NOT NULL COMMENT '优惠方式: 1=满减 2=折扣',
    min_amount      DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '使用门槛(满X元)',
    discount_value  DECIMAL(10,2)  NOT NULL COMMENT '优惠值',
    discount_rate   DECIMAL(5,2)   DEFAULT NULL COMMENT '折扣率(%)',
    valid_type      TINYINT        NOT NULL COMMENT '有效期类型: 1=固定日期 2=领取后N天',
    start_date      DATE           DEFAULT NULL COMMENT '有效期开始',
    end_date        DATE           DEFAULT NULL COMMENT '有效期结束',
    valid_days      INT            DEFAULT NULL COMMENT '领取后有效天数',
    total_count     INT            NOT NULL COMMENT '发放总数',
    remain_count    INT            NOT NULL COMMENT '剩余数量',
    per_limit       INT            NOT NULL DEFAULT 1 COMMENT '每人限领',
    receive_count   INT            NOT NULL DEFAULT 0 COMMENT '已领取数量',
    use_count       INT            NOT NULL DEFAULT 0 COMMENT '已使用数量',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 0=未启用 1=启用 2=已下架',
    target_type     TINYINT        DEFAULT 1 COMMENT '适用对象: 1=全部 2=新人 3=会员',
    service_ids     VARCHAR(500)   DEFAULT NULL COMMENT '适用服务ID列表(逗号分隔)',
    image_url       VARCHAR(255)   DEFAULT NULL COMMENT '优惠券图片',
    description     VARCHAR(500)   DEFAULT NULL COMMENT '使用说明',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_status (status),
    KEY idx_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券表';

-- ---------------------------------------------------------------
-- 12. 客户优惠券表 (pos_customer_coupon)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS pos_customer_coupon;
CREATE TABLE pos_customer_coupon (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '客户优惠券ID',
    customer_id     BIGINT         NOT NULL COMMENT '客户ID',
    coupon_id       BIGINT         NOT NULL COMMENT '优惠券ID',
    code            VARCHAR(32)    NOT NULL COMMENT '领取的优惠券码',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 1=未使用 2=已使用 3=已过期',
    receive_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    use_time        DATETIME       DEFAULT NULL COMMENT '使用时间',
    order_id        BIGINT         DEFAULT NULL COMMENT '使用的订单ID',
    expired_time    DATETIME       NOT NULL COMMENT '过期时间',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '删除标记: 0=未删除 1=已删除',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_coupon_id (coupon_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户优惠券表';

-- ---------------------------------------------------------------
-- 初始化测试美容师
-- ---------------------------------------------------------------
INSERT INTO beautician (code, name, phone, gender, specialty, level, rating, service_count, status, join_date) VALUES
('B001', '李美容', '13900139001', 0, '面部护理,美甲', 3, 4.90, 156, 1, '2024-01-15'),
('B002', '王技师', '13900139002', 1, '美发,烫发', 4, 4.95, 203, 1, '2023-06-01'),
('B003', '张按摩', '13900139003', 1, '按摩,推拿', 2, 4.80, 98, 1, '2024-03-20'),
('B004', '陈美甲', '13900139004', 0, '美甲,手足护理', 3, 4.85, 167, 1, '2023-11-10'),
('B005', '刘化妆', '13900139005', 0, '化妆,新娘妆', 4, 4.92, 134, 1, '2023-08-25');

-- ---------------------------------------------------------------
-- 初始化测试活动
-- ---------------------------------------------------------------
INSERT INTO pos_campaign (code, name, type, start_date, end_date, discount_type, discount_value, min_amount, description, status) VALUES
('CMP001', '新客8折特惠', 1, '2026-01-01', '2026-12-31', 1, 80.00, 0.00, '新客户首次消费享受8折优惠', 1),
('CMP002', '满500减100', 2, '2026-04-01', '2026-04-30', 2, 100.00, 500.00, '单笔消费满500元减100元', 1),
('CMP003', '会员日双倍积分', 4, '2026-01-01', '2026-12-31', 1, 100.00, 0.00, '每月15日为会员日，消费双倍积分', 1);

-- ---------------------------------------------------------------
-- 初始化测试优惠券
-- ---------------------------------------------------------------
INSERT INTO pos_coupon (code, name, type, discount_type, min_amount, discount_value, valid_type, start_date, end_date, total_count, remain_count, per_limit, status, description) VALUES
('CPN001', '新人50元券', 1, 1, 200.00, 50.00, 1, '2026-01-01', '2026-12-31', 1000, 850, 1, 1, '满200元减50元，新人专享'),
('CPN002', '8折折扣券', 2, 2, 100.00, 0.00, 2, NULL, NULL, 30, 28, 1, 1, '满100元享8折优惠，领取后30天有效'),
('CPN003', '免费体验券', 3, 1, 0.00, 0.00, 2, NULL, NULL, 50, 45, 1, 1, '可兑换任意单项服务，领取后7天有效');
