# VisionWear-Community 社区微服务模块

## 模块说明

VisionWear-Community 是 VisionWear 项目的社区功能微服务模块,提供用户社区交互功能,包括:

- 📝 帖子发布与管理(图文、纯文字、图片分享)
- 💬 评论功能(支持多级回复)
- 👍 点赞功能(帖子和评论)
- ⭐ 收藏功能(支持收藏夹分类)
- 👥 关注功能(用户关注)
- 🏷️ 标签功能(帖子标签)

## 目录结构

```
VisionWear-Community/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── wtu/
│   │   │           ├── CommunityApplication.java     # 启动类
│   │   │           ├── config/                       # 配置类
│   │   │           │   └── WebConfig.java           # Web配置
│   │   │           ├── controller/                   # 控制器层
│   │   │           ├── service/                      # 服务层
│   │   │           ├── mapper/                       # 数据访问层
│   │   │           ├── entity/                       # 实体类
│   │   │           ├── dto/                          # 数据传输对象
│   │   │           ├── vo/                           # 视图对象
│   │   │           └── interceptor/                  # 拦截器
│   │   │               └── UserInfoInterceptor.java # 用户信息拦截器
│   │   └── resources/
│   │       ├── application.yml          # 应用配置
│   │       ├── application-dev.yml      # 开发环境配置
│   │       ├── bootstrap.yml            # 引导配置
│   │       └── init.sql                 # 数据库初始化脚本
│   └── test/                            # 测试代码
├── pom.xml                              # Maven配置
└── README.md                            # 本文档
```

## 数据库设计

### 核心表结构

1. **post** - 帖子表
   - 存储帖子基本信息、统计数据
   
2. **post_image** - 帖子图片关联表
   - 关联帖子和图片,支持多图

3. **comment** - 评论表
   - 支持多级评论和回复

4. **like_record** - 点赞记录表
   - 记录用户对帖子/评论的点赞

5. **collect_record** - 收藏记录表
   - 记录用户收藏的帖子

6. **collect_folder** - 收藏夹表
   - 用户自定义收藏夹

7. **follow** - 关注关系表
   - 用户之间的关注关系

8. **tag** - 标签表
   - 帖子标签管理

9. **post_tag** - 帖子标签关联表
   - 帖子和标签的多对多关系

## 数据库初始化

执行 `src/main/resources/init.sql` 脚本创建数据库和表:

```bash
mysql -u root -p < src/main/resources/init.sql
```

或在 Nacos 配置中心配置数据源信息。

## 配置说明

### 端口配置
- 服务端口: `8083`

### 数据库配置
在 `application-dev.yml` 中配置:
```yaml
vision:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    community:
      url: jdbc:mysql://your-host:3306/vision_wear_community?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
      username: your-username
      password: your-password
```

### Nacos 配置
在 `bootstrap.yml` 中配置:
```yaml
spring:
  cloud:
    nacos:
      server-addr: your-nacos-server:8848
      config:
        file-extension: yml
        shared-configs:
          - dataId: shared.jdbc.yml
          - dataId: shared.log.yml
          - dataId: shared.swagger.yml
          - dataId: shared-redis.yml
```

## API路由

所有社区相关的API通过网关统一入口访问:

```
http://gateway-host:8080/api/community/**
```

网关会自动路由到 `VisionWear-Community` 服务。

## 依赖关系

### 依赖的模块
- `VisionWear-Common` - 公共工具类和配置
- `VisionWear-Api` - Feign客户端接口

### 核心依赖
- Spring Boot Web
- Spring Cloud Nacos Discovery
- Spring Cloud Nacos Config
- MyBatis Plus
- MySQL Connector
- Knife4j (API文档)
- Lombok

## 启动步骤

1. **启动 Nacos 服务**
   ```bash
   cd nacos/bin
   ./startup.sh -m standalone
   ```

2. **初始化数据库**
   ```bash
   mysql -u root -p < src/main/resources/init.sql
   ```

3. **配置 Nacos**
   - 在 Nacos 配置中心添加共享配置
   - 确保数据库连接信息正确

4. **启动网关服务**
   ```bash
   cd VisionWear-GateWay
   mvn spring-boot:run
   ```

5. **启动社区服务**
   ```bash
   cd VisionWear-Community
   mvn spring-boot:run
   ```

6. **访问 Swagger 文档**
   ```
   http://localhost:8083/doc.html
   ```

## 功能特性

### 1. 用户上下文传递
- 通过 `UserInfoInterceptor` 拦截器自动从请求头提取用户信息
- 使用 `ThreadLocal` 存储当前用户上下文
- 避免在每个方法中传递 `userId`

### 2. 统计数据实时更新
- 使用 Redis 缓存热点数据(浏览量、点赞数等)
- 定时同步到数据库,减轻数据库压力

### 3. 热门推荐算法
- 基于点赞数、评论数、浏览量等多维度计算热度
- 支持置顶功能

### 4. 内容审核
- 支持帖子/评论的状态管理(正常、删除、审核中、违规)

## 注意事项

1. **数据库设计**
   - 所有表都没有外键约束,适应微服务架构
   - 通过应用层保证数据一致性

2. **用户信息获取**
   - 需要调用 `VisionWear-User` 服务的 Feign 客户端获取用户详细信息
   - 用户头像、昵称等信息不冗余存储

3. **图片关联**
   - 帖子图片通过 `post_image` 表关联 `vision_wear_image` 库的图片
   - 图片URL冗余存储,提高查询性能

4. **Redis 使用**
   - 点赞/收藏状态使用 Redis 缓存
   - 热门帖子列表使用 Redis 排序集合

## 开发规范

1. **实体类命名**
   - 使用 `@TableName` 注解指定表名
   - 使用 `@TableId` 注解指定主键

2. **接口命名**
   - RESTful 风格
   - 统一使用 `/api/community` 前缀

3. **返回值封装**
   - 使用 `Result<T>` 统一返回格式

4. **异常处理**
   - 使用 `GlobalExceptionHandler` 统一异常处理
   - 自定义业务异常继承 `BusinessException`

## 后续开发计划

- [ ] 实现帖子 CRUD 功能
- [ ] 实现评论功能
- [ ] 实现点赞功能
- [ ] 实现收藏功能
- [ ] 实现关注功能
- [ ] 实现标签管理
- [ ] 实现热门推荐算法
- [ ] 实现消息通知功能
- [ ] 实现敏感词过滤
- [ ] 实现内容审核流程

