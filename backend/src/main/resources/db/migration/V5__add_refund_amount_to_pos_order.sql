-- 给 pos_order 表添加退款金额字段
ALTER TABLE pos_order
ADD COLUMN refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '退款金额' AFTER pay_amount;

-- 给 pos_coupon 表添加领取会员ID字段
ALTER TABLE pos_coupon
ADD COLUMN member_id BIGINT DEFAULT NULL COMMENT '领取会员ID' AFTER campaign_id;
