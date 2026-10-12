# gamerecom

基于 Spring Boot + MyBatis + MySQL 的游戏推荐系统项目，包含前台展示、后台管理、用户/管理员账号体系、评论、购物车、游戏信息和分类管理等功能。

## 项目简介

本项目用于实现游戏信息展示与推荐相关业务，配套前后台页面和数据库持久化。当前仓库代码结构显示系统已经具备：

- 前台用户页面：游戏首页、游戏详情、评论、购物车、个人账号等
- 后台管理页面：用户管理、管理员管理、游戏管理、分类管理、评论管理等
- 基础推荐能力：项目中引入了 Mahout 等推荐相关依赖
- 文件上传与图片展示：支持游戏封面、轮播图和静态资源管理

## 技术栈

- JDK 1.8
- Spring Boot 2.1.x
- MyBatis / tk.mybatis
- MySQL
- Thymeleaf
- PageHelper
- Hutool
- Apache POI
- Thumbnailator
- Fastjson

## 目录结构

- `src/main/java`：后端 Java 代码
- `src/main/resources/mapper`：MyBatis 映射文件
- `src/main/resources/static/front`：前台页面资源
- `src/main/resources/static/end`：后台页面资源
- `src/main/resources/static/picfile`：上传图片资源

## 本地运行

1. 准备环境：安装 JDK 1.8、Maven、MySQL。
2. 创建数据库：数据库名为 `gamerecom`。
3. 导入数据：如果你有课程论文或数据库脚本，先将表结构和初始数据导入 MySQL。
4. 修改配置：编辑 `src/main/resources/application.yml`，更新数据库账号、密码和连接地址。
5. 启动项目：在项目根目录执行 `mvn spring-boot:run`，或直接运行启动类 `com.example.gamerecom.gamerecomApplication`。
6. 访问系统：默认端口为 `8080`。

## 配置说明

`application.yml` 中默认数据库配置如下：

- 地址：`jdbc:mysql://localhost:3306/gamerecom`
- 用户名：`root`
- 密码：`123456`

如果你的本地数据库配置不同，请先修改后再启动。

## 功能说明

- 用户注册、登录、退出
- 游戏信息浏览、搜索、分类筛选
- 游戏评论与互动
- 购物车相关操作
- 后台对用户、管理员、游戏、分类、评论等数据的维护
- 图片上传与静态资源展示

## 说明

当前 README 是根据仓库代码结构整理的基础版说明。如果你把论文正文、摘要或章节目录发我，我可以继续把这里补成更贴近论文的项目简介、研究背景、系统设计和功能亮点。