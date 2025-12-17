# 菜篮子工程车系统 - CentOS 7 服务器部署指南

本指南将帮助您在 CentOS 7 服务器上使用 Docker 部署整个系统。即使您不熟悉 Linux 命令，按照本指南的步骤操作也能成功部署。

---

## 📋 部署前准备

### 服务器要求
- **操作系统**：CentOS 7
- **推荐配置**：2核CPU / 4GB内存 / 50GB硬盘
- **最低配置**：2核CPU / **2GB内存** / 50GB硬盘（适用于学习环境，并发10人左右）
- **网络**：确保服务器可以访问互联网（用于下载 Docker 镜像和 Git 仓库）

> **⚠️ 内存说明**：
> - **4GB 内存**：推荐配置，系统运行流畅
> - **2GB 内存**：可以使用，但需要优化配置（见"低内存配置"章节）
> - 如果服务器只有 2GB 内存，请使用 `docker-compose.low-memory.yml` 配置文件

### 需要准备的内容
1. 服务器 IP 地址和 root 密码（或具有 sudo 权限的账号）
2. SSH 客户端（Windows 推荐使用 PuTTY 或 Xshell，Mac/Linux 可直接使用终端）
3. 确保服务器可以访问 GitHub（用于克隆代码仓库）

---

## 第一步：连接到服务器

### Windows 用户
1. 打开 **PuTTY**（如果没有，请先下载安装）
2. 在 "Host Name" 输入框填入服务器 IP 地址
3. 端口保持 22（SSH 默认端口）
4. 点击 "Open" 连接
5. 输入用户名（通常是 `root`）和密码

### Mac/Linux 用户
打开终端，输入以下命令：
```bash
ssh root@你的服务器IP地址
```
然后输入密码

---

## 第二步：安装 Docker 和 Docker Compose

### 2.1 安装 Docker

在服务器上依次执行以下命令（每行命令执行完后再执行下一行）：

```bash
# 更新系统软件包
yum update -y

# 安装必要的工具
yum install -y yum-utils device-mapper-persistent-data lvm2

# 添加 Docker 官方仓库
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 安装 Docker（包含 Docker Compose 插件）
yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 启动 Docker 服务
systemctl start docker

# 设置 Docker 开机自启
systemctl enable docker

# 验证 Docker 是否安装成功（看到版本号说明安装成功）
docker --version
```

### 2.2 验证 Docker Compose 插件

**重要**：新版本的 Docker 已经将 Compose 作为插件集成，不再需要单独安装 `docker-compose` 命令，而是使用 `docker compose`（注意是空格，不是横线）。

```bash
# 验证 Docker Compose 插件是否安装成功
docker compose version

# 应该看到类似这样的输出：
# Docker Compose version v2.x.x
```

如果看到版本号，说明安装成功。如果提示命令不存在，请重新执行上面的安装命令。

---

## 第三步：获取项目代码

### 使用 Git 克隆项目（推荐）

```bash
# 进入 root 目录
cd /root

# 克隆项目（如果 GitHub 访问慢，可能需要多试几次）
git clone https://github.com/Kalstess/vegetable-basket.git

# 进入项目目录
cd vegetable-basket

# 查看项目文件（确认克隆成功）
ls -la
```

**应该能看到以下目录和文件**：
- `backend/` - 后端代码目录
- `frontend/` - 前端代码目录
- `mini-program/` - 小程序代码目录
- `docs/` - 文档目录
- `docker-compose.yml` - Docker 配置文件
- `docker-compose.low-memory.yml` - 低内存配置文件
- `deploy.sh` - 部署脚本

### 如果 Git 克隆失败

如果服务器无法访问 GitHub，可以使用以下方法：

**方法一：使用国内镜像（如果可用）**
```bash
# 某些 Git 镜像服务可能可用
git clone https://gitee.com/镜像地址/vegetable-basket.git
```

**方法二：手动上传项目文件**
1. 在本地电脑下载项目 ZIP 包
2. 使用 WinSCP 或 FileZilla 上传到服务器的 `/root/` 目录
3. 在服务器上解压：
```bash
cd /root
unzip vegetable-basket.zip
cd vegetable-basket
```

---

## 第四步：检查项目文件

```bash
# 确保在项目根目录
cd /root/vegetable-basket

# 检查关键文件是否存在
ls -la docker-compose.yml
ls -la docker-compose.low-memory.yml
ls -la docs/cailanzi_localhost-2025_12_17_19_51_30-dump.sql

# 如果 SQL 文件不存在，请检查 docs 目录
ls -la docs/
```

**重要**：确保 `docs/cailanzi_localhost-2025_12_17_19_51_30-dump.sql` 文件存在，这是数据库初始化文件。

---

## 第五步：修改配置（重要！）

### 5.1 修改数据库密码（强烈建议）

**⚠️ 安全提示**：默认密码 `111111` 非常不安全，生产环境必须修改！

打开 `docker-compose.yml` 文件：
```bash
cd /root/vegetable-basket
vi docker-compose.yml
```

**使用 vi 编辑器的方法**：
- 按 `i` 键进入编辑模式
- 找到以下两处，将 `111111` 改为您自己的密码（建议使用复杂密码，至少8位，包含字母和数字）：
  - 第 9 行：`MYSQL_ROOT_PASSWORD: 111111`
  - 第 32 行：`SPRING_DATASOURCE_PASSWORD: 111111`
- 按 `Esc` 键退出编辑模式
- 输入 `:wq` 然后按回车保存并退出

**示例修改后**：
```yaml
MYSQL_ROOT_PASSWORD: MySecurePassword123
# ...
SPRING_DATASOURCE_PASSWORD: MySecurePassword123
```

**如果使用低内存配置**，还需要修改 `docker-compose.low-memory.yml`：
```bash
vi docker-compose.low-memory.yml
```
同样修改第 17 行和第 71 行的密码。

### 5.2 检查端口占用

检查服务器上是否有服务占用了以下端口：
- **80**：Web 前端端口
- **8080**：后端 API 端口
- **3306**：MySQL 数据库端口

```bash
# 检查端口是否被占用
netstat -tlnp | grep 3306
netstat -tlnp | grep 80
netstat -tlnp | grep 8080
```

**如果端口被占用**，有两种解决方案：

**方案一**：停止占用端口的服务（如果不需要）
```bash
# 查看占用端口的进程
netstat -tlnp | grep 3306

# 停止服务（根据实际情况，替换服务名）
systemctl stop 服务名
```

**方案二**：修改 Docker Compose 配置文件中的端口映射
```bash
vi docker-compose.yml
```
修改端口映射，例如：
```yaml
ports:
  - "3307:3306"  # 将外部端口改为 3307，避免冲突
  - "8081:8080"  # 将后端端口改为 8081
  - "81:80"      # 将前端端口改为 81
```

### 5.3 低内存配置（2GB 内存服务器）

如果您的服务器只有 **2GB 内存**，需要使用优化后的配置文件：

```bash
# 检查服务器内存
free -h

# 如果内存为 2GB 或更少，后续启动时使用 docker-compose.low-memory.yml 文件
```

**低内存配置说明**：
- MySQL 限制为 512MB 内存
- 后端 Java 应用限制为 768MB 内存（JVM 堆内存 512MB）
- 前端 Nginx 限制为 128MB 内存
- 总计约 1.4GB，为系统预留约 600MB

**内存分配建议**：
- 操作系统：约 500-600MB
- MySQL：512MB
- 后端应用：768MB
- 前端 Nginx：128MB
- Docker 系统：约 100MB
- **总计**：约 2GB

> **注意**：2GB 内存可以运行，但性能可能不如 4GB 内存的服务器。如果系统运行缓慢，建议：
> 1. 减少并发用户数
> 2. 定期清理日志文件
> 3. 考虑升级到 4GB 内存

---

## 第六步：配置防火墙和安全组

### 6.1 配置服务器防火墙（CentOS 7）

CentOS 7 默认使用 `firewalld` 防火墙，需要开放以下端口：

```bash
# 开放 80 端口（Web 前端）
firewall-cmd --permanent --add-port=80/tcp

# 开放 8080 端口（后端 API）
firewall-cmd --permanent --add-port=8080/tcp

# 开放 3306 端口（MySQL，如果需要在外部访问）
# 注意：生产环境建议不开放此端口，只允许本地访问
firewall-cmd --permanent --add-port=3306/tcp

# 重新加载防火墙配置
firewall-cmd --reload

# 查看已开放的端口
firewall-cmd --list-ports
```

**如果防火墙未运行**，可以启动它：
```bash
# 启动防火墙
systemctl start firewalld
systemctl enable firewalld
```

### 6.2 配置云服务器安全组（重要！）

**如果您使用的是云服务器**（阿里云、腾讯云、华为云等），还需要在云服务商的控制台中配置安全组规则：

1. **登录云服务器控制台**
2. **找到"安全组"或"防火墙"设置**
3. **添加入站规则**，开放以下端口：
   - **80/tcp** - 允许访问 Web 前端
   - **8080/tcp** - 允许访问后端 API
   - **22/tcp** - SSH 端口（通常已默认开放）
   - **3306/tcp** - MySQL 端口（可选，建议仅内网访问）

**安全组配置示例**（以阿里云为例）：
- 规则方向：入方向
- 协议类型：TCP
- 端口范围：80/80
- 授权对象：0.0.0.0/0（允许所有 IP 访问，生产环境建议限制）

**⚠️ 重要提示**：
- 如果不配置安全组，即使服务器防火墙开放了端口，外部也无法访问
- 生产环境建议将 3306 端口仅开放给内网 IP，不要对外开放

---

## 第七步：启动服务

### 7.1 构建并启动所有服务

在项目根目录执行：

**4GB 内存服务器**（推荐）：
```bash
cd /root/vegetable-basket

# 构建并启动所有服务（这个过程可能需要 10-20 分钟，请耐心等待）
docker compose up -d --build
```

**2GB 内存服务器**（低内存配置）：
```bash
cd /root/vegetable-basket

# 使用低内存配置文件启动
docker compose -f docker-compose.low-memory.yml up -d --build
```

**命令说明**：
- `up`：启动服务
- `-d`：后台运行
- `--build`：重新构建镜像
- `-f`：指定配置文件（低内存配置时使用）

**⚠️ 注意**：使用的是 `docker compose`（空格），不是 `docker-compose`（横线）！

**第一次执行时会**：
1. 下载 MySQL、Maven、Node.js 等基础镜像（需要一些时间，取决于网络速度）
2. 编译后端 Java 代码（可能需要 5-10 分钟）
3. 编译前端 Vue 代码（可能需要 3-5 分钟）
4. 启动 MySQL 数据库并导入 SQL 文件（可能需要 1-2 分钟）
5. 启动后端服务
6. 启动前端服务

**整个过程可能需要 15-30 分钟**，请耐心等待，不要中断。

### 7.2 查看服务状态

```bash
# 查看所有容器的运行状态
docker compose ps

# 应该看到三个服务都在运行：
# - cailanzi-mysql
# - cailanzi-backend
# - cailanzi-frontend
```

**如果使用低内存配置**：
```bash
docker compose -f docker-compose.low-memory.yml ps
```

### 7.3 查看日志（如果启动失败）

```bash
# 查看所有服务的日志
docker compose logs

# 查看特定服务的日志
docker compose logs backend
docker compose logs frontend
docker compose logs db

# 实时查看日志（按 Ctrl+C 退出）
docker compose logs -f
```

**如果使用低内存配置**，在命令后添加 `-f docker-compose.low-memory.yml`：
```bash
docker compose -f docker-compose.low-memory.yml logs
```

---

## 第八步：验证部署

### 8.1 检查服务是否正常运行

```bash
# 检查容器状态（所有容器应该都是 "Up" 状态）
docker compose ps

# 检查端口监听
netstat -tlnp | grep 8080  # 后端端口
netstat -tlnp | grep 80    # 前端端口
netstat -tlnp | grep 3306  # 数据库端口

# 查看容器资源使用情况
docker stats
```

### 8.2 在浏览器中访问

1. **访问前端管理界面**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址`
   - 应该能看到登录页面
   - 如果无法访问，检查防火墙和安全组配置

2. **访问后端 API 文档**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址:8080/api/swagger-ui.html`
   - 应该能看到 Swagger API 文档页面

3. **测试后端接口**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址:8080/api/v3/api-docs`
   - 应该能看到 JSON 格式的 API 文档

### 8.3 如果无法访问

**检查步骤**：

1. **检查容器是否运行**：
```bash
docker compose ps
```
如果容器状态不是 "Up"，查看日志排查问题。

2. **检查防火墙**：
```bash
# 查看防火墙状态
systemctl status firewalld

# 查看已开放的端口
firewall-cmd --list-ports

# 如果端口未开放，重新执行第六步的防火墙配置
```

3. **检查安全组**（云服务器）：
   - 登录云服务器控制台
   - 检查安全组规则是否正确配置
   - 确保 80 和 8080 端口已开放

4. **检查端口占用**：
```bash
netstat -tlnp | grep 80
netstat -tlnp | grep 8080
```
如果端口被其他进程占用，需要停止该进程或修改配置。

5. **检查后端日志**：
```bash
docker compose logs backend | tail -50
```
查看是否有错误信息。

---

## 第九步：初始化管理员账号

### 9.1 检查数据库是否已导入数据

```bash
# 进入 MySQL 容器
docker exec -it cailanzi-mysql mysql -uroot -p111111

# 在 MySQL 中执行
USE cailanzi;
SELECT COUNT(*) FROM users;
exit;
```

**如果 users 表为空**（返回 0），需要手动导入数据或创建管理员账号。

### 9.2 创建默认管理员账号

如果数据库中没有用户数据，可以手动创建管理员账号：

```bash
# 进入 MySQL 容器
docker exec -it cailanzi-mysql mysql -uroot -p111111

# 在 MySQL 中执行以下 SQL（将密码替换为您在 docker-compose.yml 中设置的密码）
USE cailanzi;

-- 创建管理员账号，密码：123456
INSERT INTO `users` (
    `created_at`, `updated_at`, `email`, `is_active`, `nickname`, 
    `password`, `role`, `username`, `phone`
) VALUES (
    NOW(), 
    NOW(), 
    'admin@example.com', 
    1, 
    '系统管理员', 
    '$2a$10$Yjijd.Im2UbVp9Wb.0Y0lOPrupIGCS.A6I6VmckeKLI9ecCv8EAou', 
    'ADMIN', 
    'admin', 
    '13800138000'
);

-- 验证账号是否创建成功
SELECT id, username, role, is_active, nickname FROM users;

exit;
```

**默认登录信息**：
- 用户名：`admin`
- 密码：`123456`

**⚠️ 安全提示**：登录后请立即修改密码！

---

## 第十步：常用管理命令

### 停止服务
```bash
cd /root/vegetable-basket
docker compose stop
```

### 启动服务
```bash
docker compose start
```

### 重启服务
```bash
docker compose restart
```

### 停止并删除容器（数据不会丢失）
```bash
docker compose down
```

### 停止并删除容器和数据卷（⚠️ 危险：会删除数据库数据）
```bash
docker compose down -v
```

### 查看实时日志
```bash
# 查看所有服务日志
docker compose logs -f

# 查看特定服务日志
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f db
```

### 进入容器内部（调试用）
```bash
# 进入后端容器
docker exec -it cailanzi-backend sh

# 进入数据库容器
docker exec -it cailanzi-mysql bash

# 进入数据库（MySQL 命令行）
docker exec -it cailanzi-mysql mysql -uroot -p111111
```

### 重新构建并启动（更新代码后）
```bash
cd /root/vegetable-basket

# 停止服务
docker compose down

# 重新构建并启动
docker compose up -d --build
```

**如果使用低内存配置**，在所有命令后添加 `-f docker-compose.low-memory.yml`。

---

## 第十一步：设置开机自启

### 11.1 确保 Docker 服务开机自启

```bash
# 检查 Docker 是否已设置开机自启
systemctl is-enabled docker

# 如果返回 "disabled"，执行以下命令
systemctl enable docker
```

### 11.2 Docker Compose 服务自动启动

Docker Compose 启动的服务默认会在容器重启时自动启动（因为配置文件中设置了 `restart: always` 或 `restart: unless-stopped`）。

**验证**：
```bash
# 重启服务器
reboot

# 重启后检查容器状态
docker compose ps
```

所有容器应该自动启动。

---

## 🔧 常见问题排查

### 问题1：docker compose 命令找不到

**错误信息**：`docker: 'compose' is not a docker command.`

**解决方法**：
```bash
# 检查是否安装了 docker-compose-plugin
yum list installed | grep docker-compose-plugin

# 如果未安装，执行安装命令
yum install -y docker-compose-plugin

# 验证安装
docker compose version
```

### 问题2：端口被占用

**解决方法**：
```bash
# 查看占用端口的进程
netstat -tlnp | grep 3306
netstat -tlnp | grep 80
netstat -tlnp | grep 8080

# 停止占用端口的服务，或修改 docker-compose.yml 中的端口映射
```

### 问题3：容器启动失败

**解决方法**：
```bash
# 查看详细日志
docker compose logs backend
docker compose logs frontend
docker compose logs db

# 根据错误信息进行排查
# 常见错误：
# - 数据库连接失败：检查密码配置
# - 内存不足：使用低内存配置
# - 端口冲突：修改端口映射
```

### 问题4：前端无法连接后端

**检查步骤**：
1. 确认后端容器正常运行：`docker compose ps`
2. 检查后端日志：`docker compose logs backend`
3. 确认数据库连接正常：查看后端日志中是否有数据库连接错误
4. 检查网络连接：`docker compose exec backend ping db`

### 问题5：数据库连接失败

**解决方法**：
1. 检查数据库容器是否启动：`docker compose ps`
2. 等待数据库完全启动（首次启动需要初始化，可能需要 1-2 分钟）
3. 检查 `docker-compose.yml` 中的数据库密码配置是否正确
4. 查看数据库日志：`docker compose logs db`

### 问题6：内存不足

**解决方法**：
```bash
# 查看系统资源使用情况
free -h
df -h

# 查看 Docker 容器内存使用
docker stats

# 如果内存不足，可以：
# 1. 使用低内存配置：docker compose -f docker-compose.low-memory.yml up -d
# 2. 关闭不必要的服务
# 3. 增加服务器内存（推荐）
```

**2GB 内存服务器优化建议**：
1. 使用 `docker-compose.low-memory.yml` 配置文件
2. 关闭系统不必要的服务
3. 定期清理 Docker 资源：
   ```bash
   # 清理未使用的镜像、容器、网络
   docker system prune -a
   
   # 清理日志文件
   find /var/lib/docker/containers/ -name "*.log" -delete
   ```

### 问题7：构建镜像时下载慢

**解决方法**：
可以配置 Docker 镜像加速器（国内服务器推荐）：
```bash
# 创建或编辑 Docker 配置文件
vi /etc/docker/daemon.json

# 添加以下内容（使用阿里云镜像加速，需要替换为您的加速地址）
{
  "registry-mirrors": [
    "https://your-mirror.mirror.aliyuncs.com"
  ]
}

# 重启 Docker
systemctl restart docker

# 重新启动服务
cd /root/vegetable-basket
docker compose up -d --build
```

### 问题8：SQL 文件导入失败

**检查步骤**：
```bash
# 检查 SQL 文件是否存在
ls -la /root/vegetable-basket/docs/cailanzi_localhost-2025_12_17_19_51_30-dump.sql

# 检查数据库容器日志
docker compose logs db | grep -i error

# 手动导入 SQL 文件
docker exec -i cailanzi-mysql mysql -uroot -p111111 < /root/vegetable-basket/docs/cailanzi_localhost-2025_12_17_19_51_30-dump.sql
```

### 问题9：无法访问前端页面

**检查清单**：
- [ ] 容器是否正常运行：`docker compose ps`
- [ ] 防火墙是否开放 80 端口：`firewall-cmd --list-ports`
- [ ] 安全组是否配置正确（云服务器）
- [ ] 端口是否被占用：`netstat -tlnp | grep 80`
- [ ] 前端容器日志：`docker compose logs frontend`

---

## 📝 重要提示

1. **数据库密码**：生产环境请务必修改默认密码 `111111`
2. **数据备份**：定期备份数据库数据
   ```bash
   # 备份数据库
   docker exec cailanzi-mysql mysqldump -uroot -p111111 cailanzi > backup_$(date +%Y%m%d).sql
   ```
3. **日志查看**：遇到问题时，首先查看日志：`docker compose logs`
4. **防火墙**：确保开放必要的端口（80、8080）
5. **安全组**：云服务器需要在控制台配置安全组规则
6. **命令格式**：使用 `docker compose`（空格），不是 `docker-compose`（横线）

---

## 🎉 部署完成

如果一切顺利，您现在应该能够：
- ✅ 通过浏览器访问前端管理界面：`http://你的服务器IP地址`
- ✅ 通过 Swagger 查看和测试 API：`http://你的服务器IP地址:8080/api/swagger-ui.html`
- ✅ 使用默认账号登录：用户名 `admin`，密码 `123456`
- ✅ 所有服务正常运行

**默认访问地址**：
- 前端：`http://你的服务器IP地址`
- 后端 API 文档：`http://你的服务器IP地址:8080/api/swagger-ui.html`

**⚠️ 安全提醒**：
- 登录后请立即修改默认密码
- 生产环境建议修改数据库密码
- 定期备份数据库数据

---

## 📞 需要帮助？

如果遇到问题：
1. 查看日志：`docker compose logs`
2. 检查容器状态：`docker compose ps`
3. 查看本文档的"常见问题排查"部分
4. 检查防火墙和安全组配置
5. 记录错误信息，联系技术支持

---

**祝您部署顺利！** 🚀

---

## 📦 项目更新

如果项目已经部署，需要更新到最新版本，请查看：[项目更新指南](UPDATE_DEPLOYMENT.md)

更新步骤简要说明：
1. 连接到服务器
2. 进入项目目录：`cd /root/vegetable-basket`
3. 停止服务：`docker compose down`
4. 更新代码：`git pull origin main`
5. 重新构建启动：`docker compose up -d --build`
6. 验证更新：访问前端页面确认功能正常

详细步骤请参考更新指南文档。
