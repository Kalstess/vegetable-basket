# 故障排查指南

## 常见问题

### 1. 前端无法连接到后端（ECONNREFUSED）

**错误信息：**
```
http proxy error: /api/statistics
AggregateError [ECONNREFUSED]
```

**可能原因：**
- 后端服务器未启动
- 后端运行在不同的端口
- 防火墙阻止连接

**解决方法：**

1. **检查后端是否运行**
   ```bash
   # 在 backend 目录下
   cd backend
   mvn spring-boot:run
   # 或
   java -jar target/cailanzi-*.jar
   ```

2. **确认后端端口**
   - 默认端口：8080
   - 检查 `backend/src/main/resources/application.properties` 中的 `server.port` 配置

3. **检查后端日志**
   - 应该看到类似 `Started CailanziApplication` 的启动信息
   - 确认后端在 `http://localhost:8080` 上运行

4. **测试后端连接**
   ```bash
   # 在浏览器中访问
   http://localhost:8080/api/swagger-ui.html
   # 或
   curl http://localhost:8080/api/auth/validate
   ```

### 2. Sass 弃用警告

**警告信息：**
```
Deprecation Warning [legacy-js-api]: The legacy JS API is deprecated
```

**解决方法：**
- 已更新 `vite.config.js` 使用现代编译器 API
- 已升级 `sass` 到最新版本
- 这些警告不影响功能，可以忽略

### 3. 路由查询正常但前端报错

**现象：**
- 后端日志显示正常的 SQL 查询
- 前端仍然报连接错误

**解决方法：**
1. 确认后端已完全启动（等待看到 "Started" 日志）
2. 清除浏览器缓存
3. 重启前端开发服务器
4. 检查浏览器控制台的完整错误信息

## 启动顺序

正确的启动顺序：

1. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **等待后端完全启动**
   - 看到 `Started CailanziApplication` 日志
   - 确认端口 8080 已监听

3. **启动前端**
   ```bash
   cd frontend
   npm run dev
   ```

## 验证连接

### 检查后端健康状态

```bash
# 使用 curl
curl http://localhost:8080/api/auth/validate

# 或访问 Swagger UI
http://localhost:8080/api/swagger-ui.html
```

### 检查前端代理

1. 打开浏览器开发者工具
2. 查看 Network 标签
3. 检查 `/api/*` 请求的状态码
4. 如果看到 404 或连接错误，检查后端是否运行

## 端口配置

- **后端端口**：8080（在 `application.properties` 中配置）
- **前端端口**：3000（在 `vite.config.js` 中配置）
- **API 路径**：`/api`（后端 context-path）

## 常见错误码

- **ECONNREFUSED**：后端未启动或端口错误
- **404**：API 路径错误或后端路由未配置
- **401**：未登录或 token 过期
- **500**：后端服务器错误，查看后端日志

