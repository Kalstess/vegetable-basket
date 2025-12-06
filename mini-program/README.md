# 小程序端使用说明

## 环境
- 使用微信开发者工具导入 `mini-program` 目录
- AppID 可先用 `touristappid` 测试
- 后端地址配置：编辑 `config/index.js` 的 `baseURL`

## 登录
- 打开 `pages/login/index` 登录，后续请求将自动带上 `Authorization: Bearer <token>`

## 选择企业
- 在首页点击“选择企业”进入 `pages/companySelector/index`，选择后将写入 `app.globalData.companyId`

## 填报入口与覆盖范围
- 企业信息：`pages/company/form` 覆盖企业基本信息（A类）
- 车辆信息：`pages/vehicle/list`、`pages/vehicle/form` 覆盖车辆基本信息（B类）
  - 行驶证：支持选择图片并将 base64 附加至填报记录（后端 `reports`），也可粘贴 OCR 结果到 JSON 文本框
- 运输统计：`pages/transport/form` 覆盖 C 类核心字段与产品类型多选
- 维护保养：`pages/maintenance/form` 覆盖 E 类
- 合规运营：`pages/compliance/form` 覆盖 F 类
- 需求反馈：`pages/feedback/form` 覆盖 G 类
- 路线填报：`pages/route/form` 暂通过 `reports` 存储原始 JSON 负载（routeDate、points）

## 统计展示
- 首页展示总览统计数据（企业、车辆、运输、反馈），来源于后端 `/statistics`

## 注意事项
- 若后端提供正式的文件上传与路线接口，请将：
  - 行驶证 base64 报送替换为正式上传接口（`wx.uploadFile`）
  - 路线填报替换为后端路线实体 API（按 `RoutePoint` 设计）

## 目录结构简述
- `app.json` 路由与窗口配置
- `apis/*` 后端接口封装
- `utils/request.js` 请求与鉴权
- `pages/*` 页面


