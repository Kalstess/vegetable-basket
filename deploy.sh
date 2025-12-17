#!/bin/bash

# 菜篮子工程车系统 - 一键部署脚本
# 适用于 CentOS 7 服务器

echo "=========================================="
echo "  菜篮子工程车系统 - Docker 部署脚本"
echo "=========================================="
echo ""

# 检查是否为 root 用户
if [ "$EUID" -ne 0 ]; then 
    echo "❌ 请使用 root 用户运行此脚本"
    echo "   可以使用: sudo bash deploy.sh"
    exit 1
fi

# 检查 Docker 是否安装
echo "📦 检查 Docker 安装状态..."
if ! command -v docker &> /dev/null; then
    echo "⚠️  Docker 未安装，开始安装 Docker..."
    
    # 更新系统
    yum update -y
    
    # 安装必要工具
    yum install -y yum-utils device-mapper-persistent-data lvm2
    
    # 添加 Docker 仓库
    yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    
    # 安装 Docker
    yum install -y docker-ce docker-ce-cli containerd.io
    
    # 启动 Docker
    systemctl start docker
    systemctl enable docker
    
    echo "✅ Docker 安装完成"
else
    echo "✅ Docker 已安装"
    docker --version
fi

# 检查 Docker Compose 是否安装
echo ""
echo "📦 检查 Docker Compose 安装状态..."
if ! command -v docker-compose &> /dev/null; then
    echo "⚠️  Docker Compose 未安装，开始安装..."
    
    # 尝试使用官方源下载
    curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose 2>/dev/null
    
    # 如果下载失败，尝试国内镜像
    if [ ! -f /usr/local/bin/docker-compose ]; then
        echo "   使用国内镜像下载..."
        curl -L "https://get.daocloud.io/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    fi
    
    # 添加执行权限
    chmod +x /usr/local/bin/docker-compose
    
    echo "✅ Docker Compose 安装完成"
else
    echo "✅ Docker Compose 已安装"
    docker-compose --version
fi

# 检查防火墙
echo ""
echo "🔥 检查防火墙配置..."
if systemctl is-active --quiet firewalld; then
    echo "   配置防火墙规则..."
    firewall-cmd --permanent --add-port=80/tcp 2>/dev/null
    firewall-cmd --permanent --add-port=8080/tcp 2>/dev/null
    firewall-cmd --reload 2>/dev/null
    echo "✅ 防火墙规则已配置"
else
    echo "⚠️  firewalld 未运行，跳过防火墙配置"
fi

# 检查项目文件
echo ""
echo "📁 检查项目文件..."
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ 未找到 docker-compose.yml 文件"
    echo "   请确保在项目根目录运行此脚本"
    exit 1
fi

if [ ! -d "backend" ] || [ ! -d "frontend" ]; then
    echo "❌ 项目文件不完整，请检查 backend 和 frontend 目录是否存在"
    exit 1
fi

echo "✅ 项目文件检查通过"

# 检查内存并选择配置文件
echo ""
echo "💾 检查服务器内存..."
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
echo "   总内存: ${TOTAL_MEM}MB"

COMPOSE_FILE="docker-compose.yml"
if [ "$TOTAL_MEM" -lt 3072 ]; then
    echo "⚠️  检测到内存小于 3GB，将使用低内存配置"
    if [ -f "docker-compose.low-memory.yml" ]; then
        COMPOSE_FILE="docker-compose.low-memory.yml"
        echo "✅ 将使用 docker-compose.low-memory.yml 配置文件"
        echo "   内存分配：MySQL 512MB + 后端 768MB + 前端 128MB ≈ 1.4GB"
    else
        echo "⚠️  未找到 docker-compose.low-memory.yml，将使用默认配置"
        echo "   建议手动使用低内存配置以避免内存不足"
    fi
else
    echo "✅ 内存充足，使用标准配置"
fi

# 提示修改密码
echo ""
echo "⚠️  重要提示："
echo "   1. 请确保已修改配置文件中的数据库密码（默认密码不安全）"
if [ "$COMPOSE_FILE" = "docker-compose.low-memory.yml" ]; then
    echo "   2. 如果使用低内存配置，请同时修改 docker-compose.low-memory.yml 中的密码"
fi
echo "   3. 如果服务器上已有服务占用 80、8080、3306 端口，请先修改配置文件"
echo ""
read -p "是否已修改配置？(y/n): " confirm
if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
    echo "   请先修改配置后再运行此脚本"
    exit 1
fi

# 停止可能存在的旧容器
echo ""
echo "🛑 停止旧容器（如果存在）..."
docker-compose -f "$COMPOSE_FILE" down 2>/dev/null

# 构建并启动服务
echo ""
echo "🚀 开始构建并启动服务..."
echo "   使用配置文件: $COMPOSE_FILE"
echo "   这可能需要 10-20 分钟，请耐心等待..."
echo ""

docker-compose -f "$COMPOSE_FILE" up -d --build

# 检查启动状态
echo ""
echo "⏳ 等待服务启动（30秒）..."
sleep 30

# 显示服务状态
echo ""
echo "📊 服务状态："
docker-compose -f "$COMPOSE_FILE" ps

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "📝 访问地址："
echo "   前端管理界面: http://$(hostname -I | awk '{print $1}')"
echo "   后端 API 文档: http://$(hostname -I | awk '{print $1}'):8080/api/swagger-ui.html"
echo ""
echo "📋 常用命令："
echo "   查看日志: docker-compose -f $COMPOSE_FILE logs -f"
echo "   停止服务: docker-compose -f $COMPOSE_FILE stop"
echo "   启动服务: docker-compose -f $COMPOSE_FILE start"
echo "   重启服务: docker-compose -f $COMPOSE_FILE restart"
echo ""
echo "如果服务未正常启动，请运行: docker-compose -f $COMPOSE_FILE logs"
echo ""

