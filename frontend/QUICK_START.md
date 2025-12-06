# 快速启动指南

## 前置要求

- Node.js >= 16.0.0
- npm >= 7.0.0

## 安装步骤

1. **安装依赖**
```bash
cd cailanzi-frontend
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端将在 http://localhost:3000 启动

3. **确保后端服务运行**
后端服务需要在 http://localhost:8080 运行

## 项目结构

```
cailanzi-frontend/
├── src/
│   ├── api/              # API接口
│   │   ├── request.js    # axios请求封装
│   │   ├── company.js    # 企业API
│   │   ├── vehicle.js    # 车辆API
│   │   └── index.js      # API导出
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   │   └── index.vue     # 主布局
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── stores/           # Pinia状态管理
│   ├── styles/           # 样式文件
│   │   └── main.scss
│   ├── views/            # 页面组件
│   │   ├── Dashboard.vue        # 仪表盘
│   │   ├── company/             # 企业管理
│   │   │   ├── CompanyList.vue
│   │   │   └── CompanyDialog.vue
│   │   ├── vehicle/             # 车辆管理
│   │   │   └── VehicleList.vue
│   │   ├── transport/           # 运输统计
│   │   ├── compliance/          # 合规管理
│   │   ├── maintenance/         # 维护保养
│   │   └── feedback/            # 反馈管理
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 功能特性

### 已实现功能

- ✅ 项目基础架构（Vue3 + Vite + Element Plus）
- ✅ 路由配置
- ✅ 布局组件（侧边栏 + 顶部导航）
- ✅ API请求封装（axios）
- ✅ 企业管理（列表、新增、编辑、删除、搜索）
- ✅ 车辆管理（列表、删除）
- ✅ 响应式设计

### 待开发功能

- ⏳ 车辆管理（新增、编辑）
- ⏳ 运输统计
- ⏳ 合规管理
- ⏳ 维护保养
- ⏳ 反馈管理
- ⏳ 数据可视化
- ⏳ 用户权限管理

## API配置

前端API请求会自动代理到后端：
- 开发环境：`/api` → `http://localhost:8080/api`
- 生产环境：需要配置nginx反向代理

## 注意事项

1. **后端响应格式**
   - 部分Controller返回 `ResponseMessage` 格式（有code字段）
   - 部分Controller直接返回Entity（如CompanyController）
   - 前端已兼容两种情况，但建议后端统一响应格式

2. **CORS配置**
   - 后端已配置CORS，允许跨域请求
   - 如果遇到CORS问题，检查后端CorsConfig配置

3. **数据格式**
   - 日期格式：YYYY-MM-DD
   - 枚举值：使用中文值（如"国有"、"民营"）

## 开发建议

1. **API开发**
   - 在 `src/api/` 目录下创建对应的API文件
   - 使用统一的请求封装 `request.js`

2. **组件开发**
   - 公共组件放在 `src/components/`
   - 页面组件放在 `src/views/`

3. **样式开发**
   - 使用SCSS编写样式
   - 全局样式在 `src/styles/main.scss`

4. **状态管理**
   - 使用Pinia进行状态管理
   - Store文件放在 `src/stores/`

## 构建部署

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

构建产物在 `dist/` 目录下。

