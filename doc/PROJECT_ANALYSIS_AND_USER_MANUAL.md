# litemall 项目分析报告与使用手册

本文档基于当前仓库 `D:\litemall` 的实际结构整理，目标是让新成员能够快速理解项目、搭建环境、启动系统、定位配置，并具备二次开发和部署的基本判断。

## 1. 项目概览

`litemall` 是一个学习型、原型级的小商城系统，整体由 Spring Boot 后端、Vue 管理后台、微信小程序商城、Vue 移动商城组成。项目覆盖了电商系统的典型链路：商品展示、分类、品牌、购物车、下单、订单、支付、售后、优惠券、团购、会员、后台运营、权限、对象存储、通知和统计报表。

当前项目是一个多端商城工程：

- 后端：Spring Boot 2.1.5.RELEASE + Maven 多模块。
- 数据库：MySQL + MyBatis + PageHelper + Druid。
- 管理后台：Vue 2 + Element UI，默认开发端口 `9527`。
- 移动 H5 商城：Vue 2 + Vant，默认开发端口通常为 `6255`。
- 微信小程序：原生小程序项目，接口指向 `/wx` 后端。
- 部署：支持手工部署、外部配置覆盖、Docker Compose。

项目适合作为商城系统学习、课程设计、内部原型或二次开发基础。若要正式商用，应先完成安全加固、支付链路重审、依赖升级评估、监控告警、备份恢复和压力测试。

## 2. 目录结构分析

| 路径 | 作用 |
| --- | --- |
| `pom.xml` | Maven 父工程，统一管理后端模块、版本和依赖。 |
| `litemall-core` | 后端通用能力模块，包括响应封装、校验、微信配置、对象存储、通知、物流、异步任务等。 |
| `litemall-db` | 数据访问模块，包括 MyBatis Mapper、领域对象、业务 Service、数据库脚本。 |
| `litemall-admin-api` | 管理后台 REST API，路径前缀主要为 `/admin`。 |
| `litemall-wx-api` | 小程序/H5 商城 REST API，路径前缀主要为 `/wx`。 |
| `litemall-all` | 聚合后端服务，将 core、db、admin-api、wx-api 打包为一个可运行 Jar。 |
| `litemall-all-war` | WAR 包部署形态。 |
| `litemall-admin` | 管理后台 Vue 前端。 |
| `litemall-vue` | 移动 H5 商城 Vue 前端。 |
| `litemall-wx` | 微信小程序商城端。 |
| `renard-wx` | 另一套微信小程序前端实现。 |
| `litemall-db/sql` | 数据库初始化脚本。 |
| `docker` | Docker Compose 和后端镜像构建配置。 |
| `deploy` | 传统服务器部署脚本和示例配置。 |
| `doc` | 原项目文档和本文档。 |
| `storage` | 本地对象存储目录，上传文件可能写入这里。 |
| `logs` | 本地日志目录。 |

## 3. 后端架构

后端采用 Maven 父子模块结构。推荐开发和本地演示时优先运行 `litemall-all`。它依赖 `core`、`db`、`wx-api` 和 `admin-api`，统一启动后同时提供：

- 管理后台接口：`http://localhost:8080/admin/...`
- 商城端接口：`http://localhost:8080/wx/...`

### 3.1 litemall-core

该模块提供跨业务复用能力：

- 统一响应工具 `ResponseUtil`
- 全局异常处理
- 参数校验
- Jackson 配置
- CORS 跨域配置
- 异步任务配置
- 微信小程序和微信支付配置
- 邮件、短信通知能力
- 本地/阿里云/腾讯云/七牛对象存储
- 物流查询配置
- 二维码服务
- 系统配置加载

核心配置文件是 `litemall-core/src/main/resources/application-core.yml`。其中包含微信、通知、物流、对象存储等配置。当前文件中存在示例 appid、secret、云存储 key 等字段，正式使用时必须替换为自己的配置，不能直接沿用示例值。

### 3.2 litemall-db

该模块是数据访问层和基础业务服务层：

- `domain`：数据库表对应的实体对象和 Example 查询对象。
- `dao`：MyBatis Mapper。
- `service`：面向业务模块的数据库服务。
- `util`：订单、优惠券、团购、售后等业务常量与工具。
- `mybatis`：JSON 类型处理器。
- `sql`：数据库脚本。

核心配置文件是 `litemall-db/src/main/resources/application-db.yml`。默认数据库配置：

- 地址：`jdbc:mysql://localhost:3306/litemall`
- 用户名：`litemall`
- 密码：`litemall123456`
- 时区：`Asia/Shanghai`

数据库初始化脚本顺序：`litemall_schema.sql`、`litemall_table.sql`、`litemall_data.sql`。

### 3.3 litemall-admin-api

该模块提供管理后台接口，典型控制器包括会员、商品、订单、售后、推广、系统、配置、统计等模块。管理端安全使用 Apache Shiro，配合角色、权限和验证码能力。管理后台接口通常以 `/admin` 为前缀。

### 3.4 litemall-wx-api

该模块提供小程序和 H5 商城接口，覆盖登录注册、首页、分类、商品、品牌、购物车、订单、地址、收藏、足迹、评价、反馈、优惠券、团购、专题、搜索、售后、文件存储等功能。商城端接口通常以 `/wx` 为前缀。用户登录态使用 JWT 相关工具和 `@LoginUser` 参数解析机制。

### 3.5 litemall-all

这是本地最常用的后端入口。配置文件是 `litemall-all/src/main/resources/application.yml`。

```yaml
spring:
  profiles:
    active: db, core, admin, wx

server:
  port: 8080
```

这表示启动 `litemall-all` 时会同时加载数据库、核心能力、管理端、商城端配置。默认服务端口是 `8080`。

## 4. 前端架构

### 4.1 管理后台 litemall-admin

目录：`litemall-admin`

技术栈：Vue 2.6、Vue Router、Vuex、Element UI、Axios、ECharts、v-charts、vue-element-admin 风格脚手架。

主要页面目录：

- `src/views/user`：会员相关。
- `src/views/mall`：商城运营相关。
- `src/views/goods`：商品管理。
- `src/views/promotion`：推广营销。
- `src/views/sys`：系统管理。
- `src/views/config`：配置管理。
- `src/views/stat`：统计报表。
- `src/views/profile`：个人中心。

开发环境接口配置：`litemall-admin/.env.development` 中的 `VUE_APP_BASE_API = '/admin'`。前端开发服务启动后访问 `http://localhost:9527`。

### 4.2 移动 H5 商城 litemall-vue

目录：`litemall-vue`

技术栈：Vue 2、Vue Router、Vuex、Vant、Axios。主要页面目录包括 `home`、`items`、`login`、`order`、`user`。开发环境接口配置为 `litemall-vue/.env.development` 中的 `VUE_APP_BASE_API = '/wx'`。启动后通常访问 `http://localhost:6255`。该模块原文档中标注为开发中，稳定性弱于管理后台和小程序端。

### 4.3 微信小程序 litemall-wx

目录：`litemall-wx`

接口配置文件是 `litemall-wx/config/api.js`。默认接口根地址：

```js
var WxApiRoot = 'http://localhost:8080/wx/';
```

微信小程序开发时，需要在微信开发者工具中导入 `litemall-wx`，并根据实际情况配置自己的小程序 appid。开发阶段可启用“不校验合法域名、web-view、TLS 版本以及 HTTPS 证书”。

## 5. 数据库设计与初始化

数据库脚本位于 `litemall-db/sql`：

1. 执行 `litemall_schema.sql`，创建数据库、用户和权限。
2. 执行 `litemall_table.sql`，创建业务表。
3. 执行 `litemall_data.sql`，导入测试数据。

默认数据库名为 `litemall`，用户名为 `litemall`，密码为 `litemall123456`。

设计特点：

- 表之间没有大量使用数据库外键，主要依赖 Java Service 层维护业务关系。
- 便于快速修改表结构，但对代码校验和业务一致性要求更高。
- 上线前建议补充约束、索引、审计日志和关键业务保护。

## 6. 本地开发环境准备

建议安装：JDK 8 或以上、Maven、MySQL 5.7 或兼容版本、Node.js 和 npm、微信开发者工具、Git、IDEA、VS Code。

版本提醒：后端声明 Java 版本为 `1.8`，Spring Boot 版本为 `2.1.5.RELEASE`。管理后台要求 Node `>=8.9`、npm `>=3.0.0`，但实际安装依赖时建议使用较旧的 Node LTS 版本以减少 Vue CLI 旧项目兼容问题。

## 7. 本地启动手册

### 7.1 启动数据库

确保 MySQL 已启动，然后按顺序导入脚本：

```bash
mysql -uroot -p < litemall-db/sql/litemall_schema.sql
mysql -uroot -p litemall < litemall-db/sql/litemall_table.sql
mysql -uroot -p litemall < litemall-db/sql/litemall_data.sql
```

如果不用命令行，也可以使用 MySQL Workbench 等工具按顺序执行脚本。

### 7.2 启动后端

在项目根目录执行：

```bash
mvn install
mvn clean package
java -Dfile.encoding=UTF-8 -jar litemall-all/target/litemall-all-0.1.0-exec.jar
```

也可以进入 `litemall-all` 模块运行：

```bash
mvn spring-boot:run
```

启动成功后可访问：

- `http://localhost:8080/wx/index/index`
- `http://localhost:8080/admin/index/index`

如果返回 JSON，说明后端聚合服务正常。

### 7.3 启动管理后台

```bash
cd litemall-admin
npm install
npm run dev
```

访问 `http://localhost:9527`。如果登录页面出现，说明前端服务正常。若登录失败，优先检查后端 `8080` 服务、数据库初始化和代理配置。

### 7.4 启动移动 H5 商城

```bash
cd litemall-vue
npm install
npm run dev
```

访问 `http://localhost:6255`，建议使用 Chrome 手机模拟模式查看。

### 7.5 启动微信小程序

1. 打开微信开发者工具。
2. 导入 `litemall-wx` 目录。
3. 确认 `litemall-wx/config/api.js` 中的 `WxApiRoot` 指向后端，例如 `http://localhost:8080/wx/`。
4. 开发阶段可关闭合法域名校验。
5. 点击编译。

微信登录、支付、订阅消息等功能依赖真实微信 appid、secret、商户号、支付密钥和回调域名。默认配置只能支持普通页面和测试数据浏览，不能代表完整线上链路。

## 8. 常用配置说明

| 配置项 | 文件 | 说明 |
| --- | --- | --- |
| 后端端口 | `litemall-all/src/main/resources/application.yml` | 默认 `8080`。 |
| 数据库连接 | `litemall-db/src/main/resources/application-db.yml` | 配置 URL、用户名、密码。 |
| 微信配置 | `litemall-core/src/main/resources/application-core.yml` | 配置 appid、secret、商户号、支付回调。 |
| 对象存储 | `litemall-core/src/main/resources/application-core.yml` | 支持 local、aliyun、tencent、qiniu。 |
| 通知配置 | `litemall-core/src/main/resources/application-core.yml` | 支持邮件、腾讯云短信、阿里云短信。 |
| 管理后台接口 | `litemall-admin/.env.development` | 默认 `/admin`。 |
| H5 商城接口 | `litemall-vue/.env.development` | 默认 `/wx`。 |
| 小程序接口 | `litemall-wx/config/api.js` | 默认 `http://localhost:8080/wx/`。 |

## 9. 打包与部署

后端 Jar 包：

```bash
mvn clean package
java -Dfile.encoding=UTF-8 -jar litemall-all/target/litemall-all-0.1.0-exec.jar
```

管理后台生产构建：

```bash
cd litemall-admin
npm run build:prod
```

移动 H5 生产构建：

```bash
cd litemall-vue
npm run build:prod
```

`litemall-all/pom.xml` 中配置了资源复制：`../litemall-admin/dist` 会复制到 `target/classes/static`，`../litemall-vue/dist` 会复制到 `target/classes/static/vue`。因此如需打包成一个后端 Jar 同时托管前端，应先构建前端，再构建 `litemall-all`。

Docker 配置位于 `docker/docker-compose.yml`，包含 `mysql57` 和 `litemall` 两个服务。正式环境必须修改密码、存储卷、网络暴露策略和后端外部配置。

## 10. 业务功能地图

商城用户端主要功能：

- 首页、分类、品牌、专题
- 新品、人气推荐
- 商品详情、规格、库存
- 搜索、搜索历史
- 购物车、下单、取消、支付、确认收货
- 订单列表、订单详情、售后申请
- 优惠券领取与使用、团购
- 收货地址、收藏、足迹、评论、意见反馈、个人中心

管理后台主要功能：

- 会员管理、地址、收藏、足迹、搜索历史、反馈
- 行政区域、品牌、订单、类目、通用问题、关键词
- 商品列表、商品上架、商品编辑、用户评论
- 广告、专题、团购规则、团购活动、优惠券
- 管理员、角色、权限、通知、对象存储、操作日志
- 商场配置、小程序配置、运费配置、订单配置
- 用户统计、订单统计、商品统计
- 个人中心、通知中心、密码修改

## 11. 二次开发指南

新增后台接口推荐流程：

1. 在 `litemall-db` 中确认或新增数据表。
2. 如需生成实体和 Mapper，使用 MyBatis Generator 配置。
3. 在 `litemall-db/service` 中封装数据库操作。
4. 在 `litemall-admin-api/web` 中新增 Controller。
5. 使用 `ResponseUtil` 返回统一响应。
6. 如涉及权限，在管理端权限体系中补充权限标识。
7. 在 `litemall-admin/src/api` 中新增前端请求方法。
8. 在 `litemall-admin/src/views` 中新增页面或表单。

新增商城端接口推荐流程：

1. 在 `litemall-db` 中准备 Service。
2. 在 `litemall-wx-api/web` 中新增 `Wx...Controller`。
3. 需要登录态时使用 `@LoginUser Integer userId`。
4. 在小程序 `config/api.js` 或 H5 前端 API 文件中补充接口地址。
5. 在小程序页面或 H5 页面中调用。

修改数据库表注意事项：

- 当前表关系主要靠业务代码维护，不要只改表而忽略 Service 和 Controller。
- 修改字段后要同步实体、Mapper XML、Example 查询对象。
- 涉及订单、库存、支付、退款的表结构变更必须补充回归测试。
- 生产环境应使用迁移脚本，不建议手工直接改库。

## 12. 响应格式和错误码

后端统一响应大致为：

```json
{
  "errno": 0,
  "errmsg": "成功",
  "data": {}
}
```

错误码分层：

- `4xx`：参数或请求使用错误。
- `5xx`：系统级错误或登录状态错误。
- `6xx`：管理后台业务错误。
- `7xx`：商城端业务错误。

前端应按错误码做统一处理，具体业务错误再在页面内展示。

## 13. 安全与上线风险

上线前必须重点处理：

- 修改默认数据库密码。
- 修改默认管理员账号和密码。
- 替换所有示例 appid、secret、云存储密钥、短信密钥、邮件密码。
- 不要把真实密钥提交到 Git。
- 关闭或限制 Swagger、调试日志和不必要的公开接口。
- 后端 `8080` 端口建议只允许内网访问，由 Nginx 反向代理暴露 HTTPS。
- 小程序上线必须配置合法域名和 HTTPS。
- 微信支付回调地址必须使用真实 HTTPS 域名。
- 退款功能风险高，建议增加人工复核、短信验证、限额或改为到微信商户平台人工处理。
- 图片上传、验证码、登录接口需要限流。
- 订单、支付、退款、库存等关键操作需要审计日志。
- 需要定期备份数据库和上传文件目录。

## 14. 常见问题

### 14.1 后端启动失败，提示数据库连接失败

检查 MySQL 是否启动、是否导入了 `litemall_schema.sql`、用户名密码是否仍为 `litemall / litemall123456`，以及 `application-db.yml` 中端口、库名、时区是否正确。

### 14.2 管理后台页面能打开，但登录失败

检查 `http://localhost:8080/admin/index/index` 是否有响应，管理后台 `.env.development` 是否为 `/admin`，前端开发服务代理是否正常，数据库测试数据是否导入。

### 14.3 小程序没有数据

检查 `litemall-wx/config/api.js` 的 `WxApiRoot` 是否指向可访问的后端，`http://localhost:8080/wx/home/index` 是否有响应，微信开发者工具是否关闭了开发阶段合法域名校验。手机真机预览时，`localhost` 指的是手机自身，应改成电脑局域网 IP。

### 14.4 前端依赖安装失败

原因通常是项目较旧、依赖版本较老。建议使用较旧 Node LTS 版本，删除 `node_modules` 后重新安装，使用稳定 npm registry，并避免随意升级 Vue CLI、Webpack、Element UI、Vant 等大版本。

### 14.5 图片上传后无法访问

检查 `litemall.storage.active` 是否为 `local`，`storagePath` 是否存在且后端进程有写权限，`address` 是否指向正确后端地址。如果部署在服务器，确认反向代理是否允许访问 `/wx/storage/fetch/`。

## 15. 推荐开发顺序

1. 导入数据库，启动 `litemall-all`。
2. 访问 `/wx/index/index` 和 `/admin/index/index` 验证后端。
3. 启动 `litemall-admin`，熟悉后台功能菜单。
4. 启动 `litemall-wx` 或 `litemall-vue`，体验用户下单链路。
5. 阅读 `litemall-db/service`，理解数据操作。
6. 阅读 `litemall-admin-api/web` 和 `litemall-wx-api/web`，理解接口组织。
7. 阅读 `litemall-core`，理解配置、存储、通知、异常处理。
8. 再开始修改业务功能。

## 16. 当前项目总体评价

优点：

- 模块划分清晰，适合学习完整商城业务。
- 管理端、用户端、数据库、部署资料比较齐全。
- 后端分层直观，Controller、Service、Mapper 易于定位。
- 支持多种对象存储、通知、微信支付和小程序能力。
- 聚合模块 `litemall-all` 降低了本地启动复杂度。

不足和风险：

- 项目依赖整体偏旧，前端依赖尤其需要注意 Node 版本兼容。
- 默认配置含大量示例密钥和弱密码，上线前必须清理。
- 数据库缺少外键，业务一致性依赖代码质量。
- 安全能力偏基础，正式商用需要补充限流、审计、风控和权限加固。
- H5 移动商城原文档标注仍不稳定。
- 支付、退款、售后等资金相关流程必须重新评审。

结论：该项目适合作为商城系统学习、课程设计、二次开发起点或内部原型。若要正式商用，应先完成安全加固、依赖升级评估、支付链路重审、部署隔离、监控告警、备份恢复和压力测试。
