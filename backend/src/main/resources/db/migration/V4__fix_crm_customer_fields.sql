-- 修复 crm_customer 表缺少的字段
-- 问题：实体类 CrmCustomer 有 create_by/update_by 字段，但表中缺失

-- 添加 create_by 字段
ALTER TABLE crm_customer
ADD COLUMN IF NOT EXISTS create_by BIGINT DEFAULT NULL COMMENT '创建人ID';

-- 添加 update_by 字段
ALTER TABLE crm_customer
ADD COLUMN IF NOT EXISTS update_by BIGINT DEFAULT NULL COMMENT '更新人ID';

-- 添加 avatar 字段（如果不存在）
ALTER TABLE crm_customer
ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL';
