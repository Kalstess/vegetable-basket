# 菜篮子工程车填报和展示系统

完整的 **多端全栈项目**，包含：
- **后端 API**：Spring Boot 3（目录 `backend/`）
- **Web 管理端**：Vue 3 + Vite + Element Plus（目录 `frontend/`）
- **微信小程序端**：用于企业现场填报（目录 `mini-program/`）

适用于“菜篮子工程车”相关企业、车辆、运输、合规、维护、问卷、反馈等数据的 **填报、管理和分析展示**。

---

## 功能概览

- **企业与账号管理**
  - 企业信息维护、搜索
  - 企业管理员/企业普通用户/司机等多角色体系
  - 企业自助注册（待商务委审批）、审批通过/驳回流程
- **车辆与司机管理**
  - 车辆基础信息管理
  - 车辆统计、车辆与企业关联
- **运行填报与统计**
  - 运输统计填报与汇总
  - 维护保养、合规运营、线路等表单填报
  - 问卷调查（运行问题、标准配置等）
- **数据分析与可视化**
  - 后台仪表盘与统计总览
  - 企业运输分析看板（`EnterpriseTransportDashboard.vue`）
  - 车辆年度统计、企业运输趋势等（Analytics 相关接口）
- **反馈与审计**
  - 企业需求/意见反馈
  - 反馈词云展示
  - 关键操作审计日志（`AuditLog`）

---

## 项目结构

```bash
cailanzi/
├── backend/                 # 后端（Spring Boot 3）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/cailanzi/
│       │   ├── config/      # 全局配置（安全拦截、Swagger、CORS等）
│       │   ├── controller/  # 控制器（企业、车辆、统计、注册审批等）
│       │   ├── dto/         # DTO/请求响应模型
│       │   ├── entity/      # JPA 实体
│       │   ├── repository/  # 数据访问层
│       │   ├── service/     # 业务服务
│       │   └── util/        # 工具类
│       └── main/resources/
│           └── application.properties
├── frontend/                # Web 管理前端（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/             # 所有前端 API 封装（含 companyRegistration、analytics 等）
│   │   ├── components/      # 公共组件（如词云、粒子标题）
│   │   ├── layouts/         # 布局
│   │   ├── router/          # 路由（含登录、注册、分析等路由）
│   │   ├── styles/
│   │   └── views/           # 各业务页面
│   ├── README.md            # 前端专用说明
│   └── QUICK_START.md       # 前端快速启动说明
├── mini-program/           # 微信小程序端
│   ├── apis/                # 小程序接口封装
│   ├── pages/               # 表单填报与统计页面
│   ├── config/index.js      # 后端 baseURL 配置
│   └── README.md            # 小程序使用说明
├── docs/                   # 项目文档
│   ├── 项目需求.txt
│   ├── 用户注册流程测试.md
│   ├── 字段整理.txt
│   └── cailanzi-localhost-dump.sql  # 数据库导出/备份
└── README.md               # 项目总览（本文件）
```

> 说明：项目已包含 Docker 配置文件，可直接使用 Docker Compose 进行部署。

---

## 🚀 服务器部署

### Docker 部署（推荐）

项目已配置完整的 Docker 部署方案，支持一键部署到服务器。

**详细部署文档**：请查看 [`docs/DEPLOYMENT_GUIDE.md`](docs/DEPLOYMENT_GUIDE.md)

# 手动部署
docker-compose up -d --build
```

**部署要求**：
- CentOS 7 或兼容的 Linux 系统
- **推荐配置**：2核CPU / 4GB内存 / 50GB硬盘
- **最低配置**：2核CPU / **2GB内存** / 50GB硬盘（适用于学习环境，并发10人左右）
- 已安装 Docker 和 Docker Compose（部署脚本可自动安装）

> **内存说明**：
> - **4GB 内存**：推荐配置，系统运行流畅
> - **2GB 内存**：可以使用，需使用 `docker-compose.low-memory.yml` 配置文件
> - 详细内存优化说明请查看：[内存优化指南](docs/DEPLOYMENT_GUIDE.md#53-低内存配置2gb-内存服务器)

---

## 技术栈

### 后端
- Spring Boot 3.3.x
- Spring Web / Spring MVC
- Spring Data JPA
- MySQL 8.0
- Lombok
- Springdoc OpenAPI 3（Swagger UI）
- JWT 认证

### Web 前端（`frontend/`）
- Vue 3 + Vite
- Vue Router
- Element Plus
- Axios
- （预留）Pinia 状态管理

### 微信小程序（`mini-program/`）
- 原生微信小程序
- 自封装 `utils/request.js` + `apis/*` 调用后端

---

## 环境要求

- JDK **17+**
- Maven **3.6+**
- Node.js **16+**（推荐 >= 18）
- MySQL **8.0+**
- 微信开发者工具（用于运行 `mini-program/`）

---

## 本地快速启动

### 1. 数据库准备

推荐使用 `docs/` 中的 SQL 备份：

```bash
# 示例（根据本地账号修改）
mysql -u root -p < docs/cailanzi_localhost-dump.sql
```

后端默认数据库配置（可在 `backend/src/main/resources/application.properties` 中调整）：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cailanzi?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=111111
```

> JPA 配置为 `update` 模式（自动根据实体更新表结构），生产环境建议改为手工迁移脚本。

---

### 2. 启动后端 API（`backend/`）

```bash
cd backend

# 启动开发环境
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/cailanzi-backend-0.0.1-SNAPSHOT.jar
```

- 后端监听地址：`http://localhost:8080`
- 上下文路径：`/api`
- **统一前缀**：所有接口完整路径形如 `http://localhost:8080/api/xxx`

常用接口示例：
- 登录：`POST /api/auth/login`
- 企业自助注册：`POST /api/company-registration/register`
- 商务委审批通过：`POST /api/company-registration/{companyId}/approve`

---

### 3. 启动 Web 管理前端（`frontend/`）

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产包
npm run build
```

- 开发环境默认地址：`http://localhost:3000`
- 已配置代理：当前端请求 `/api/**` 时，会自动转发到 `http://localhost:8080/api/**`

主要菜单/页面包括：
- 登录页：`/login`
- 企业自助注册：`/company-register`
- 仪表盘：`/dashboard`
- 企业管理、车辆管理、运输统计、维护保养、合规管理、问卷分析、反馈管理等模块

如需进一步前端开发细节，可参考：
- `frontend/README.md`
- `frontend/QUICK_START.md`

---

### 4. 启动微信小程序端（`mini-program/`）

1. 打开 **微信开发者工具**，选择“导入项目”，目录指向 `mini-program/`。
2. `AppID` 可先使用测试号 `touristappid`。
3. 修改后端地址：

   编辑 `mini-program/config/index.js`，将 `baseURL` 设置为后端地址，例如：

   ```js
   // mini-program/config/index.js
   module.exports = {
     baseURL: 'http://localhost:8080/api'
   }
   ```

4. 预览/真机调试时，确保手机能访问到后端服务地址。

更多小程序端说明见：`mini-program/README.md`。

---

## API 文档（Swagger）

后端启动后，可通过 Swagger UI 查看和调试接口：

- Swagger UI 地址：`http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/api/v3/api-docs`

---

## 业务流程提示

### 企业自助注册 & 审批

1. 企业在前端“企业自助注册”页面或通过接口提交注册信息：
   - `POST /api/company-registration/register`
2. 注册后企业状态为 `PENDING`，管理员账号为 **禁用** 状态。
3. 商务委/管理员登录后台后，在企业注册审批界面执行：
   - 审批通过：`POST /api/company-registration/{companyId}/approve`
   - 审批驳回并删除数据：`POST /api/company-registration/{companyId}/reject`
4. 审批通过后：
   - 企业状态变为 `APPROVED`
   - 企业管理员账号启用，并具有企业管理员权限（可管理本企业用户、车辆等）。

### 小程序填报

- 登录：`pages/login/index`
- 选择企业：`pages/companySelector/index`
- 主要填报入口：
  - 企业信息：`pages/company/form`
  - 车辆信息：`pages/vehicle/list` / `pages/vehicle/form`
  - 运输统计：`pages/transport/form`
  - 维护保养、合规运营、需求反馈等对应页面

---

## 常见问题

### 1. 前端提示无法连接后端
- 确认后端已在 `http://localhost:8080/api` 启动
- 检查 `frontend/vite.config.js` 中代理配置是否仍指向该地址
- 浏览器控制台查看网络请求与 CORS 情况

### 2. Swagger 打不开
- 确认访问路径为：`http://localhost:8080/api/swagger-ui.html`
- 检查 `application.properties` 中 `springdoc.swagger-ui.path` 是否被修改

### 3. 微信小程序接口 404 / 超时
- 检查 `mini-program/config/index.js` 中 `baseURL` 是否正确
- 真机调试时，确认手机网络能访问后端 IP/端口

---

## 许可证与联系方式

- 许可证：MIT License（如无特别说明）
- 若需对接、二次开发或排查问题，可结合：
  - 后端日志（`logging.level.com.example.cailanzi=DEBUG`）
  - 前端浏览器控制台
  - 小程序开发者工具控制台

如需进一步协助，可在项目管理平台/代码平台中补充具体问题场景与报错截图。