# 菜篮子工程车系统 - CentOS 7 服务器部署指南

本指南将帮助您在 CentOS 7 服务器上使用 Docker 部署整个系统。即使您不熟悉 Linux 命令，按照本指南的步骤操作也能成功部署。

---

## 📋 部署前准备

### 服务器要求
- **操作系统**：CentOS 7
- **推荐配置**：2核CPU / 4GB内存 / 50GB硬盘
- **最低配置**：2核CPU / **2GB内存** / 50GB硬盘（适用于学习环境，并发10人左右）
- **网络**：确保服务器可以访问互联网（用于下载 Docker 镜像）

> **⚠️ 内存说明**：
> - **4GB 内存**：推荐配置，系统运行流畅
> - **2GB 内存**：可以使用，但需要优化配置（见"低内存配置"章节）
> - 如果服务器只有 2GB 内存，请使用 `docker-compose.low-memory.yml` 配置文件

### 需要准备的内容
1. 服务器 IP 地址和 root 密码（或具有 sudo 权限的账号）
2. SSH 客户端（Windows 推荐使用 PuTTY 或 Xshell，Mac/Linux 可直接使用终端）
3. 文件传输工具（Windows 推荐 WinSCP 或 FileZilla，Mac/Linux 可使用 scp 命令）

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

# 安装 Docker
yum install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker 服务
systemctl start docker

# 设置 Docker 开机自启
systemctl enable docker

# 验证 Docker 是否安装成功（看到版本号说明安装成功）
docker --version
```

### 2.2 安装 Docker Compose

```bash
# 下载 Docker Compose（如果下载慢，可以多试几次）
curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加执行权限
chmod +x /usr/local/bin/docker-compose

# 验证安装（看到版本号说明安装成功）
docker-compose --version
```

**如果下载 Docker Compose 失败**，可以使用国内镜像：
```bash
# 使用国内镜像下载
curl -L "https://get.daocloud.io/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose --version
```

---

## 第三步：上传项目文件到服务器

### 方法一：使用 WinSCP（Windows 推荐）

1. 打开 **WinSCP**
2. 新建连接：
   - 主机名：服务器 IP 地址
   - 用户名：root
   - 密码：服务器密码
   - 协议：SFTP
3. 连接成功后，在右侧（服务器端）进入 `/root` 目录
4. 在左侧（本地）找到项目文件夹 `vegetable-basket`
5. 将整个 `vegetable-basket` 文件夹拖拽到服务器右侧，等待上传完成

### 方法二：使用 scp 命令（Mac/Linux）

在本地电脑的终端中执行（需要将项目路径替换为实际路径）：
```bash
scp -r /本地项目路径/vegetable-basket root@服务器IP:/root/
```

### 方法三：使用 Git（如果服务器已安装 Git）

```bash
# 在服务器上执行
cd /root
git clone 你的项目Git地址 vegetable-basket
```

---

## 第四步：进入项目目录并检查文件

```bash
# 进入项目目录
cd /root/vegetable-basket

# 查看文件列表（确认文件都已上传）
ls -la

# 应该能看到以下目录：backend、frontend、mini-program、docs、docker-compose.yml 等
```

---

## 第五步：修改配置（重要！）

### 5.1 修改数据库密码（强烈建议）

打开 `docker-compose.yml` 文件：
```bash
vi docker-compose.yml
```

**使用 vi 编辑器的方法**：
- 按 `i` 键进入编辑模式
- 找到 `MYSQL_ROOT_PASSWORD: 111111` 这一行，将 `111111` 改为您自己的密码（建议使用复杂密码）
- 同时找到 `SPRING_DATASOURCE_PASSWORD: 111111` 这一行，也改为相同的密码
- 按 `Esc` 键退出编辑模式
- 输入 `:wq` 然后按回车保存并退出

**示例修改后**：
```yaml
MYSQL_ROOT_PASSWORD: 你的新密码
# ...
SPRING_DATASOURCE_PASSWORD: 你的新密码
```

### 5.2 检查端口占用（可选）

如果您的服务器上已经运行了 MySQL（3306端口）或 Web 服务（80端口），需要修改 `docker-compose.yml` 中的端口映射：

```bash
# 检查端口是否被占用
netstat -tlnp | grep 3306
netstat -tlnp | grep 80
```

如果端口被占用，需要修改 `docker-compose.yml` 中的端口映射，例如：
```yaml
ports:
  - "3307:3306"  # 将外部端口改为 3307，避免冲突
```

### 5.3 低内存配置（2GB 内存服务器）

如果您的服务器只有 **2GB 内存**，需要使用优化后的配置文件：

```bash
# 检查服务器内存
free -h

# 如果内存为 2GB 或更少，使用低内存配置
# 启动时使用 docker-compose.low-memory.yml 文件
docker-compose -f docker-compose.low-memory.yml up -d --build
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

## 第六步：启动服务

### 6.1 构建并启动所有服务

在项目根目录执行：

**4GB 内存服务器**（推荐）：
```bash
cd /root/vegetable-basket

# 构建并启动所有服务（这个过程可能需要 10-20 分钟，请耐心等待）
docker-compose up -d --build
```

**2GB 内存服务器**（低内存配置）：
```bash
cd /root/vegetable-basket

# 使用低内存配置文件启动
docker-compose -f docker-compose.low-memory.yml up -d --build
```

**命令说明**：
- `up`：启动服务
- `-d`：后台运行
- `--build`：重新构建镜像

**第一次执行时会**：
1. 下载 MySQL、Maven、Node.js 等基础镜像（需要一些时间）
2. 编译后端 Java 代码
3. 编译前端 Vue 代码
4. 启动 MySQL 数据库
5. 启动后端服务
6. 启动前端服务

### 6.2 查看服务状态

```bash
# 查看所有容器的运行状态
docker-compose ps

# 应该看到三个服务都在运行：
# - cailanzi-mysql
# - cailanzi-backend
# - cailanzi-frontend
```

### 6.3 查看日志（如果启动失败）

```bash
# 查看所有服务的日志
docker-compose logs

# 查看特定服务的日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db
```

---

## 第七步：验证部署

### 7.1 检查服务是否正常运行

```bash
# 检查容器状态
docker-compose ps

# 检查端口监听
netstat -tlnp | grep 8080  # 后端端口
netstat -tlnp | grep 80    # 前端端口
netstat -tlnp | grep 3306  # 数据库端口
```

### 7.2 在浏览器中访问

1. **访问前端管理界面**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址`
   - 应该能看到登录页面

2. **访问后端 API 文档**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址:8080/api/swagger-ui.html`
   - 应该能看到 Swagger API 文档页面

3. **测试后端接口**：
   - 在浏览器地址栏输入：`http://你的服务器IP地址:8080/api/v3/api-docs`
   - 应该能看到 JSON 格式的 API 文档

### 7.3 如果无法访问

**检查防火墙**：
```bash
# CentOS 7 使用 firewalld，需要开放端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --reload

# 查看开放的端口
firewall-cmd --list-ports
```

**如果使用的是云服务器**，还需要在云服务商的控制台中配置安全组规则，开放 80 和 8080 端口。

---

## 第八步：常用管理命令

### 停止服务
```bash
cd /root/vegetable-basket
docker-compose stop
```

### 启动服务
```bash
docker-compose start
```

### 重启服务
```bash
docker-compose restart
```

### 停止并删除容器（数据不会丢失）
```bash
docker-compose down
```

### 停止并删除容器和数据卷（⚠️ 危险：会删除数据库数据）
```bash
docker-compose down -v
```

### 查看实时日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
```

### 进入容器内部（调试用）
```bash
# 进入后端容器
docker exec -it cailanzi-backend sh

# 进入数据库容器
docker exec -it cailanzi-mysql bash
```

---

## 第九步：设置开机自启（可选）

确保 Docker 服务开机自启：
```bash
systemctl enable docker
```

Docker Compose 启动的服务默认会在容器重启时自动启动（因为 `docker-compose.yml` 中配置了 `restart: always`）。

---

## 🔧 常见问题排查

### 问题1：docker-compose 命令找不到

**解决方法**：
```bash
# 检查是否安装
docker-compose --version

# 如果未安装，重新安装（参考第二步）
```

### 问题2：端口被占用

**解决方法**：
```bash
# 查看占用端口的进程
netstat -tlnp | grep 3306
netstat -tlnp | grep 80

# 停止占用端口的服务，或修改 docker-compose.yml 中的端口映射
```

### 问题3：容器启动失败

**解决方法**：
```bash
# 查看详细日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db

# 根据错误信息进行排查
```

### 问题4：前端无法连接后端

**检查**：
1. 确认后端容器正常运行：`docker-compose ps`
2. 检查后端日志：`docker-compose logs backend`
3. 确认数据库连接正常：查看后端日志中是否有数据库连接错误

### 问题5：数据库连接失败

**解决方法**：
1. 检查数据库容器是否启动：`docker-compose ps`
2. 等待数据库完全启动（首次启动需要初始化，可能需要 1-2 分钟）
3. 检查 `docker-compose.yml` 中的数据库密码配置是否正确

### 问题6：内存不足

**解决方法**：
```bash
# 查看系统资源使用情况
free -h
df -h

# 查看 Docker 容器内存使用
docker stats

# 如果内存不足，可以：
# 1. 使用低内存配置：docker-compose -f docker-compose.low-memory.yml up -d
# 2. 关闭不必要的服务
# 3. 增加服务器内存（推荐）
# 4. 优化 Docker 配置
```

**2GB 内存服务器优化建议**：
1. 使用 `docker-compose.low-memory.yml` 配置文件
2. 关闭系统不必要的服务：
   ```bash
   # 查看运行的服务
   systemctl list-units --type=service --state=running
   
   # 停止不必要的服务（根据实际情况）
   systemctl stop 服务名
   systemctl disable 服务名
   ```
3. 定期清理 Docker 资源：
   ```bash
   # 清理未使用的镜像、容器、网络
   docker system prune -a
   
   # 清理日志文件
   find /var/lib/docker/containers/ -name "*.log" -delete
   ```
4. 如果仍然内存不足，考虑升级到 4GB 内存

### 问题7：构建镜像时下载慢

**解决方法**：
可以配置 Docker 镜像加速器（国内服务器推荐）：
```bash
# 创建或编辑 Docker 配置文件
vi /etc/docker/daemon.json

# 添加以下内容（使用阿里云镜像加速）
{
  "registry-mirrors": ["https://your-mirror.mirror.aliyuncs.com"]
}

# 重启 Docker
systemctl restart docker
```

---

## 📝 重要提示

1. **数据库密码**：生产环境请务必修改默认密码
2. **数据备份**：定期备份数据库数据
3. **日志查看**：遇到问题时，首先查看日志：`docker-compose logs`
4. **防火墙**：确保开放必要的端口（80、8080）
5. **安全组**：云服务器需要在控制台配置安全组规则

---

## 🎉 部署完成

如果一切顺利，您现在应该能够：
- ✅ 通过浏览器访问前端管理界面
- ✅ 通过 Swagger 查看和测试 API
- ✅ 所有服务正常运行

**默认访问地址**：
- 前端：`http://你的服务器IP地址`
- 后端 API 文档：`http://你的服务器IP地址:8080/api/swagger-ui.html`

---

## 📞 需要帮助？

如果遇到问题：
1. 查看日志：`docker-compose logs`
2. 检查容器状态：`docker-compose ps`
3. 查看本文档的"常见问题排查"部分
4. 记录错误信息，联系技术支持

---

**祝您部署顺利！** 🚀

