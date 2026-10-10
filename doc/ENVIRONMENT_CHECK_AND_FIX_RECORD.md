# litemall  环境检查与问题修复记录

> 记录时间：2026-09-18
> 记录范围：本机（Windows 10）`D:\litemall` 项目的依赖环境检查、问题排查与修复全过程。
> 结论：修复后系统已可正常运行，管理后台、H5 商城、后端 API 均验证通过。

## 1. 结论摘要

| 检查项 | 结论 |
| --- | --- |
| 本机环境（JDK 8、Maven 3.6.3、MySQL 5.7） | ✅ 齐全且可用 |
| 后端聚合服务 `litemall-all` | ✅ 可正常启动，API 正常返回数据 |
| 管理后台（`http://localhost:8080/`） | ✅ 修复后正常，默认账号 `admin123 / admin123` |
| H5 商城（`http://localhost:8080/vue/index.html`） | ✅ 修复后正常，首页数据正常加载 |
| 微信小程序相关 | ⚠️ 未配置真实 appid，仅页面与测试数据可用 |

排查过程中发现并修复了 **4 个问题**，详见第 3 节。

## 2. 本机环境检查结果

| 项目 | 版本/位置 | 状态 |
| --- | --- | --- |
| JDK | 1.8.0_503，`JAVA_HOME=D:\java` | ✅ 正常 |
| Maven | 3.6.3，`D:\maven\apache-maven-3.6.3` | ✅ 正常 |
| MySQL | 5.7.44，以 `mysqld.exe` 运行在 3306（无 Windows 服务名） | ✅ 正常 |
| 数据库 | `litemall` 库存在，表结构完整；账号 `litemall / litemall123456` 验证通过 | ✅ 正常 |
| 系统默认 Node | v24.18.0 / npm 11.16.0 | ⚠️ 对旧前端工具链不兼容（见问题 1） |
| Node 14（构建用） | v14.21.3，`D:\node\node-v14.21.3-win-x64` | ✅ 正常 |
| Maven 本地仓库 | 已含 litemall 各模块 0.1.0 产物，但 jar 为空壳（见问题 3） | ⚠️ 已修复 |
| 前端依赖 | `litemall-admin`、`litemall-vue` 均已 `npm install` | ✅ 正常 |

## 3. 发现的问题与修复

### 问题 1：系统默认 Node 24 无法构建前端（旧 Vue CLI 工具链）

- **现象**：项目使用 Vue CLI 3/4（webpack 4）时代的技术栈，Node 24 下构建存在兼容风险。
- **修复**：改用 Node 14（`D:\node\node-v14.21.3-win-x64`）构建，实测一次通过。
- **用法**：
  ```bash
  export PATH="/d/node/node-v14.21.3-win-x64:$PATH"   # Git Bash 下
  ```
  或直接使用该目录下的 `node.exe` / `npm.cmd`。

### 问题 2：litemall-vue 构建报错（sass 版本 + 缺依赖）

- **现象**：`npm run build` 报两处错误：
  1. scss 编译失败：`@import "./mixin/one-border"` 无法解析；
  2. `This dependency was not found: regenerator-runtime/runtime`。
- **原因**：`package.json` 中 `"sass": "^1.26.2"` 的脱字符版本范围被 npm 解析到 1.104.1，与 sass-loader 7 及旧 scss 语法不兼容；`regenerator-runtime` 未直接声明，npm 新版本提升策略不同导致根目录找不到该包。
- **修复**（`litemall-vue/package.json`）：
  - `"sass": "1.26.2"`（精确锁定，与 litemall-admin 一致）；
  - devDependencies 增加 `"regenerator-runtime": "^0.13.11"`；
  - 用 Node 14 的 npm 重新 `npm install`。

### 问题 3：单独在 litemall-all 模块打包出"缺依赖"的 8MB 坏 jar

- **现象**：在 `litemall-all` 下执行 `mvn package`，产物 jar 只有 8MB、`BOOT-INF/lib` 仅 13 个 jar，其中 litemall 模块 jar 只有约 460 字节。
- **原因**：本地 Maven 仓库（`~/.m2`）中 litemall 各模块的 jar 是 460 字节空壳（2026-01-03 的错误产物）。单模块构建时 Spring Boot repackage 从本地仓库解析依赖，只能拿到空壳和父 pom 声明的少量 spring 依赖。
- **修复**：必须从项目根目录整体 reactor 构建：
  ```bash
  cd D:\litemall && mvn clean install -DskipTests
  ```
  reactor 构建使用各模块源码编译产物而非仓库空壳，同时会把正确的 jar 重新安装回本地仓库。修复后 jar 为 62MB、141 个依赖 jar，完整可用。

### 问题 4：生产构建的前端页面报"登录连接超时"

- **现象**：管理后台和 H5 商城页面都能打开，但登录/首页数据请求失败，弹出"登录连接超时"。
- **原因**：两个前端的 `.env.production` 中 `VUE_APP_BASE_API` 都是官方占位符，生产构建时被写死进 JS bundle：
  - `litemall-admin/.env.production`：`https://www.example.com/admin`
  - `litemall-vue/.env.production`：`https://www.example.com/`
  所有请求发往 example.com，axios 5 秒超时后触发兜底弹窗（admin 在 `src/utils/request.js:85`，vue 在 `src/utils/request.js:53`）。
- **修复**：改为本机后端地址：
  - `litemall-admin/.env.production` → `VUE_APP_BASE_API = 'http://localhost:8080/admin'`
  - `litemall-vue/.env.production` → `VUE_APP_BASE_API = 'http://localhost:8080/wx'`

## 4. 修复后的验证结果

| 验证项 | 结果 |
| --- | --- |
| 后端启动（`java -jar litemall-all-0.1.0-exec.jar`） | ✅ 约 7~11 秒启动成功 |
| `POST /admin/auth/login`（`admin123 / admin123`） | ✅ `errno:0`，正常返回 token |
| `http://localhost:8080/`（管理后台页面） | ✅ 200 |
| `http://localhost:8080/vue/index.html`（H5 商城页面） | ✅ 200 |
| `GET /wx/home/index`（商城首页 API） | ✅ 200，正常返回商品数据 |
| `GET /wx/index/index`、`GET /admin/index/index` | ✅ 200 |

注意：`/vue/` 直接访问是 404（Spring Boot 子目录不会自动补 `index.html`），需访问 `/vue/index.html`，这是项目原有行为。

## 5. 本机标准构建与启动流程（已验证）

```bash
# 1. 构建前端（必须用 Node 14）
export PATH="/d/node/node-v14.21.3-win-x64:$PATH"
cd D:/litemall/litemall-admin && npm run build
cd D:/litemall/litemall-vue && npm run build

# 2. 从根目录整体构建后端（必须 reactor 构建，原因见问题 3）
cd D:/litemall && mvn clean install -DskipTests

# 3. 启动
cd D:/litemall/litemall-all && java -jar target/litemall-all-0.1.0-exec.jar
```

访问地址：

- 管理后台：`http://localhost:8080/`（账号 `admin123 / admin123`）
- H5 商城：`http://localhost:8080/vue/index.html`
- 管理 API：`http://localhost:8080/admin/...`
- 商城 API：`http://localhost:8080/wx/...`

## 6. 未提交 Git 的本地改动清单

排查修复涉及以下文件改动（截至 2026-09-18 尚未提交）：

| 文件 | 改动 |
| --- | --- |
| `litemall-vue/package.json` | sass 锁定 1.26.2；新增 devDependency regenerator-runtime |
| `litemall-vue/package-lock.json` | 随上述依赖调整更新 |
| `litemall-admin/.env.production` | `VUE_APP_BASE_API` 改为 `http://localhost:8080/admin` |
| `litemall-vue/.env.production` | `VUE_APP_BASE_API` 改为 `http://localhost:8080/wx` |

## 7. 遗留注意点

- `litemall-core/src/main/resources/application-core.yml` 中微信 appid/secret、商户号、云存储密钥等仍为官方示例值。微信登录（报 40029 invalid code）、支付、短信、物流等需要真实配置后才能使用，不影响本地页面浏览和测试数据。
- 默认数据库密码、默认管理员账号属于开发配置，正式部署前必须修改。
- 管理后台、H5 商城的开发模式（`npm run dev`）不受第 4 节问题影响，因为 `.env.development` 使用相对路径 `/admin`、`/wx` 并走 devServer 代理。
