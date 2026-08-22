# 需求：未支付订单超时关闭必须保持资源一致性

## Requirement Status

- `status`: implemented-and-tested
- `implementation_change`: transaction-boundary service, task delegation, failure logging, and integration fixtures
- `story_id`: `MALL-ORD-TIMEOUT-001`
- `chain`: `order-lifecycle`, `timeout-compensation`, `stock-compensation`, `coupon-release`

## Objective

当未支付订单达到超时时间后，系统应自动关闭订单，并可靠释放订单占用的
库存和优惠券。订单状态、库存和优惠券必须保持一致：不能出现订单已经关闭
但库存未回补，也不能出现资源已经释放但订单仍显示为待支付。

如果超时任务因数据库、库存或优惠券服务暂时失败，系统必须能够安全重试，
不会重复回补库存或重复释放优惠券。

## User Story

作为商城运营方，我希望未支付订单超时后能够自动、完整地释放占用资源，
以便库存和优惠券不会因为异常任务而长期不可用，也不会因为重试产生重复补偿。

## Current Implementation Gap

当前 `OrderUnpaidTask.run()` 的处理顺序是：

1. 将订单状态更新为系统自动取消 `103`；
2. 逐条回补商品库存；
3. 释放优惠券。

当前任务已委托给 Spring 管理的 `OrderTimeoutCompensationService`，由服务方法
统一声明事务边界。订单状态、库存回补和优惠券释放在同一事务内执行，任一步
失败都会抛出异常并回滚；订单状态门禁与乐观锁保证重复任务安全跳过。

Source evidence:

- `litemall/litemall-wx-api/src/main/java/org/linlinjava/litemall/wx/task/OrderUnpaidTask.java`
- `OrderUnpaidTask.run()` 先调用 `updateWithOptimisticLocker`，再调用 `addStock`，最后调用 `releaseCoupon`。
- 当前已有测试覆盖正常超时路径的业务规则，但没有完整的故障注入、原子性和安全重试验收。

## In Scope

1. 未支付订单达到超时时间后进入系统自动取消状态 `103`。
2. 订单中的全部商品库存只回补一次。
3. 已占用优惠券只释放一次，并恢复为可用状态。
4. 订单状态、库存、优惠券的最终结果必须一致。
5. 库存回补或优惠券释放失败时，定义事务回滚或补偿重试策略。
6. 超时任务重复触发时不得产生重复库存或优惠券副作用。
7. 任务失败、重试、跳过和最终成功必须有可审计日志。

## Out of Scope

- 用户主动取消未支付订单的交互流程。
- 已支付订单的退款。
- 新增通用消息队列平台。
- 修改库存预占模型或优惠券数据模型。
- 运营后台的完整任务监控页面。

## Proposed Business Rules

- R01：只有状态为待支付 `101` 的订单可以被超时任务关闭。
- R02：订单关闭与资源释放必须遵循确定的原子性或可恢复补偿策略。
- R03：一个订单的每个商品库存最多被超时补偿一次。
- R04：一个订单的优惠券最多被超时释放一次。
- R05：任务重复执行必须安全，不得把已关闭订单再次当作待支付订单处理。
- R06：资源释放失败不能静默吞掉；任务必须失败、重试或进入明确的人工处理状态。
- R07：任务日志必须包含订单号、任务结果、失败原因和重试上下文，但不得输出敏感信息。

## Acceptance-Oriented Success Criteria

- 正常路径：待支付订单超时后变为 `103`，库存和优惠券均恢复。
- 库存失败：系统不会留下无法解释的“订单已关闭但库存未恢复”状态，且存在可验证的恢复路径。
- 优惠券失败：系统不会静默返回成功，且存在可验证的回滚或重试路径。
- 部分库存补偿失败：重试后最终库存数量正确，不重复计算已经完成的补偿。
- 重复任务：第二次执行不重复改变订单、库存或优惠券。
- 并发任务：同一订单同时触发多个超时任务时，最多一个任务完成资源补偿。
- 审计日志：成功和失败任务都能关联到订单号和处理结果。

## Testing Implications

预计需要覆盖：

- Backend UT：状态门禁、补偿顺序、重复执行和失败分支。
- Backend IT：订单、库存、优惠券真实持久化一致性。
- Failure Injection：库存失败、优惠券失败、部分商品失败后的回滚/重试。
- Concurrency：同一订单多个超时任务的竞争行为。
- Observability：失败日志包含必要上下文且不泄漏敏感信息。
- E2E：只有当超时关闭结果需要从用户界面观察时才增加，不用 E2E 代替事务证据。

## Assumptions

- 本次实现不新增数据库表、消息队列或补偿状态机。
- 订单状态 `101` 表示待支付，`103` 表示系统自动取消。
- 产品和技术方案需要共同决定采用数据库事务、补偿记录、幂等标记或其他可恢复机制。

## Remaining Verification Boundaries

- 并发任务的真实多线程竞争尚未单独建立测试；当前有乐观锁失败单测和重复
  执行 IT 证据。
- 日志已包含订单号、成功/跳过/失败和失败原因，但尚未建立结构化日志 schema
  或日志采集断言。

## Downstream Handoff

- `next_skill`: `acceptance-criteria`
- `required_focus`: 事务边界、补偿幂等、故障注入、并发任务、持久化一致性、审计日志
- `implementation_status`: not implemented
- `do_not_claim`: 不得从现有正常路径测试通过推断故障恢复和事务一致性已实现
- `expected_gap`: 当前超时任务缺少完整的故障恢复和重复补偿证明
