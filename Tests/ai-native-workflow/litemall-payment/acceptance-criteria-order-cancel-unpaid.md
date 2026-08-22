# Acceptance Criteria：取消未支付订单

## Acceptance Criteria

| ac_id | story_id | type | Given | When | Then | observable_evidence | source |
| --- | --- | --- | --- | --- | --- | --- | --- |
| AC01 | MALL-ORD-CANCEL-001 | happy-path | 当前用户拥有状态为 101（未支付）的订单 | 用户以 `POST /order/cancel` 提交 `orderId` | 接口成功；订单状态变为 102（用户取消）；订单产生结束时间 | API response、订单对象/数据库状态、`end_time` | `WxOrderService.cancel`、`OrderUtil.build` |
| AC02 | MALL-ORD-CANCEL-001 | compensation | 未支付订单已占用商品库存 | 取消成功 | 订单中每个货品按订单数量回补库存；库存不出现负数或重复回补 | 商品货品数据库数量、库存服务调用 | `WxOrderService.cancel`、`LitemallGoodsProductService.addStock` |
| AC03 | MALL-ORD-CANCEL-001 | compensation | 未支付订单占用了状态为 USED 的优惠券 | 取消成功 | 相关优惠券恢复为 USABLE，并更新时间 | `litemall_coupon_user.status`、更新时间 | `WxOrderService.cancel`、`releaseCoupon` |
| AC04 | MALL-ORD-CANCEL-001 | guard | 订单不是当前用户的未支付订单，或订单状态为已支付/已发货/已取消 | 用户提交取消请求 | 返回业务错误；订单、库存和优惠券不发生取消副作用 | `errno/errmsg`、无更新/无库存调用/无优惠券更新 | `OrderUtil.build`、`WxOrderService.cancel` |
| AC05 | MALL-ORD-CANCEL-001 | contract | 客户端调用订单取消能力 | 客户端发送取消请求 | 请求使用 `POST /order/cancel`，body 必须包含 `orderId` | Controller mapping、contract fixture、前端 request spy | `WxOrderController.cancel`、`order-api.contract.json` |
| AC06 | MALL-ORD-CANCEL-001 | consistency | 取消过程中库存回补或优惠券释放失败 | 服务执行取消流程 | 订单状态、库存和优惠券不应留下部分成功状态；失败应可观察并回滚或进入明确补偿 | 事务回滚/错误响应/数据库一致性 | `@Transactional`、`WxOrderService.cancel` |

## Business Rule Table

| rule_id | condition | expected_result | risk_tag | evidence |
| --- | --- | --- | --- | --- |
| R01 | `order_status = 101` 且用户归属匹配 | 允许取消 | order-status-transition | `OrderUtil.build` |
| R02 | `order_status ∈ {201, 301, 102, 103}` | 拒绝取消，不产生资源副作用 | refund-guard / idempotency | `OrderUtil.build` |
| R03 | 取消成功 | `order_status = 102` 且设置 `end_time` | order-status-transition | `WxOrderService.cancel` |
| R04 | 订单包含货品 | 每个货品按数量回补 | stock-compensation | `addStock` |
| R05 | 订单关联已使用优惠券 | 优惠券变为可用 | coupon-release | `releaseCoupon` |
| R06 | 取消流程中出现运行时失败 | 事务一致性可验证 | transaction-consistency | `@Transactional` |

## Open Decisions

- AC01–AC05 依据当前业务规则和已有测试资产进入本轮验证。
- AC06 当前只有源码上的 `@Transactional` 依据，没有完整取消流程的真实事务失败测试，列为本轮 residual risk。
- 当前实现对重复取消返回“订单不能取消”；是否要求幂等成功需要产品确认。

## Downstream Handoff

- `next_skill`: `test-case-design`
- `generation_focus`: 按 Feature 查找 AC09 相关已有用例，合并取消、库存、优惠券、接口契约和浏览器用例
