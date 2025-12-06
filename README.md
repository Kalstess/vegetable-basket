# 菜篮子工程车填报和展示系统

完整的全栈项目，包含后端（Spring Boot）和前端（Vue3）应用。

## 项目结构

```
cailanzi/
├── backend/              # 后端项目（Spring Boot）
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/            # 前端项目（Vue3）
│   ├── src/
│   ├── package.json
│   └── ...
├── docs/                # 项目文档
│   ├── database/        # 数据库相关文档
│   └── api/            # API文档
├── docker/              # Docker相关配置
├── .gitignore          # Git忽略文件
├── docker-compose.yml   # Docker Compose配置（可选）
└── README.md           # 项目说明（本文件）
```

## 技术栈

### 后端
- Spring Boot 3.3.0
- Spring Data JPA
- MySQL 8.0
- MapStruct
- Lombok
- Swagger/OpenAPI

### 前端
- Vue 3
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+

### 1. 数据库准备

```bash
# 进入后端目录
cd backend

# 执行DDL脚本创建数据库
mysql -u root -p < src/main/resources/ddl.sql
# 或者直接导入ddl.sql文件
```

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 修改数据库配置（如果需要）
# 编辑 src/main/resources/application.properties

# 启动后端服务
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/cailanzi-backend-0.0.1-SNAPSHOT.jar
```

后端服务将在 http://localhost:8080 启动

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

## API文档

后端启动后，访问 Swagger UI：
- http://localhost:8080/api/swagger-ui.html

## 开发说明

### 后端开发

- 项目路径：`backend/`
- 配置文件：`backend/src/main/resources/application.properties`
- 主类：`backend/src/main/java/com/example/cailanzi/CailanziApplication.java`

### 前端开发

- 项目路径：`frontend/`
- 配置文件：`frontend/vite.config.js`
- API代理：开发环境已配置，前端请求 `/api` 会自动代理到后端

## 构建部署

### 后端构建

```bash
cd backend
mvn clean package
```

构建产物：`backend/target/cailanzi-backend-0.0.1-SNAPSHOT.jar`

### 前端构建

```bash
cd frontend
npm run build
```

构建产物：`frontend/dist/`

## 环境配置

### 后端配置

编辑 `backend/src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/cailanzi
spring.datasource.username=root
spring.datasource.password=your_password
```

### 前端配置

前端API代理配置在 `frontend/vite.config.js`：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 常见问题

### 1. 数据库连接失败

- 检查MySQL服务是否启动
- 检查数据库配置是否正确
- 确认数据库已创建

### 2. 前端无法连接后端

- 确认后端服务已启动
- 检查CORS配置
- 查看浏览器控制台错误信息

### 3. 端口冲突

- 后端默认端口：8080（可在application.properties修改）
- 前端默认端口：3000（可在vite.config.js修改）

## 许可证

MIT License

## 联系方式

如有问题，请联系开发团队。

