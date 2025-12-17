# 部署快速参考卡片

## 🚀 一键部署（最简单）

```bash
# 1. 上传项目到服务器 /root/vegetable-basket
# 2. 进入项目目录
cd /root/vegetable-basket

# 3. 修改数据库密码（重要！）
vi docker-compose.yml
# 将 MYSQL_ROOT_PASSWORD 和 SPRING_DATASOURCE_PASSWORD 改为你的密码

# 4. 运行部署脚本（会自动检测内存并选择配置）
bash deploy.sh
```

### 2GB 内存服务器

```bash
# 如果服务器只有 2GB 内存，使用低内存配置
docker-compose -f docker-compose.low-memory.yml up -d --build
```

## 📋 手动部署步骤

```bash
# 1. 安装 Docker
yum update -y
yum install -y yum-utils device-mapper-persistent-data lvm2
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install -y docker-ce docker-ce-cli containerd.io
systemctl start docker
systemctl enable docker

# 2. 安装 Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 3. 配置防火墙
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --reload

# 4. 启动服务
cd /root/vegetable-basket
docker-compose up -d --build
```

## 🔍 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose stop

# 启动服务
docker-compose start

# 重启服务
docker-compose restart

# 停止并删除容器
docker-compose down
```

## 🌐 访问地址

- **前端管理界面**：`http://服务器IP`
- **后端 API 文档**：`http://服务器IP:8080/api/swagger-ui.html`

## ⚠️ 重要提示

1. **修改默认密码**：务必修改 `docker-compose.yml` 中的数据库密码
2. **检查端口**：确保 80、8080、3306 端口未被占用
3. **防火墙**：确保开放 80 和 8080 端口
4. **云服务器**：需要在控制台配置安全组规则
5. **内存要求**：
   - **4GB 内存**：推荐，使用 `docker-compose.yml`
   - **2GB 内存**：可以使用，使用 `docker-compose.low-memory.yml`

## 🐛 问题排查

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db

# 检查端口占用
netstat -tlnp | grep 3306
netstat -tlnp | grep 80
netstat -tlnp | grep 8080
```

---

**详细部署文档**：请查看 [`DEPLOYMENT_GUIDE.md`](DEPLOYMENT_GUIDE.md)

