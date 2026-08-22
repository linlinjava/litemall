# litemall 分层自动化测试体系

本目录是为 `litemall` 建立的独立测试工程。所有新增测试代码、契约、mock/harness、运行脚本都放在 `Tests/` 下，原项目源码不做侵入式改造，适合培训、试点和后续迁移到 CI。

当前已实现测试以功能正确性为主，覆盖订单支付链路的业务规则、接口契约、真实持久化和浏览器主路径。完整测试体系还应继续补齐 performance 和 accessibility 两类非功能质量门禁。

新增的商品浏览训练切片覆盖“首页 -> 商品列表筛选 -> 商品详情 -> 相关推荐”的前后端协作，用于培训中展示支付之外的轻量业务模块如何分层测试。

## 质量维度

| 维度 | 当前状态 | 推荐技术选型 | 建议 Gate |
| --- | --- | --- | --- |
| Functional | 已实现 baseline | JUnit、Mockito、Spring Test、Testcontainers、Vitest、Playwright、Contract fixtures | PR Fast、PR Infra、Main Smoke |
| Coverage | 已实现报告生成 | JaCoCo、Vitest V8 coverage | PR Review、Training Review |
| Performance | Gate 配置已可校验，实测数据待接入 | Lighthouse CI、Core Web Vitals、Playwright trace、k6/JMeter、API p95 budget、bundle budget | PR Perf Check、Nightly Perf |
| Accessibility | 规划补齐 | axe-core、Playwright accessibility scan、键盘导航脚本、对比度检查、人工 screen reader 抽查 | PR A11y Check、Release A11y |

## 测试分层

| 层级 | 路径 | 技术选型 | 主要验证点 |
| --- | --- | --- | --- |
| 后端 UT | `backend/src/test/java/.../unit` | JUnit 4, Mockito, Spring Test | 订单状态规则、支付回调分支、金额一致性、重复回调幂等、取消补偿、退款、团购状态 |
| 后端 IT | `backend/src/test/java/.../it` | Spring Boot Test, MyBatis, Testcontainers MySQL, Colima | MySQL mapper 行为、乐观锁、库存扣减/回补、优惠券回滚、团购记录过滤 |
| 契约测试 | `backend/src/test/java/.../contract`, `contracts/` | JUnit, JSON/XML fixtures | 前端 API 路径和后端 Controller mapping 对齐、微信支付回调 XML 字段稳定、订单生命周期接口稳定 |
| 前端 UT | `frontend/tests/unit` | Vitest, jsdom | 金额展示、localStorage checkout 信息保存、订单状态动作映射 |
| 前端 IT | `frontend/tests/it` | Vitest, request spy | 移动端下单/预支付/H5 支付/结算/取消/退款/确认/删除 API 请求结构 |
| Coverage | `backend/target/site/jacoco`, `frontend/coverage` | JaCoCo, Vitest V8 coverage | 后端生产模块覆盖率报告、前端训练切片覆盖率报告 |
| AI UI Agent（可选） | `ui-agent/` | Page Agent 1.11.0, Playwright handoff | 自然语言 UI 探索、选择器修复、候选 E2E 步骤生成；不作为 CI 强门禁 |
| 前端 E2E | `e2e/tests` | Playwright, Express harness | 浏览器支付主链路、重复支付回调只生效一次、取消订单释放库存和优惠券 |
| 脚本自检 | `scripts/` | Bash, mocked npm | 分层入口脚本、npm lockfile 变更检测、重复安装保护 |

## 商品浏览切片

商品浏览模块适合做培训主模块，当前测试覆盖：

- 首页聚合：banner、频道、新品、人气、品牌、专题、团购、楼层商品。
- 商品列表：分类、品牌、关键词、新品/人气、分页、排序参数透传，登录搜索写入搜索历史。
- 商品详情：商品基础信息、属性、规格、货品库存、品牌、评论、团购规则、收藏状态、分享图和登录足迹。
- 相关推荐：详情页按同类目推荐商品，未知商品返回参数值错误。

新增关键文件：

- `Tests/backend/src/test/java/org/linlinjava/litemall/tests/unit/WxProductBrowsingUnitTest.java`
- `Tests/backend/src/test/java/org/linlinjava/litemall/tests/contract/ProductBrowsingContractTest.java`
- `Tests/contracts/product-browsing-api.contract.json`
- `Tests/frontend/tests/it/mobile-product-browsing-api.it.test.ts`
- `Tests/e2e/tests/product-browsing-flow.spec.ts`

商品发现专用命令只运行上述训练切片，适合课堂和最小证据反馈：

| 命令 | 精确资产 | 证据边界 |
| --- | --- | --- |
| `./Tests/scripts/run-layered-tests.sh browse-backend-ut` | `WxProductBrowsingUnitTest` | Controller 受控依赖下的规则；不证明真实数据库或前端交互 |
| `./Tests/scripts/run-layered-tests.sh browse-contract` | `ProductBrowsingContractTest` | 前端路径、后端 mapping、HTTP method 与详情 `id`；不证明完整 response schema |
| `./Tests/scripts/run-layered-tests.sh browse-frontend-it` | `mobile-product-browsing-api.it.test.ts` | 前端 API client 的路径和 query；不证明页面状态或服务端规则 |
| `./Tests/scripts/run-layered-tests.sh browse-e2e` | `product-browsing-flow.spec.ts` | Express harness 中的浏览器 wiring；不代表部署态 litemall |
| `./Tests/scripts/run-layered-tests.sh browse-fast` | 前三项，不含 E2E | 快速商品发现反馈；不证明浏览器交互 |

当前 E2E 能看到相关推荐文本，但没有点击推荐商品并进入新的详情页。当前详情资产能返回或显示规格和库存，但没有覆盖规格选择 UI 及零库存不可售状态。这两项应作为显式能力缺口，不得从现有绿色结果外推。

## 非功能测试规划

Performance 和 accessibility 不应挤进现有 functional baseline，也不应简单用 E2E happy path 代替。建议后续按独立 gate 补齐：

| 层级 | 建议路径 | 建议命令 | 验证点 |
| --- | --- | --- | --- |
| Performance gate config | `performance/` | `./Tests/scripts/run-layered-tests.sh performance-gate` | 校验预算、Quick/Deep mode、metric honesty、CI artifacts 规则 |
| Performance smoke | `performance/` | future: `./Tests/scripts/run-layered-tests.sh performance` | 首页、订单列表、订单详情、支付页的 Lighthouse 分数、Core Web Vitals、bundle budget |
| Backend performance | `performance/backend` | `./Tests/scripts/run-layered-tests.sh backend-perf` | 提交订单、订单详情、支付回调等关键接口的 p95 响应时间和错误率 |
| Accessibility scan | `accessibility/` | `./Tests/scripts/run-layered-tests.sh accessibility` | axe 自动扫描、语义标签、表单 label、颜色对比度、ARIA 基础问题 |
| Accessibility keyboard | `accessibility/keyboard` | `./Tests/scripts/run-layered-tests.sh a11y-keyboard` | 仅用键盘完成登录、下单、支付、退款、订单详情查看 |

建议初期将 performance/accessibility 作为 non-blocking gate 输出报告，等阈值和误报治理稳定后再提升为 blocking gate。

## 一键运行

```bash
cd /Users/summer/Work/Code/AI/training/litemall

# 快速反馈：脚本自检 + 后端 UT + 契约 + 前端 UT + 前端 IT
./Tests/scripts/run-layered-tests.sh fast

# 商品发现快速反馈：Backend UT + Contract + Frontend IT
./Tests/scripts/run-layered-tests.sh browse-fast

# 商品发现浏览器 wiring（Express harness）
./Tests/scripts/run-layered-tests.sh browse-e2e

# 后端集成测试：Testcontainers MySQL
./Tests/scripts/run-layered-tests.sh backend-it

# 前端浏览器 E2E
./Tests/scripts/run-layered-tests.sh e2e

# 性能门禁配置校验：不伪造 Lighthouse/CrUX/API 指标
./Tests/scripts/run-layered-tests.sh performance-gate

# 覆盖率报告：后端 JaCoCo + 前端 Vitest V8
./Tests/scripts/run-layered-tests.sh coverage

# 规划中的实测非功能门禁
# ./Tests/scripts/run-layered-tests.sh performance
# ./Tests/scripts/run-layered-tests.sh accessibility

# 全量验证
./Tests/scripts/run-layered-tests.sh all
```

脚本会自动处理：

- 首次运行时安装 `Tests/frontend` 和 `Tests/e2e` 的 npm 依赖。
- 当 `package-lock.json` 变化时自动使用 `npm ci` 重装依赖，避免复用陈旧的 `node_modules`。
- 每次后端测试命令开始时，将当前 litemall 后端模块安装到本地 Maven 仓库，避免测试依赖旧 jar；同一次 `all` 运行内只安装一次。
- 如果检测到 `~/.colima/default/docker.sock`，自动为 Testcontainers 设置 `DOCKER_HOST`。
- 在新 Docker/Colima 环境下使用 Testcontainers `1.21.4`，避免旧 Docker API 兼容问题。

## Coverage

Coverage 用于培训评审和缺口分析，不替代分层测试本身。当前命令会生成两类报告：

- 后端：`./Tests/scripts/run-layered-tests.sh backend-coverage`
  - 运行 backend UT、contract、backend IT。
  - 使用 JaCoCo agent 收集执行数据。
  - 使用 JaCoCo CLI 对 `litemall-core`、`litemall-db`、`litemall-wx-api` 的生产类和源码生成报告。
  - 报告位置：`Tests/backend/target/site/jacoco/index.html`、`jacoco.xml`、`jacoco.csv`。
- 前端：`./Tests/scripts/run-layered-tests.sh frontend-coverage`
  - 运行 Vitest 全部前端 UT/IT。
  - 使用 V8 coverage 统计当前训练切片。
  - 报告位置：`Tests/frontend/coverage/index.html`、`lcov.info`、`coverage-summary.json`。

当前不设置全局 coverage 阈值。原因是训练工程只覆盖支付和商品浏览切片，直接把 litemall 全量模块纳入阈值会把大量未纳入训练范围的历史功能计为失败。推荐做法是先用报告识别缺口，再为进入训练范围的模块逐步增加模块级阈值。

## Performance Gate

Performance gate 复用 `agent-skills` 的 measure-first 设计：

```text
Measure -> Identify -> Fix -> Verify -> Guard
```

当前可运行的 `performance-gate` 只校验门禁配置，不声称已经测量性能：

- Quick mode：没有 Lighthouse、CrUX、PageSpeed、DevTools trace 或 RUM artifact 时，只允许做源码级审计，所有指标必须标为 `not measured`。
- Deep mode：有测量 artifact 时，LCP、INP、CLS、Lighthouse、API p95 等指标必须标注来源。
- Metric honesty：禁止从源码推断 Core Web Vitals，禁止混用 lab 和 field 数据，禁止没有 artifact 就宣称通过。

关键文件：

- `Tests/performance/performance-gate.yml`
- `Tests/performance/validate-performance-gate.rb`

## Page Agent UI 层（可选）

Page Agent 适合放在 UI 探索和脚本生成前置层：用自然语言驱动本地页面，收集 action history，再把稳定流程转成 Playwright。它依赖 LLM key、模型行为和浏览器内执行，因此默认不纳入 `fast` 或 `all`。

场景和证据格式见 `Tests/ui-agent/page-agent-scenarios.md`。推荐训练流程是：

```text
Story/AC/Test Case ID
  -> Page Agent 探索 UI 流程
  -> 保存 prompt + trace + 可见业务证据
  -> 质量评审
  -> 转成确定性 Playwright
  -> 进入 E2E smoke
```

## Colima 准备

后端 IT 需要 Docker-compatible runtime。macOS 上推荐 Colima：

```bash
colima start --runtime docker --network-address --cpu 4 --memory 6 --disk 60
docker info
```

正常情况下无需手动导出环境变量，`run-layered-tests.sh` 会自动识别 Colima socket。如需手动指定：

```bash
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

## 训练业务切片

当前测试体系围绕 litemall 的支付可靠性链路展开：

```text
用户提交订单
  -> 扣减库存
  -> 发起支付
  -> 微信支付回调到达
  -> 校验回调金额和订单实付金额
  -> 重复回调幂等返回成功
  -> 订单标记为已支付
  -> 移除未支付超时补偿任务
```

扩展后的关键业务点：

```text
取消未支付订单
  -> 订单状态变为已取消
  -> 商品库存回补
  -> 已占用优惠券恢复为可用

团购订单支付成功
  -> 团购记录从未支付占位变为进行中
  -> 团长生成分享图
  -> 达到人数阈值时发起团和参团记录一起变为成团

退款申请
  -> 仅已付款未发货订单可申请
  -> 订单进入退款中
  -> 通知运营处理
```

## Test Case ID

- `MALL-PAY-AC01-BE-UT-001`: 支付成功回调将待支付订单更新为已支付。
- `MALL-PAY-AC02-BE-UT-001`: 重复支付回调幂等返回成功，不重复更新订单。
- `MALL-PAY-AC03-BE-UT-001`: 回调金额和订单金额不一致时拒绝更新。
- `MALL-PAY-AC04-BE-UT-001`: 微信回调解析失败时不查询、不更新订单。
- `MALL-PAY-AC04-BE-UT-002`: 回调订单号不存在时失败返回且不触发副作用。
- `MALL-PAY-AC04-CONTRACT-001`: 微信支付回调 XML 必填字段保持稳定。
- `MALL-PAY-AC04-CONTRACT-002`: 微信支付回调 XML 可装载为 SDK notify result 模型。
- `MALL-PAY-AC00-CONTRACT-001`: 契约文件内 test case id 和 HTTP method 保持稳定格式。
- `MALL-PAY-AC00-CONTRACT-002`: OpenAPI apispec 中的路径、方法、test case id 和必填字段与契约文件一致。
- `MALL-PAY-AC05-BE-UT-001`: 乐观锁失败时停止通知和超时任务移除。
- `MALL-PAY-AC05-BE-IT-001`: 订单乐观锁拒绝过期副本覆盖最新状态。
- `MALL-PAY-AC06-BE-IT-001`: 库存不能扣成负数，取消/补偿可回补库存。
- `MALL-PAY-AC01-BE-IT-001`: 已支付订单状态、支付流水号、支付时间真实持久化。
- `MALL-PAY-AC07-FE-IT-001`: 移动端订单 API 发送符合契约的请求。
- `MALL-PAY-AC08-E2E-001`: 浏览器支付主链路最终只进入一次 paid 状态。
- `MALL-PAY-AC08-UIAGENT-001`: Page Agent 探索支付主路径并生成候选 Playwright 步骤。
- `MALL-PAY-AC09-BE-UT-001`: 取消未支付订单恢复库存并释放优惠券。
- `MALL-PAY-AC09-BE-UT-002`: 已付款订单不能走取消补偿路径。
- `MALL-PAY-AC09-BE-IT-001`: 优惠券释放状态真实持久化。
- `MALL-PAY-AC09-FE-IT-001`: 移动端发送取消订单请求。
- `MALL-PAY-AC09-E2E-001`: 浏览器取消未支付订单后库存和优惠券恢复。
- `MALL-PAY-AC09-UIAGENT-001`: Page Agent 探索取消未支付订单 UI 流程并记录 trace。
- `MALL-PAY-AC10-BE-UT-001`: 参团支付达到人数阈值后团购成功。
- `MALL-PAY-AC10-BE-UT-002`: 团长支付后生成团购分享图。
- `MALL-PAY-AC10-BE-IT-001`: 团购查询忽略未支付占位和逻辑删除记录。
- `MALL-PAY-AC11-FE-UT-001`: 前端订单状态和可用动作映射稳定。
- `MALL-PAY-AC12-BE-UT-001`: 已付款订单申请退款进入退款中并通知运营。
- `MALL-PAY-AC12-BE-UT-002`: 未付款订单不能申请退款。
- `MALL-PAY-AC12-FE-IT-001`: 移动端发送退款申请请求。
- `MALL-PAY-AC12-UIAGENT-001`: Page Agent 探索退款申请 UI 流程并生成候选 smoke 步骤。
- `MALL-PAY-AC13-FE-IT-001`: 移动端发送确认收货请求。
- `MALL-PAY-AC14-FE-IT-001`: 移动端发送删除订单请求。
- `MALL-INFRA-AC01-SCRIPT-001`: 分层运行脚本在 npm lockfile 变化时使用 `npm ci`，未变化时不重复安装。
- `MALL-INFRA-AC02-SCRIPT-001`: 分层运行脚本提供 coverage 入口并分发到后端 JaCoCo 和前端 Vitest coverage。
- `MALL-BROWSE-AC01-BE-UT-001`: 首页接口聚合商品运营区块并按登录态选择优惠券来源。
- `MALL-BROWSE-AC02-BE-UT-001`: 商品列表保留筛选、分页、排序参数并写入登录用户搜索历史。
- `MALL-BROWSE-AC03-BE-UT-001`: 商品详情返回规格、货品、品牌、评论、收藏和分享信息，并记录用户足迹。
- `MALL-BROWSE-AC04-BE-UT-001`: 相关推荐按同类目返回，未知商品返回参数值错误。
- `MALL-BROWSE-AC05-BE-UT-001`: 一级分类进入商品分类页时自动选中第一个二级分类并返回兄弟分类。
- `MALL-BROWSE-AC05-BE-UT-002`: 二级分类进入商品分类页时保留当前分类并返回父级兄弟分类。
- `MALL-BROWSE-AC06-BE-UT-001`: 商品总数接口返回在售商品数量。
- `MALL-BROWSE-AC00-CONTRACT-001`: 商品浏览契约中的前端路径和后端 mapping 保持一致。
- `MALL-BROWSE-AC01-FE-IT-001`: 移动端请求首页聚合接口。
- `MALL-BROWSE-AC02-FE-IT-001`: 移动端商品列表筛选和分页参数透传。
- `MALL-BROWSE-AC03-FE-IT-001`: 移动端按商品 ID 请求详情。
- `MALL-BROWSE-AC04-FE-IT-001`: 移动端按商品 ID 请求相关推荐。
- `MALL-BROWSE-AC03-E2E-001`: 浏览器从首页商品进入详情并看到库存、规格和相关推荐。
- `MALL-BROWSE-AC02-E2E-001`: 浏览器商品列表按新品筛选且排序查询保持稳定。

## 目录说明

- `test-index.yml`: 测试层级、命令、责任边界和测试文件索引。
- `contracts/`: 前后端接口契约、OpenAPI apispec、人读接口文档和微信支付回调样例。
- `backend/`: 独立 Maven 测试工程，依赖 litemall 原模块。
- `frontend/`: 独立 Vitest 测试工程，通过 alias 引用 `litemall-vue/src`。
- `ui-agent/`: Page Agent UI 探索场景、证据格式和 Playwright handoff 规则。
- `e2e/`: Playwright + Express 支付链路训练 harness。
- `scripts/run-layered-tests.sh`: 团队本地和 CI 都可复用的统一入口。
- `scripts/run-layered-tests-script-test.sh`: 分层入口脚本的轻量自检。
