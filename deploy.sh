#!/bin/bash
# deploy.sh

echo "开始部署项目..."

# 部署后端
echo "编译并部署后端..."
cd backend
mvn clean install
echo "请输入服务器密码以传输后端文件:"
scp target/backend-0.0.1-SNAPSHOT.jar root@8.155.5.178:/opt/vision-wear

# 部署前端
echo "编译并部署前端..."
cd ../frontend
npm run build
echo "请输入服务器密码以传输前端文件:"
scp -r dist/ root@8.155.5.178:/opt/vision-wear/

# 重启服务
echo "文件上传完成! "