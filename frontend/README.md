# 菜篮子工程车填报和展示系统 - 前端

基于 Vue 3 + Vite + Element Plus 开发的前端应用。

## 技术栈

- Vue 3
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios
- Day.js

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 项目结构

```
src/
  ├── api/          # API接口
  ├── assets/       # 静态资源
  ├── components/   # 公共组件
  ├── layouts/      # 布局组件
  ├── router/       # 路由配置
  ├── stores/       # Pinia状态管理
  ├── styles/       # 样式文件
  └── views/        # 页面组件
```

## API配置

后端API地址: http://localhost:8080/api

开发环境已配置代理，前端请求 `/api` 会自动转发到后端服务器。

