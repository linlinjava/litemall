# 需求：取消未支付订单并释放占用资源

## Story Card

- `story_id`: `MALL-ORD-CANCEL-001`
- `title`: 取消未支付订单并释放库存与优惠券
- `chain`: `order-lifecycle`, `stock-compensation`, `coupon-release`
- `actor`: 已登录商城用户
- `user_story`: 作为商城用户，我希望能够取消尚未支付的订单，以便及时释放被订单占用的库存和优惠券，避免资源长期被锁定。
- `business_value`: 降低未支付订单对库存和优惠券可用性的影响，保持订单状态与占用资源的一致性。

## In Scope

1. 用户对未支付订单发起取消操作。
2. 订单进入已取消状态。
3. 订单已扣减或占用的商品库存得到回补。
4. 订单已占用的优惠券恢复为可用状态。
5. 取消结果能够被订单详情或订单列表观察到。

## Out of Scope

- 已支付订单的退款流程。
- 已发货订单的售后处理。
- 支付回调本身的金额校验和幂等处理。
- 新增库存预占模型或优惠券生命周期模型。
- 浏览器端完整 UI 重构。

## Source Evidence

- `Tests/ai-native-workflow/litemall-payment/workflow-pack.md`：AC09，取消未支付订单后回补库存并释放优惠券。
- `Tests/test-index.yml`：`backend-ut`、`backend-it`、`frontend-it`、`e2e` 层的订单取消覆盖边界。
- `WxOrderService.cancel`：订单取消业务入口。
- `releaseCoupon`：优惠券释放逻辑。
- `LitemallGoodsProductService.addStock`：库存回补逻辑。
- `WxOrderLifecycleUnitTest`、`PaymentPersistenceIT`：现有生命周期与持久化测试资产。

## Impacted Objects

- 订单状态与订单取消接口。
- 商品货品库存。
- 用户优惠券占用状态。
- 移动端订单操作请求。
- 订单详情/列表中的状态与可用动作展示。

## Risk Tags

- `order-status-transition`
- `stock-compensation`
- `coupon-release`
- `transaction-consistency`
- `idempotency`
- `frontend-api-contract`

## Assumptions

- 本需求只讨论未支付订单。
- 库存和优惠券是订单取消时需要处理的已占用资源。
- 业务预期必须以产品/业务确认后的 Acceptance Criteria 为准，当前实现不能单独作为 Oracle。

## Open Questions

1. 取消操作允许的订单状态是否只有“待支付”？超时关闭订单是否复用同一规则？
2. 重复取消同一订单时，接口应返回成功、幂等成功，还是业务错误？
3. 订单取消时，订单状态、库存回补、优惠券释放是否必须保持原子性？
4. 优惠券已过期、已被其他流程使用或释放失败时，订单取消应如何处理？
5. 用户端取消和后台超时补偿是否必须共享同一个接口与业务服务？
6. 取消成功后，订单详情和列表是否需要立即刷新可用动作与资源状态？

## Downstream Handoff

- `next_skill`: `acceptance-criteria`
- `required_focus`: 未支付状态边界、订单状态转移、库存回补、优惠券释放、重复取消、事务一致性、移动端请求契约
- `blocked_until`: Open Questions 1–4 至少完成业务确认后再固化 P0/P1 Acceptance Criteria
