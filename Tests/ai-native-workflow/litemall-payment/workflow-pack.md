# Payment Reliability Test Agent Workflow Pack

schema_version: 2
workflow_id: litemall-payment
chain: payment-callback-reliability

## Identity Contract

| identity | owner | meaning |
| --- | --- | --- |
| case_id | Test Case Design | stable layer-neutral business Case |
| test_case_id | Test Strategy | stable executable Case for one selected layer |
| execution_id | Test Strategy run | one ordered execution item |

## Acceptance Criteria

| ac_id | priority | behavior | observable oracle |
| --- | --- | --- | --- |
| AC01 | P0 | 订单更新为已支付，并持久化 pay_id 与 pay_time | mutated order status, pay_id, pay_time, MySQL row |
| AC02 | P0 | 系统幂等返回成功，不能再次更新订单或重复移除任务 | success response, no duplicate update, mutation count |
| AC03 | P0 | 系统失败返回且不更新订单 | FAIL response, order unchanged, no side effect |
| AC04 | P1 | 系统不能触发订单副作用，并保持 XML 契约可诊断 | XML fixture fields, FAIL response, no lookup/update |
| AC05 | P0 | 乐观锁拒绝覆盖，且停止通知和超时任务移除 | optimistic lock result, MySQL row status, no side effect |
| AC06 | P0 | 库存不能扣成负数，补偿路径可以回补库存 | product number, stock decrement, stock restore |
| AC07 | P1 | 请求路径、方法和 payload 必须符合契约 | captured request body, contract fixture, OpenAPI apispec |
| AC08 | P1 | 页面最终进入 paid 状态，重复回调只生效一次 | visible paid status, mutation count, Playwright trace when failed |
| AC09 | P0 | 订单置为已取消，并回补库存、释放已占用优惠券 | order status, stock restored, coupon released |
| AC10 | P0 | 团购状态被正确推进，查询忽略未支付占位和逻辑删除记录 | groupon status, share image, filtered MySQL rows |
| AC11 | P1 | 前端文案和动作映射必须与后端状态机保持一致 | pure function output, status table assertions |
| AC12 | P0 | 仅已付款未发货订单可进入退款中并通知运营 | order refund status, notification evidence, failed invalid request |
| AC13 | P1 | 接口契约必须保持稳定 | captured request body, contract method/path |
| AC14 | P1 | 接口契约必须保持稳定 | captured request body, contract method/path |

## Test Case Design

design_id: TCD-LITEMALL-PAYMENT-001
design_version: 2
boundary: business Cases only; no layer, framework, command, runtime, or target selection

| case_id | ac_id | origin | priority | purpose | oracle |
| --- | --- | --- | --- | --- | --- |
| MALL-PAY-AC01-001 | AC01 | existing | P0 | 支付成功回调更新订单状态 | mutated order, SUCCESS response |
| MALL-PAY-AC02-002 | AC02 | existing | P0 | 重复支付回调幂等返回成功 | no update invocation, SUCCESS response |
| MALL-PAY-AC03-003 | AC03 | existing | P0 | 金额不一致拒绝更新订单 | FAIL response, no update invocation |
| MALL-PAY-AC04-004 | AC04 | existing | P1 | 微信支付回调 XML 必填字段稳定 | XML fixture validation |
| MALL-PAY-AC05-005 | AC05 | existing | P0 | 过期订单副本不能覆盖最新支付状态 | MySQL row status, optimistic lock result |
| MALL-PAY-AC06-006 | AC06 | existing | P0 | 库存不能为负且可回补 | MySQL product number |
| MALL-PAY-AC07-007 | AC07 | existing | P1 | 移动端订单 API 发送符合契约的请求 | captured request body |
| MALL-PAY-AC08-008 | AC08 | existing | P1 | 浏览器支付主链路只进入一次 paid 状态 | visible paid status, mutation count |
| MALL-PAY-AC09-009 | AC09 | new | P0 | 取消未支付订单恢复库存并释放优惠券 | mutated order, stock call, coupon state |
| MALL-PAY-AC10-010 | AC10 | new | P0 | 团购支付达到阈值后成团 | source groupon status, join groupon status |
| MALL-PAY-AC11-011 | AC11 | new | P1 | 前端订单状态和可用动作映射稳定 | pure function output |
| MALL-PAY-AC12-012 | AC12 | new | P0 | 已付款订单申请退款进入退款中 | mutated order, notification evidence |
| MALL-PAY-AC13-013 | AC13 | new | P1 | 移动端发送确认收货请求 | captured request body |
| MALL-PAY-AC14-014 | AC14 | new | P1 | 移动端发送删除订单请求 | captured request body |

## Test Case Quality Review

design: TCD-LITEMALL-PAYMENT-001 v2
decision: PASS
approved_case_count: 14
boundary: approves Case quality only; does not choose layers or commands

## Test Strategy

strategy_id: TS-LITEMALL-PAYMENT-001
execution_profile: release-focused
decision: READY

| order | execution_id | case_id | test_case_id | layer | layer_reference | action | command |
| ---: | --- | --- | --- | --- | --- | --- | --- |
| 1 | EXEC-20260815-001 | MALL-PAY-AC01-001 | MALL-PAY-AC01-BE-UT-001 | backend-ut | references/layers/backend-ut.md | run-existing | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 2 | EXEC-20260815-002 | MALL-PAY-AC02-002 | MALL-PAY-AC02-BE-UT-001 | backend-ut | references/layers/backend-ut.md | run-existing | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 3 | EXEC-20260815-003 | MALL-PAY-AC03-003 | MALL-PAY-AC03-BE-UT-001 | backend-ut | references/layers/backend-ut.md | run-existing | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 4 | EXEC-20260815-004 | MALL-PAY-AC04-004 | MALL-PAY-AC04-CONTRACT-001 | contract | references/layers/contract.md | run-existing | ./Tests/scripts/run-layered-tests.sh contract |
| 5 | EXEC-20260815-005 | MALL-PAY-AC05-005 | MALL-PAY-AC05-BE-IT-001 | backend-it | references/layers/backend-it-testcontainers.md | run-existing | ./Tests/scripts/run-layered-tests.sh backend-it |
| 6 | EXEC-20260815-006 | MALL-PAY-AC06-006 | MALL-PAY-AC06-BE-IT-001 | backend-it | references/layers/backend-it-testcontainers.md | run-existing | ./Tests/scripts/run-layered-tests.sh backend-it |
| 7 | EXEC-20260815-007 | MALL-PAY-AC07-007 | MALL-PAY-AC07-FE-IT-001 | frontend-it | references/layers/frontend-it.md | run-existing | ./Tests/scripts/run-layered-tests.sh frontend-it |
| 8 | EXEC-20260815-008 | MALL-PAY-AC08-008 | MALL-PAY-AC08-E2E-001 | e2e | references/layers/e2e.md | run-existing | ./Tests/scripts/run-layered-tests.sh e2e |
| 9 | EXEC-20260815-009 | MALL-PAY-AC09-009 | MALL-PAY-AC09-BE-UT-001 | backend-ut | references/layers/backend-ut.md | create-new | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 10 | EXEC-20260815-010 | MALL-PAY-AC10-010 | MALL-PAY-AC10-BE-UT-001 | backend-ut | references/layers/backend-ut.md | create-new | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 11 | EXEC-20260815-011 | MALL-PAY-AC11-011 | MALL-PAY-AC11-FE-UT-001 | frontend-ut | references/layers/frontend-ut.md | create-new | ./Tests/scripts/run-layered-tests.sh frontend-ut |
| 12 | EXEC-20260815-012 | MALL-PAY-AC12-012 | MALL-PAY-AC12-BE-UT-001 | backend-ut | references/layers/backend-ut.md | create-new | ./Tests/scripts/run-layered-tests.sh backend-ut |
| 13 | EXEC-20260815-013 | MALL-PAY-AC13-013 | MALL-PAY-AC13-FE-IT-001 | frontend-it | references/layers/frontend-it.md | create-new | ./Tests/scripts/run-layered-tests.sh frontend-it |
| 14 | EXEC-20260815-014 | MALL-PAY-AC14-014 | MALL-PAY-AC14-FE-IT-001 | frontend-it | references/layers/frontend-it.md | create-new | ./Tests/scripts/run-layered-tests.sh frontend-it |

## Execution and Repair Contract

- Test Execution loads only the layer_reference values selected above.
- The project CLI and Tests/test-index.yml are execution authorities.
- Product defects are reported unless product-code repair was explicitly authorized.
- Design defects return to Case Design and Review; Strategy defects return to Test Strategy.
- Valid results: PASSED, FAILED, BLOCKED, SKIPPED, NOT_PROVEN, INCOMPLETE.

## Validation

    ruby ".agents/skills/test-agent-workflow/scripts/validate-workflow.rb"
    python3 "AI Native/scripts/build-litemall-quality-kb.py"
    python3 "AI Native/scripts/run-litemall-skill-benchmark.py"
