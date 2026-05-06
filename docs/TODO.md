# Beauty Salon UniApp 开发任务清单

> 创建时间: 2026-05-02
> 项目路径: `/home/phy/IdeaProjects/PHY/beauty-salon/uniapp`
> 后端API: `/home/phy/IdeaProjects/PHY/beauty-salon/backend`

---

## 📋 功能模块

### 1. 首页 (home) ✅
- [x] Banner 轮播图（3张活动 banner，支持自动轮播）
- [x] 快捷入口（在线预约、会员码、优惠券、我的订单）
- [x] 热门服务展示（横向滚动，推荐美容师）
- [x] API 对接（降级到静态数据）
- [x] 下拉刷新支持

### 2. 预约流程 (booking) ✅
#### 2.1 选择服务 ✅
- [x] 服务分类 Tab 切换（API 动态加载分类）
- [x] 服务列表展示（API 降级到静态数据）
- [x] 多选服务（Pinia 状态管理）
- [x] 上拉加载更多
- [x] API 对接完成

#### 2.2 选择美容师 ✅
- [x] 美容师列表（API 降级到静态数据）
- [x] 评分、服务次数展示
- [x] 擅长领域标签
- [x] 等级标签显示
- [x] API 对接完成

#### 2.3 选择时间 ✅
- [x] 日期选择（未来14天）
- [x] 时间段选择
- [x] 不可预约时段灰显（API 获取已约时间段）
- [x] API 对接完成

#### 2.4 确认预约 ✅
- [x] 预约信息汇总
- [x] 客户信息输入（姓名、手机号）
- [x] 备注输入
- [x] 提交预约（createAppointment API）
- [x] 预约成功弹窗
- [x] 自动跳转到预约记录

### 3. 预约记录 (appointments) ✅
- [x] Tab 分类筛选（全部/待服务/服务中/已完成/已取消）
- [x] 预约列表展示
- [x] 状态标签展示
- [x] 空状态展示
- [x] 下拉刷新
- [x] 上拉加载更多
- [x] 取消预约功能
- [x] API 对接完成

### 4. 会员中心 (member) ✅
- [x] 会员卡片展示（渐变背景）
- [x] 积分、余额、优惠券数量统计
- [x] 会员权益展示（折扣、生日礼包等）
- [x] 会员码弹窗
- [x] 充值功能（支持多档位）
- [x] 菜单导航（个人信息、预约记录、订单、积分明细）
- [x] API 对接完成

### 5. 优惠券 (coupons) ✅
- [x] Tab 分类筛选（可用/已用/已过期）
- [x] 优惠券列表展示（面值、满减条件、有效期）
- [x] 状态区分（颜色+标签）
- [x] 立即使用按钮
- [x] 下拉刷新 + 上拉加载
- [x] API 对接完成

### 6. 登录/注册 (login) ✅
- [x] 登录/注册 Tab 切换
- [x] 手机号 + 密码登录
- [x] 验证码发送倒计时
- [x] 新用户注册
- [x] 用户协议勾选
- [x] Token 持久化存储

### 7. 我的订单 (orders) ✅
- [x] Tab 分类筛选（全部/待支付/已支付/已完成/已取消）
- [x] 订单列表展示
- [x] 订单详情（服务项目、数量、价格）
- [x] 状态标签展示
- [x] 去支付、取消订单功能
- [x] API 对接完成

---

## 🔧 技术任务

### 基础架构 ✅
- [x] 项目结构搭建
- [x] Vite + UniApp 配置
- [x] Pinia 状态管理
- [x] pages.json 路由配置
- [x] 全局样式变量
- [x] Store 持久化（booking/member）
- [x] 登录状态自动初始化

### API 层 ✅
- [x] request.js 封装（token、拦截器）
- [x] API 模块化
  - [x] auth.js - 认证
  - [x] service.js - 服务项目
  - [x] beautician.js - 美容师
  - [x] appointment.js - 预约
  - [x] coupon.js - 优惠券
  - [x] member.js - 会员/客户
  - [x] order.js - 订单
  - [x] api/index.js - 统一导出

### UI 组件库 ✅
- [x] btn 按钮组件
- [x] modal 弹窗组件
- [x] loading 加载组件
- [x] toast 提示工具函数

### 平台适配 ✅
- [x] H5 配置（hash 路由）
- [x] 微信小程序配置模板
- [x] manifest.json 平台配置

### 微信功能 ✅
- [x] 微信支付工具（pay.js）
- [x] 模拟支付（测试用）

---

## 📱 平台适配

### H5 ✅
- [x] 基础页面搭建
- [x] Hash 路由模式
- [x] 开发服务器配置

### 微信小程序 ✅
- [x] manifest.json 配置模板
- [x] appid 占位配置
- [x] 权限配置（位置）
- [x] 微信支付占位实现

---

## 🐛 问题修复

- [x] 预约流程状态管理优化（刷新丢失）- ✅ 已用 localStorage 持久化
- [x] TabBar 选中状态同步 - ✅ pages.json 配置
- [x] iOS 安全区域适配 - ✅ 使用 env(safe-area-inset-bottom)

---

## 📝 待优化

- [ ] 骨架屏
- [ ] 页面过渡动画
- [ ] 错误边界处理
- [ ] 微信分享配置

---

## 🚀 下一阶段任务

### 优先级 P0（已完成）
1. ✅ ~~登录注册流程~~ → 完成
2. ✅ ~~首页轮播图~~ → 完成
3. ✅ ~~订单页面~~ → 完成
4. ✅ ~~UI组件库~~ → 完成
5. ✅ ~~微信适配~~ → 完成
6. ✅ ~~微信支付~~ → 完成（占位实现）

### 优先级 P1（可选增强）
1. 微信分享配置（朋友/朋友圈）
2. 订阅消息集成
3. 地图定位门店
4. 消息推送

---

## 后端 API 参考

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 服务 | `/api/pos/service/list` | GET | 分页查询服务 |
| 服务 | `/api/pos/service/categories` | GET | 获取所有分类 |
| 服务 | `/api/pos/service/{id}` | GET | 服务详情 |
| 服务 | `/api/pos/service/active` | GET | 上架服务列表 |
| 美容师 | `/api/beautician/list` | GET | 美容师列表 |
| 美容师 | `/api/beautician/{id}` | GET | 美容师详情 |
| 美容师 | `/api/beautician/active` | GET | 在职美容师 |
| 预约 | `/api/appointment/list` | GET | 预约列表 |
| 预约 | `/api/appointment` | POST | 创建预约 |
| 预约 | `/api/appointment/{id}` | PUT | 更新预约 |
| 预约 | `/api/appointment/{id}/cancel` | PUT | 取消预约 |
| 预约 | `/api/appointment/available-slots` | GET | 可用时间段 |
| 预约 | `/api/appointment/check-conflict` | GET | 冲突检测 |
| 优惠券 | `/api/coupon/list` | GET | 优惠券列表 |
| 优惠券 | `/api/coupon/{id}` | GET | 优惠券详情 |
| 优惠券 | `/api/coupon/receive` | POST | 领取优惠券 |
| 优惠券 | `/api/coupon/customer/{id}` | GET | 客户优惠券 |
| 会员 | `/api/crm/customer/{id}` | GET | 客户详情 |
| 会员 | `/api/crm/customer/{id}/recharge` | POST | 账户充值 |
| 会员 | `/api/crm/customer/{id}/appointments` | GET | 客户预约记录 |
| 订单 | `/api/pos/order/list` | GET | 订单列表 |
| 订单 | `/api/pos/order/{id}` | GET | 订单详情 |
| 订单 | `/api/pos/order/customer/{id}` | GET | 客户订单列表 |
| 订单 | `/api/pos/order/{id}/pay` | PUT | 支付订单 |
| 订单 | `/api/pos/order/{id}/cancel` | PUT | 取消订单 |
| 认证 | `/api/auth/login` | POST | 登录 |
| 认证 | `/api/auth/register` | POST | 注册 |

---

## 项目文件结构

```
uniapp/src/
├── api/
│   ├── index.js          # 统一导出
│   ├── request.js        # 请求封装
│   ├── auth.js           # 认证
│   ├── service.js        # 服务
│   ├── beautician.js     # 美容师
│   ├── appointment.js    # 预约
│   ├── coupon.js         # 优惠券
│   ├── member.js         # 会员
│   └── order.js          # 订单
├── components/
│   ├── btn/              # 按钮组件
│   ├── modal/            # 弹窗组件
│   └── loading/          # 加载组件
├── pages/
│   ├── home/             # 首页
│   ├── booking/          # 预约流程
│   │   ├── index.vue     # 选择服务
│   │   ├── beautician/  # 选择美容师
│   │   ├── time/        # 选择时间
│   │   └── confirm/     # 确认预约
│   ├── appointments/     # 预约记录
│   ├── member/           # 会员中心
│   ├── coupons/          # 优惠券
│   ├── login/            # 登录/注册
│   └── orders/           # 我的订单
├── store/
│   ├── index.js          # Pinia 入口
│   ├── booking.js        # 预约状态（持久化）
│   └── member.js         # 会员状态
├── utils/
│   ├── request.js        # 请求工具
│   └── pay.js            # 微信支付工具
├── App.vue
├── main.js
├── pages.json
└── manifest.json
```

---

## 进度统计

| 模块 | 完成度 | 状态 |
|------|--------|------|
| 首页 | 100% | ✅ |
| 预约流程 | 100% | ✅ |
| 预约记录 | 100% | ✅ |
| 会员中心 | 100% | ✅ |
| 优惠券 | 100% | ✅ |
| 登录/注册 | 100% | ✅ |
| 我的订单 | 100% | ✅ |
| API层 | 100% | ✅ |
| UI组件库 | 100% | ✅ |
| 微信适配 | 100% | ✅ |
| 微信支付 | 100% | ✅ |

---

**开发服务器：** `npm run dev:h5` → http://localhost:5174/

**小程序编译：** `npm run dev:mp-weixin` → 使用微信开发者工具打开 `dist/dev/mp-weixin`

---

*最后更新: 2026-05-02*
