# Acceptance Criteria：未支付订单超时关闭资源一致性

## Acceptance Criteria

| ac_id | story_id | type | Given | When | Then | observable_evidence | source |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `MALL-ORD-TIMEOUT-AC01` | `MALL-ORD-TIMEOUT-001` | happy-path | 订单状态为 `101`，包含商品库存和已使用优惠券，且已达到超时时间 | 执行一次超时关闭任务 | 订单变为 `103`，全部库存回补一次，优惠券变为可用，任务成功结束 | 订单行、商品库存行、优惠券行、任务结果日志 | requirement R01–R04; `OrderUnpaidTask` |
| `MALL-ORD-TIMEOUT-AC02` | `MALL-ORD-TIMEOUT-001` | state-guard | 订单状态不是 `101`，包括已支付、用户取消或已自动取消 | 执行超时关闭任务 | 不更新订单、不回补库存、不释放优惠券，任务安全跳过 | 订单状态和资源副作用未变化 | requirement R01/R05 |
| `MALL-ORD-TIMEOUT-AC03` | `MALL-ORD-TIMEOUT-001` | failure | 订单状态为 `101`，库存回补在任一商品上失败 | 执行超时关闭任务 | 不得报告完整成功；订单与资源保持可恢复的一致状态，失败原因可定位 | 订单、库存、优惠券状态；异常结果；日志 | requirement R02/R06 |
| `MALL-ORD-TIMEOUT-AC04` | `MALL-ORD-TIMEOUT-001` | failure | 库存回补成功后，优惠券释放失败 | 执行超时关闭任务 | 不得静默成功；系统按确定策略回滚或登记可重试补偿，不能留下无主资源状态 | 订单、库存、优惠券状态；补偿记录或重试证据 | requirement R02/R06 |
| `MALL-ORD-TIMEOUT-AC05` | `MALL-ORD-TIMEOUT-001` | partial-failure | 订单包含多个商品，前一个商品回补成功，后一个商品回补失败 | 执行失败后再重试 | 最终每个商品库存只恢复一次，订单最终状态与全部资源一致 | 多商品库存行、订单行、重试结果 | requirement R03/R05 |
| `MALL-ORD-TIMEOUT-AC06` | `MALL-ORD-TIMEOUT-001` | concurrency | 同一订单被两个超时任务同时触发 | 并发执行任务 | 最多一个任务完成补偿；不重复更新订单、库存或优惠券 | 并发任务结果、数据库状态、资源变更次数 | requirement R03/R05 |
| `MALL-ORD-TIMEOUT-AC07` | `MALL-ORD-TIMEOUT-001` | observability | 超时任务成功、跳过或失败 | 任务处理结束 | 日志包含订单号、结果、失败原因和重试上下文，不包含敏感信息 | 结构化/可检索日志 | requirement R07 |

## Business Rule Table

| rule_id | condition | expected_result | risk_tag | evidence |
| --- | --- | --- | --- | --- |
| R01 | only order status `101` | eligible for timeout close | status-transition | order row |
| R02 | any resource compensation fails | atomic rollback or explicit recoverable compensation; never silent success | transaction-consistency | DB + failure result |
| R03 | same order resource is released | at most once | duplicate-compensation | DB delta |
| R04 | normal timeout close | status `103`, stock restored, coupon usable | timeout-compensation | DB rows |
| R05 | task repeats or races | safe skip/retry, no duplicate side effects | concurrency | task and DB evidence |
| R06 | task fails | error is visible and retry/owner is defined | observability | log/alert evidence |

## Open Decisions

1. AC03/AC04 的一致性策略必须在实现前确认：数据库事务回滚，或补偿记录 + 可重试状态机。
2. AC06 是否作为本期 P0，还是先作为 P1/Release Gate。
3. 日志格式、重试次数、退避时间和人工接管条件需要技术/运营确认。

## Downstream Handoff

- `next_skill`: `test-case-design`
- `generation_focus`: 超时状态门禁、原子性/可恢复补偿、重复执行、并发、日志证据
- `blocked_decisions`: AC03/AC04 的一致性策略、AC06 优先级、重试策略
