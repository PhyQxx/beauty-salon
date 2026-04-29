# 美容沙龙管理系统 (Beauty Salon Management System)

一套完整的美容沙龙业务管理系统，包含前端 Vue3 和后端 Spring Boot。

## 项目结构

```
beauty-salon/
├── backend/                         # 后端 Spring Boot 项目
│   ├── pom.xml                      # Maven 依赖配置
│   └── src/main/
│       ├── java/com/beautysalon/
│       │   ├── BeautySalonApplication.java   # 启动类
│       │   ├── controller/                   # 控制器层
│       │   │   ├── SysUserController.java     # 系统用户管理
│       │   │   ├── CrmCustomerController.java # CRM客户管理
│       │   │   ├── PosServiceController.java  # 服务项目管理
│       │   │   ├── PosMembershipCardController.java # 会员卡管理
│       │   │   ├── AppointmentController.java # 预约管理
│       │   │   └── PosOrderController.java    # 订单管理
│       │   ├── service/                      # 服务层
│       │   ├── mapper/                       # 数据访问层
│       │   ├── entity/                       # 实体类
│       │   ├── dto/                           # 数据传输对象
│       │   ├── vo/                            # 视图对象
│       │   ├── config/                        # 配置类
│       │   └── common/                        # 公共类
│       └── resources/
│           ├── application.yml               # 应用配置
│           └── db/migration/
│               └── V1__init_schema.sql       # 数据库初始化脚本
│
├── frontend/                        # 前端 Vue3 项目
│   ├── package.json                # NPM 依赖配置
│   ├── vite.config.js              # Vite 配置
│   └── src/
│       ├── main.js                 # Vue 入口文件
│       ├── App.vue                 # 根组件
│       ├── router/                 # 路由配置
│       ├── store/                  # 状态管理
│       ├── api/                    # API 接口封装
│       │   ├── sys/user.js         # 用户相关接口
│       │   ├── crm/customer.js     # 客户相关接口
│       │   ├── pos/service.js      # 服务项目接口
│       │   ├── pos/membershipCard.js # 会员卡接口
│       │   ├── appointment/        # 预约接口
│       │   └── pos/order.js        # 订单接口
│       ├── utils/                  # 工具函数
│       ├── views/                  # 页面组件
│       └── assets/                 # 静态资源
│
└── README.md                       # 项目说明文档
```

## 技术栈

### 后端技术栈
- **Spring Boot 2.7.18** - 核心框架
- **MyBatis-Plus 3.5.3.1** - ORM框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存和Session存储
- **JWT** - 身份认证
- **Swagger (SpringFox 1.6.2)** - API文档
- **Lombok** - 简化代码

### 前端技术栈
- **Vue 3.3** - 渐进式JavaScript框架
- **Vue Router 4.2** - 路由管理
- **Pinia 2.1** - 状态管理
- **Element Plus 2.3** - UI组件库
- **Axios** - HTTP客户端
- **Vite 4.4** - 构建工具

## 功能模块

| 模块 | 说明 |
|------|------|
| 系统管理 | 用户管理、角色管理、权限管理 |
| CRM客户管理 | 客户信息、会员等级、积分管理 |
| 预约管理 | 预约创建、状态变更、日历视图 |
| 服务项目管理 | 服务项目维护、分类管理 |
| 会员卡管理 | 会员卡/套餐管理 |
| 订单管理 | 服务订单、充值、报表统计 |

## 数据库表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| crm_customer | 客户表 |
| pos_service | 服务项目表 |
| pos_membership_card | 会员卡/套餐表 |
| appointment | 预约记录表 |
| pos_order | 订单表 |
| pos_order_item | 订单明细表 |

## 快速开始

### 环境要求
- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Redis 5.0+

### 后端启动

1. 创建数据库并导入初始化脚本
```bash
mysql -u root -p < backend/src/main/resources/db/migration/V1__init_schema.sql
```

2. 修改数据库配置
编辑 `backend/src/main/resources/application.yml` 中的数据库连接信息。

3. 编译并运行
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将运行在 http://localhost:8080/api

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将运行在 http://localhost:3000

### 访问系统

- 前端地址: http://localhost:3000
- API文档: http://localhost:8080/api/swagger-ui/
- 默认管理员账号: admin / admin123

## 数据库配置

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/beauty_salon
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

## API 文档

启动后端服务后，访问 Swagger UI 查看完整API文档：
```
http://localhost:8080/api/swagger-ui/
```

## 开发说明

### 后端开发
- Controller 层负责请求处理和参数校验
- Service 层负责业务逻辑处理
- Mapper 层负责数据库操作
- 使用 MyBatis-Plus 的自动填充功能处理创建时间和更新时间

### 前端开发
- API 接口封装在 `src/api/` 目录下
- 页面组件放在 `src/views/` 目录下
- 使用 Element Plus 组件库构建UI

## 许可证

本项目仅供学习参考使用。

## 联系方式

如有问题，请提交 Issue。
