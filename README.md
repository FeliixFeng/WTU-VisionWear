# WTU-VisionWear

VisionWear是一个基于AI技术的时尚图像生成应用，集成了多种图像生成和编辑功能，帮助用户创建个性化的时尚图像。

## 项目简介

VisionWear将前沿AI技术与时尚设计完美融合，为用户提供多样化的图像生成能力。系统集成了Stable Diffusion等AI模型，支持多种创意场景，包括风格延伸、线稿成图、局部重绘等功能。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.2
- MyBatis-Plus
- MySQL
- Swagger/Knife4j
- JWT认证
- 阿里云OSS对象存储

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router
- Vite

## 核心功能

- **图灵绘境**：基于AI生成时尚图像
- **风格延伸**：将您的图片转换为不同风格
- **线稿成图**：将线稿快速转化为完整图像
- **局部重绘**：针对图像特定区域进行调整

## 项目结构

### 后端结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/wtu/
│   │   │       ├── controller/    # 控制器层，处理HTTP请求
│   │   │       ├── service/       # 业务逻辑层
│   │   │       ├── mapper/        # MyBatis映射接口
│   │   │       ├── entity/        # 实体类
│   │   │       ├── DTO/           # 数据传输对象
│   │   │       ├── VO/            # 视图对象
│   │   │       ├── config/        # 配置类
│   │   │       ├── utils/         # 工具类
│   │   │       ├── interceptor/   # 拦截器
│   │   │       ├── properties/    # 属性配置类
│   │   │       ├── result/        # 统一响应结果
│   │   │       ├── exception/     # 异常处理
│   │   │       └── handler/       # 处理器
│   │   └── resources/
│   │       ├── mapper/            # MyBatis XML映射文件
│   │       ├── static/            # 静态资源
│   │       └── application.yml    # 应用配置文件
│   └── test/                      # 测试代码
└── pom.xml                        # Maven依赖配置
```

### 前端结构
```
frontend/
├── src/
│   ├── api/           # API接口请求
│   ├── assets/        # 静态资源文件
│   ├── components/    # 可复用组件
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理
│   ├── styles/        # 样式文件
│   ├── utils/         # 工具函数
│   ├── views/         # 页面视图组件
│   ├── App.vue        # 根组件
│   └── main.js        # 入口文件
├── public/            # 公共静态资源
├── index.html         # HTML模板
├── vite.config.js     # Vite配置
└── package.json       # 依赖配置
```

## 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+

## 本地开发

### 后端启动
```shell
cd backend
mvn spring-boot:run
```

### 前端启动
```shell
cd frontend
npm install
npm run dev
```

## 项目打包并部署
- 打包前注意修改前端src/api/request.js中的baseurl
- http://**localhost**:8080/api  --->  http://**8.155.5.178**:8080/api

### 运行脚本以部署
```shell
./deploy.sh
```

### 脚本内容
```shell
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
echo "文件上传完成!"
```