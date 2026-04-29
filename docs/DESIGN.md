# 美发店管理系统 - 系统设计文档

## 一、系统架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                          客户端层                                    │
│  ┌─────────────────────┐         ┌─────────────────────┐           │
│  │   移动端 (uniapp)    │         │   Web端 (Vue)        │           │
│  │  · 会员微信小程序     │         │  · 管理员后台        │           │
│  │  · 技师接单APP       │         │  · 数据可视化        │           │
│  └─────────┬───────────┘         └─────────┬───────────┘           │
│            │                               │                        │
│            └───────────────┬───────────────┘                        │
│                            ▼                                        │
│                   ┌─────────────────┐                               │
│                   │   Nginx 网关    │                               │
│                   │  (负载均衡/路由) │                               │
│                   └────────┬────────┘                               │
│                            │                                        │
└────────────────────────────┼────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                            ▼              服务层                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    Spring Boot 微服务                         │   │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐  │   │
│  │  │ 会员服务   │ │ 预约服务   │ │ 消费服务   │ │ 活动服务   │  │   │
│  │  │ Member     │ │Appointment │ │Consumption │ │ Campaign   │  │   │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘  │   │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────────────────────┐  │   │
│  │  │ 美容师服务 │ │ 充值服务   │ │ 服务项目Service           │  │   │
│  │  │ Beautician │ │ Recharge   │ │                            │  │   │
│  │  └────────────┘ └────────────┘ └────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                            │                                        │
└────────────────────────────┼────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                            ▼                数据层                   │
│         ┌──────────────────┼──────────────────┐                      │
│         ▼                  ▼                  ▼                      │
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                 │
│  │   MySQL    │    │   Redis    │    │   文件存储  │                 │
│  │  (主数据库) │    │  (缓存/会话) │    │  (七牛云)  │                 │
│  └────────────┘    └────────────┘    └────────────┘                 │
│                                                                  数据层│
└─────────────────────────────────────────────────────────────────────┘
```

---

## 二、技术选型说明

### 2.1 前端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue.js | 3.x | 渐进式JavaScript框架，组件化开发 |
| 多端框架 | uni-app | 3.x | 一套代码编译到微信/支付宝/H5等多端 |
| 状态管理 | Pinia | 2.x | Vue3推荐的状态管理库 |
| 路由 | Vue Router | 4.x | Vue官方路由管理器 |
| HTTP客户端 | Axios | 1.x | HTTP请求库，支持拦截器 |
| UI组件库 | uView | 2.x | uni-app生态的UI组件库 |
| 构建工具 | Vite | 4.x | 下一代前端构建工具 |

### 2.2 后端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Spring Boot | 2.7.x | 快速构建Spring应用 |
| ORM | MyBatis-Plus | 3.5.x | MyBatis增强，简化CRUD |
| 数据库 | MySQL | 8.0 | 关系型数据库 |
| 缓存 | Redis | 6.x | 高性能缓存/会话存储 |
| 安全 | Spring Security | 5.x | 认证授权框架 |
| JWT | jjwt | 0.11.x | JSON Web Token实现 |
| 日志 | SLF4J + Logback | - | 日志框架 |
| Java | OpenJDK | 17 | LTS版本 |

### 2.3 基础设施

| 组件 | 技术 | 说明 |
|------|------|------|
| 网关 | Nginx | 负载均衡、反向代理、静态资源服务 |
| 容器 | Docker | 应用容器化部署 |
| CI/CD | Jenkins | 持续集成/持续部署 |

---

## 三、数据库 ER 图

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│    service      │       │  pos_campaign   │       │   beautician    │
│   (服务项目)     │       │    (活动)        │       │   (美容师)       │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)         │
│ name            │       │ code            │       │ code            │
│ category        │       │ name            │       │ name            │
│ price           │       │ type            │       │ phone           │
│ duration        │       │ start_date      │       │ avatar          │
│ description     │       │ end_date        │       │ specialty       │
│ status          │       │ discount_type   │       │ level           │
│ create_time     │       │ discount_value  │       │ rating          │
└────────┬────────┘       │ min_amount      │       │ service_count   │
         │                 │ status          │       │ status          │
         │                 │ target_type     │       │ join_date       │
         │                 └────────┬────────┘       └────────┬────────┘
         │                          │                          │
         │    ┌─────────────────────┘                          │
         │    │                                                  │
         │    ▼                                                  │
┌─────────────────┐                          ┌─────────────────┐
│   appointment   │◄─────────────────────────│     member      │
│    (预约)       │                          │    (会员)       │
├─────────────────┤       1:N               ├─────────────────┤
│ id (PK)         │─────────────────────────►│ id (PK)         │
│ member_id (FK)  │                          │ name            │
│ beautician_id  │       N:1                │ phone           │
│ service_id (FK)│◄─────────────────────────│ gender          │
│ appointment_time│       ┌──────────────┐  │ birthday        │
│ status          │       │beautician_id │  │ level           │
│ remarks         │       └──────────────┘  │ balance         │
│ create_time     │                          │ gift_balance    │
└────────┬────────┘                          │ points          │
         │                                   │ total_consume   │
         │                                   │ create_time     │
         │                                   └────────┬────────┘
         │                                            │
         │                                            │
         ▼                                            ▼
┌─────────────────┐                          ┌─────────────────┐
│  consumption    │                          │  pos_recharge   │
│   (消费记录)    │                          │   (充值记录)    │
├─────────────────┤                          ├─────────────────┤
│ id (PK)         │                          │ id (PK)         │
│ member_id (FK)  │                          │ recharge_no     │
│ appointment_id │                          │ customer_id     │
│ amount          │                          │ amount          │
│ discount        │                          │ gift_amount     │
│ actual_amount   │                          │ pay_type        │
│ service_id (FK) │                          │ operator_id     │
│ create_time     │                          │ campaign_id     │
└─────────────────┘                          │ before_balance  │
                                              │ after_balance   │
                                              │ create_time     │
                                              └────────┬────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │   pos_coupon    │
                                              │   (优惠券)       │
                                              ├─────────────────┤
                                              │ id (PK)         │
                                              │ code            │
                                              │ name            │
                                              │ type            │
                                              │ discount_type   │
                                              │ min_amount      │
                                              │ discount_value  │
                                              │ valid_type      │
                                              │ total_count     │
                                              │ remain_count    │
                                              │ status          │
                                              └────────┬────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │pos_customer_    │
                                              │coupon           │
                                              │(客户优惠券)      │
                                              ├─────────────────┤
                                              │ id (PK)         │
                                              │ customer_id     │
                                              │ coupon_id (FK)  │
                                              │ code            │
                                              │ status          │
                                              │ receive_time    │
                                              │ use_time        │
                                              │ expired_time    │
                                              └─────────────────┘
```

---

## 四、业务流程设计

### 4.1 会员消费完整流程

```
新客注册 → 预约服务 → 到店确认 → 美容师接单 → 服务完成 → 收银结算（余额/现金/混合） → 积分累计 → 会员升级判断 → 发送评价邀请
```

### 4.2 充值完整流程

```
选择充值金额 → 确认赠送方案 → 支付（扫码/现金） → 充值到账 → 发送通知 → 记录流水
```

### 4.3 活动执行流程

```
创建活动（设置规则/时间/范围） → 活动上线 → 顾客参与（领取/下单） → 核销使用 → 效果统计
```

### 4.4 优惠券完整生命周期

```
创建优惠券 → 发放优惠券（手动/自动/领取） → 客户领取 → 核销使用 → 过期处理
```

---

---

## 五、数据库表结构设计

### 5.1 会员表 (member)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| phone | VARCHAR(20) | UNIQUE, NOT NULL | 手机号 |
| gender | TINYINT | DEFAULT 0 | 性别: 0-未知, 1-男, 2-女 |
| birthday | DATE | NULL | 生日 |
| level | INT | DEFAULT 1 | 会员等级 |
| balance | DECIMAL(10,2) | DEFAULT 0.00 | 账户余额 |
| gift_balance | DECIMAL(10,2) | DEFAULT 0.00 | 赠送金余额 |
| points | INT | DEFAULT 0 | 积分 |
| total_consume | DECIMAL(12,2) | DEFAULT 0.00 | 累计消费金额 |
| total_points_earned | INT | DEFAULT 0 | 累计获得积分 |
| total_points_used | INT | DEFAULT 0 | 累计使用积分 |
| status | TINYINT | DEFAULT 1 | 状态: 1-正常, 0-禁用 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 会员等级根据累计消费金额或积分划分，不同等级享有不同折扣
- 积分获取：消费得积分（按实际支付金额×等级积分倍率计算）、活动奖励、签到奖励
- 积分使用：积分抵现（100积分=1元）、积分兑换礼品、积分抽奖
- 充值赠送金单独核算，消费时优先扣除赠送金

### 5.2 预约表 (appointment)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| member_id | BIGINT | FK, NOT NULL | 会员ID |
| beautician_id | BIGINT | FK, NOT NULL | 美容师ID |
| service_id | BIGINT | FK, NOT NULL | 服务项目ID |
| appointment_time | DATETIME | NOT NULL | 预约时间 |
| status | TINYINT | DEFAULT 1 | 状态: 1-待确认, 2-已确认, 3-服务中, 4-已完成, 5-已取消, 6-爽约 |
| remarks | VARCHAR(255) | NULL | 备注 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 顾客可指定美容师或由系统自动分配空闲美容师
- 预约成功后发送短信/小程序消息提醒
- 顾客可在预约时间前N小时（如2小时）免费取消或改期
- 支持候补预约队列，有取消时自动通知候补顾客

### 5.3 充值记录表 (recharge)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| member_id | BIGINT | FK, NOT NULL | 会员ID |
| amount | DECIMAL(10,2) | NOT NULL | 充值金额 |
| gift_amount | DECIMAL(10,2) | DEFAULT 0.00 | 赠送金额 |
| payment_method | VARCHAR(20) | NOT NULL | 支付方式: cash/wechat/alipay |
| operator_id | BIGINT | NOT NULL | 操作员ID |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 5.4 消费记录表 (consumption)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| member_id | BIGINT | FK, NOT NULL | 会员ID |
| appointment_id | BIGINT | FK, NULL | 预约ID |
| service_id | BIGINT | FK, NOT NULL | 服务项目ID |
| amount | DECIMAL(10,2) | NOT NULL | 原价 |
| discount | DECIMAL(5,2) | DEFAULT 1.00 | 折扣率 |
| actual_amount | DECIMAL(10,2) | NOT NULL | 实付金额 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 5.5 活动表 (pos_campaign)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| type | VARCHAR(20) | NOT NULL | 活动类型: discount/coupon/gift |
| start_date | DATE | NOT NULL | 开始日期 |
| end_date | DATE | NOT NULL | 结束日期 |
| discount | DECIMAL(5,2) | NULL | 折扣力度 |
| description | TEXT | NULL | 活动描述 |
| status | TINYINT | DEFAULT 1 | 状态: 1-进行中, 0-已结束 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 5.6 服务项目表 (service)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL | 项目名称 |
| category | VARCHAR(50) | NOT NULL | 分类: haircut/perming/hair_color |
| price | DECIMAL(10,2) | NOT NULL | 价格 |
| duration | INT | NOT NULL | 服务时长(分钟) |
| description | TEXT | NULL | 项目描述 |
| image_url | VARCHAR(255) | NULL | 图片URL |
| status | TINYINT | DEFAULT 1 | 状态: 1-上架, 0-下架 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 5.7 美容师表 (beautician)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 美容师ID |
| code | VARCHAR(20) | UNIQUE, NOT NULL | 工号 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| phone | VARCHAR(20) | NOT NULL | 手机号 |
| avatar | VARCHAR(255) | NULL | 头像URL |
| gender | TINYINT | DEFAULT 0 | 性别: 0-女, 1-男, 2-未知 |
| specialty | VARCHAR(200) | NULL | 擅长领域 |
| introduction | VARCHAR(500) | NULL | 个人简介 |
| level | TINYINT | NOT NULL, DEFAULT 1 | 等级: 1-初级, 2-中级, 3-高级, 4-首席 |
| rating | DECIMAL(3,2) | NOT NULL, DEFAULT 5.00 | 评分 |
| service_count | INT | NOT NULL, DEFAULT 0 | 服务次数 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 0-离职, 1-在职, 2-休假 |
| join_date | DATE | NULL | 入职日期 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 删除标记: 0-未删除, 1-已删除 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 美容师档案需支持姓名、工号、性别、手机号、照片、入职日期、擅长领域、技能等级
- 技能标签用于顾客和系统匹配预约
- 绩效统计按月/季/年统计预约量、好评率、服务时长、销售额

---

### 4.8 活动表 (pos_campaign)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 活动ID |
| code | VARCHAR(20) | UNIQUE, NOT NULL | 活动编码 |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| type | TINYINT | NOT NULL | 活动类型: 1-折扣, 2-满减, 3-赠品, 4-套餐 |
| start_date | DATE | NOT NULL | 开始日期 |
| end_date | DATE | NOT NULL | 结束日期 |
| discount_type | TINYINT | DEFAULT 1 | 优惠类型: 1-折扣率, 2-固定金额 |
| discount_value | DECIMAL(10,2) | NULL | 优惠值(折扣率或固定金额) |
| min_amount | DECIMAL(10,2) | DEFAULT 0.00 | 最低消费金额 |
| description | VARCHAR(500) | NULL | 活动描述 |
| image_url | VARCHAR(255) | NULL | 活动图片 |
| target_type | TINYINT | DEFAULT 1 | 适用对象: 1-全部, 2-新客, 3-会员 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 0-未启用, 1-进行中, 2-已结束 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 删除标记: 0-未删除, 1-已删除 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 支持限时折扣、满额满减、满件折扣、组合套餐、第二件半价等活动类型
- 活动时段支持设置重复规则（如每周五会员日）
- 可指定活动适用门店和适用人群（会员等级限制）

---

### 4.9 充值记录表 (pos_recharge)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 充值ID |
| recharge_no | VARCHAR(32) | UNIQUE, NOT NULL | 充值单号 |
| customer_id | BIGINT | NOT NULL | 客户ID |
| amount | DECIMAL(10,2) | NOT NULL | 充值金额 |
| gift_amount | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00 | 赠送金额 |
| pay_type | TINYINT | NOT NULL | 支付方式: 1-现金, 2-微信, 3-支付宝, 4-银行卡 |
| operator_id | BIGINT | NOT NULL | 操作员ID |
| before_balance | DECIMAL(10,2) | NOT NULL | 充值前余额 |
| after_balance | DECIMAL(10,2) | NOT NULL | 充值后余额 |
| campaign_id | BIGINT | NULL | 参与活动ID |
| remark | VARCHAR(500) | NULL | 备注 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 删除标记: 0-未删除, 1-已删除 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 支持多档充值金额（如500/1000/2000/5000元）及自定义金额
- 充值赠送类型：满送赠送、比例赠送、积分赠送、阶梯赠送
- 赠送金实时到账，支持设置有效期
- 消费时优先扣除赠送金，再扣除本金余额

---

### 4.10 优惠券表 (pos_coupon)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 优惠券ID |
| code | VARCHAR(32) | UNIQUE, NOT NULL | 优惠券码 |
| name | VARCHAR(100) | NOT NULL | 优惠券名称 |
| type | TINYINT | NOT NULL | 优惠券类型: 1-满减券, 2-折扣券, 3-兑换券 |
| discount_type | TINYINT | NOT NULL | 优惠方式: 1-满减, 2-折扣 |
| min_amount | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00 | 使用门槛(满X元) |
| discount_value | DECIMAL(10,2) | NOT NULL | 优惠值 |
| discount_rate | DECIMAL(5,2) | NULL | 折扣率(%) |
| valid_type | TINYINT | NOT NULL | 有效期类型: 1-固定日期, 2-领取后N天 |
| start_date | DATE | NULL | 有效期开始 |
| end_date | DATE | NULL | 有效期结束 |
| valid_days | INT | NULL | 领取后有效天数 |
| total_count | INT | NOT NULL | 发放总数 |
| remain_count | INT | NOT NULL | 剩余数量 |
| per_limit | INT | NOT NULL, DEFAULT 1 | 每人限领 |
| receive_count | INT | NOT NULL, DEFAULT 0 | 已领取数量 |
| use_count | INT | NOT NULL, DEFAULT 0 | 已使用数量 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 0-未启用, 1-启用, 2-已下架 |
| target_type | TINYINT | DEFAULT 1 | 适用对象: 1-全部, 2-新人, 3-会员 |
| service_ids | VARCHAR(500) | NULL | 适用服务ID列表(逗号分隔) |
| image_url | VARCHAR(255) | NULL | 优惠券图片 |
| description | VARCHAR(500) | NULL | 使用说明 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 删除标记: 0-未删除, 1-已删除 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 优惠券类型：满减券、折扣券、现金券、项目体验券
- 发放方式：管理员手动发放、顾客自助领取、注册/消费/活动自动发放
- 核销规则：有效期（固定日期/领取后N天）、每人限领数量、适用服务/产品

---

### 4.11 客户优惠券表 (pos_customer_coupon)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 客户优惠券ID |
| customer_id | BIGINT | NOT NULL | 客户ID |
| coupon_id | BIGINT | NOT NULL | 优惠券ID |
| code | VARCHAR(32) | NOT NULL | 领取的优惠券码 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 1-未使用, 2-已使用, 3-已过期 |
| receive_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 领取时间 |
| use_time | DATETIME | NULL | 使用时间 |
| order_id | BIGINT | NULL | 使用的订单ID |
| expired_time | DATETIME | NOT NULL | 过期时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 删除标记: 0-未删除, 1-已删除 |
| create_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**需求说明**：
- 每张优惠券记录发放对象、发放时间、使用时间、使用订单
- 支持优惠券领取、核销、过期状态的完整生命周期管理

---

## 六、API 接口设计

### 5.1 接口规范

- **基础路径**: `/api/v1`
- **认证方式**: Bearer Token (JWT)
- **请求格式**: `Content-Type: application/json`
- **响应格式**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- **分页格式**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

---

### 6.2 会员管理 (Member)

#### 创建会员
```
POST /api/v1/members
```

**请求参数**:
```json
{
  "name": "张三",
  "phone": "13800138000",
  "gender": 1,
  "birthday": "1990-01-01",
  "level": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01",
    "level": 1,
    "balance": 0.00,
    "points": 0,
    "status": 1,
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 查询会员列表
```
GET /api/v1/members
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 姓名(模糊查询) |
| phone | String | 手机号 |
| level | Integer | 会员等级 |
| status | Integer | 状态 |
| page | Integer | 页码(默认1) |
| size | Integer | 每页条数(默认10) |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "phone": "13800138000",
        "gender": 1,
        "level": 1,
        "balance": 100.00,
        "points": 50
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

#### 获取会员详情
```
GET /api/v1/members/{id}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01",
    "level": 1,
    "balance": 100.00,
    "points": 50,
    "status": 1,
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 更新会员
```
PUT /api/v1/members/{id}
```

**请求参数**:
```json
{
  "name": "张三",
  "gender": 2,
  "birthday": "1990-06-01",
  "level": 2
}
```

#### 删除会员
```
DELETE /api/v1/members/{id}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 6.3 预约管理 (Appointment)

#### 创建预约
```
POST /api/v1/appointments
```

**请求参数**:
```json
{
  "memberId": 1,
  "beauticianId": 1,
  "serviceId": 1,
  "appointmentTime": "2026-04-30 10:00:00",
  "remarks": "需要染发"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "memberId": 1,
    "beauticianId": 1,
    "serviceId": 1,
    "appointmentTime": "2026-04-30 10:00:00",
    "status": 1,
    "remarks": "需要染发",
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 查询预约列表
```
GET /api/v1/appointments
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| memberId | Long | 会员ID |
| beauticianId | Long | 美容师ID |
| status | Integer | 状态 |
| startDate | String | 开始日期 |
| endDate | String | 结束日期 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

#### 获取预约详情
```
GET /api/v1/appointments/{id}
```

#### 更新预约
```
PUT /api/v1/appointments/{id}
```

**请求参数**:
```json
{
  "beauticianId": 2,
  "appointmentTime": "2026-04-30 14:00:00",
  "status": 2,
  "remarks": "改期为下午"
}
```

#### 删除预约
```
DELETE /api/v1/appointments/{id}
```

#### 更新预约状态
```
PATCH /api/v1/appointments/{id}/status
```

**请求参数**:
```json
{
  "status": 3
}
```

---

### 6.4 充值管理 (Recharge)

#### 创建充值记录
```
POST /api/v1/recharges
```

**请求参数**:
```json
{
  "memberId": 1,
  "amount": 1000.00,
  "giftAmount": 100.00,
  "paymentMethod": "wechat",
  "operatorId": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "memberId": 1,
    "amount": 1000.00,
    "giftAmount": 100.00,
    "paymentMethod": "wechat",
    "operatorId": 1,
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 查询充值记录列表
```
GET /api/v1/recharges
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| memberId | Long | 会员ID |
| paymentMethod | String | 支付方式 |
| startDate | String | 开始日期 |
| endDate | String | 结束日期 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

#### 获取充值记录详情
```
GET /api/v1/recharges/{id}
```

#### 删除充值记录
```
DELETE /api/v1/recharges/{id}
```

---

### 6.5 活动管理 (Campaign)

#### 创建活动
```
POST /api/v1/campaigns
```

**请求参数**:
```json
{
  "name": "五一特惠",
  "type": "discount",
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "discount": 0.80,
  "description": "五一劳动节全场8折"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "五一特惠",
    "type": "discount",
    "startDate": "2026-05-01",
    "endDate": "2026-05-05",
    "discount": 0.80,
    "description": "五一劳动节全场8折",
    "status": 1,
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 查询活动列表
```
GET /api/v1/campaigns
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 活动名称(模糊查询) |
| type | String | 活动类型 |
| status | Integer | 状态 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

#### 获取活动详情
```
GET /api/v1/campaigns/{id}
```

#### 更新活动
```
PUT /api/v1/campaigns/{id}
```

**请求参数**:
```json
{
  "name": "五一特惠-延长",
  "endDate": "2026-05-10",
  "discount": 0.75
}
```

#### 删除活动
```
DELETE /api/v1/campaigns/{id}
```

---

### 6.6 美容师管理 (Beautician)

#### 获取美容师详情
```
GET /api/v1/beauticians/{id}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "code": "B001",
    "name": "李美容",
    "phone": "13900139001",
    "avatar": "https://example.com/avatar.jpg",
    "gender": 0,
    "specialty": "面部护理,美甲",
    "introduction": "资深美容师，擅长面部护理和美甲",
    "level": 3,
    "rating": 4.90,
    "serviceCount": 156,
    "status": 1,
    "joinDate": "2024-01-15"
  }
}
```

#### 查询美容师列表
```
GET /api/v1/beauticians
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 姓名(模糊查询) |
| status | Integer | 状态 |
| specialty | String | 擅长领域 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

---

### 6.7 优惠券管理 (Coupon)

#### 创建优惠券
```
POST /api/v1/coupons
```

**请求参数**:
```json
{
  "name": "新人50元券",
  "type": 1,
  "discountType": 1,
  "minAmount": 200.00,
  "discountValue": 50.00,
  "validType": 1,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "totalCount": 1000,
  "remainCount": 1000,
  "perLimit": 1,
  "targetType": 2,
  "description": "满200元减50元，新人专享"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "code": "CPN001",
    "name": "新人50元券",
    "type": 1,
    "status": 1,
    "createTime": "2026-04-29 00:31:00"
  }
}
```

#### 查询优惠券列表
```
GET /api/v1/coupons
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 优惠券名称(模糊查询) |
| type | Integer | 优惠券类型 |
| status | Integer | 状态 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

#### 领取优惠券
```
POST /api/v1/coupons/{id}/receive
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "couponId": 1,
    "code": "UCN00001",
    "status": 1,
    "expiredTime": "2026-12-31 23:59:59"
  }
}
```

#### 使用优惠券
```
POST /api/v1/coupons/use
```

**请求参数**:
```json
{
  "customerCouponId": 1,
  "orderId": 100
}
```

#### 查询客户优惠券列表
```
GET /api/v1/customer-coupons
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| customerId | Long | 客户ID |
| status | Integer | 状态: 1-未使用, 2-已使用, 3-已过期 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

---

### 6.8 服务项目管理 (Service)

#### 查询服务项目列表
```
GET /api/v1/services
```

**查询参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| name | String | 项目名称(模糊查询) |
| category | String | 分类 |
| status | Integer | 状态 |
| page | Integer | 页码 |
| size | Integer | 每页条数 |

#### 获取服务项目详情
```
GET /api/v1/services/{id}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "面部护理",
    "category": "facial",
    "price": 299.00,
    "duration": 60,
    "description": "深层清洁面部护理，包含卸妆、洁面、按摩、面膜",
    "imageUrl": "https://example.com/service.jpg",
    "status": 1
  }
}
```

---

## 七、错误码规范

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token过期 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 八、安全设计

1. **JWT认证**: 所有API需携带有效Token访问
2. **密码加密**: 使用BCrypt加密存储
3. **接口限流**: 防止恶意请求
4. **数据校验**: 使用JSR-303进行参数校验
5. **SQL注入防护**: MyBatis-Plus参数绑定

---

## 九、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.1.0 | 2026-04-29 | 补充美容师、活动、充值、优惠券表结构设计；添加业务流程设计；完善ER图 |
| 1.0.0 | 2026-04-29 | 初始版本 |
