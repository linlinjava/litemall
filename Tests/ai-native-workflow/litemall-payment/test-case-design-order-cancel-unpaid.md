# Test Case Design：取消未支付订单

## Test Basis

| obligation_id | source | oracle | status | clarification_owner |
| --- | --- | --- | --- | --- |
| O01 | AC01 / `WxOrderService.cancel` | response success, status 102, `end_time != null` | READY | - |
| O02 | AC02 / `addStock` | every order-good quantity is restored | READY | - |
| O03 | AC03 / `releaseCoupon` | USED coupon becomes USABLE | READY | - |
| O04 | AC04 / `OrderUtil.build` | forbidden order cannot change or release resources | READY | - |
| O05 | AC05 / Controller + contract | POST path and `orderId` payload match | READY | - |
| O06 | AC06 / `@Transactional` | no partial persistence after injected failure | BLOCKED | product/test owner |

## Test Analysis

- changed_surfaces: order lifecycle service, order controller, inventory service, coupon release, mobile order API, payment-flow harness
- affected_contracts: `POST /order/cancel`, request body `{ orderId }`
- quality_concerns: transaction consistency, idempotency, data isolation
- risks: status transition, stock compensation, coupon release, ownership guard, duplicate cancel
- coverage_obligations: O01–O06

## Feature Case Discovery and Consolidation

Feature fingerprint: `AC09`, `WxOrderService.cancel`, `OrderUtil.STATUS_CREATE`,
`STATUS_CANCEL`, `releaseCoupon`, `addStock`, `/order/cancel`, `orderId`,
`MALL-PAY-AC09-*`.

| obligation_id | existing_case_id | origin | decision | evidence_gap | consolidated_case_id |
| --- | --- | --- | --- | --- | --- |
| O01/O02/O03 | `MALL-PAY-AC09-BE-UT-001` | existing | reuse | none for service-level happy path | `MALL-PAY-AC09-BE-UT-001` |
| O04 | `MALL-PAY-AC09-BE-UT-002` | existing | reuse | does not cover wrong-user or malformed body | `MALL-PAY-AC09-BE-UT-002` |
| O02 | `MALL-PAY-AC09-BE-IT-001` | existing | extend | verifies stock and coupon persistence separately, not one cancel transaction | `MALL-PAY-AC09-BE-IT-001` |
| O05 | `MALL-PAY-AC09-FE-IT-001` | existing | reuse | request spy proves client payload, not deployed endpoint | `MALL-PAY-AC09-FE-IT-001` |
| O05 | `MALL-PAY-AC00-CONTRACT-001` | existing | extend | verifies mapping and method; response schema remains partial | `MALL-PAY-AC00-CONTRACT-001` |
| O01/O02/O03 | `MALL-PAY-AC09-E2E-001` | existing | reuse | Express harness, not deployed backend | `MALL-PAY-AC09-E2E-001` |
| O06 | - | new | needs-clarification | no failure-injection transaction case | `TEST-ORD-CANCEL-AC06-001` |

## Coverage and Technique Decisions

| obligation_id | evidence_layer | technique | required_artifact | rationale |
| --- | --- | --- | --- | --- |
| O01/O02/O03 | backend-ut | direct example | input order + response + status/stock/coupon observations | one concrete service behavior already has a focused test |
| O04 | backend-ut | decision table | status/ownership/operation matrix | cancellation depends on order state and ownership |
| O05 | contract + frontend-it | direct example | endpoint mapping and captured request | consumer/provider boundary is distinct from service behavior |
| O06 | backend-it | cause-effect / direct failure example | injected failure + persisted rollback observations | requires real transaction/persistence evidence; currently not implemented |
| O01/O02/O03 | e2e | use case | browser action and visible status/stock/coupon observations | critical user journey, bounded to local harness |

## Case Specifications

| case_id | primary_ac | purpose | preconditions | test_data | steps | expected_result | oracle | priority | layer_rationale |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| MALL-PAY-AC09-BE-UT-001 | AC01/02/03 | prove successful cancellation releases resources | user 1 owns order 1001 in status 101 | product 1 × 2; coupon 501 USED | call `cancel(1,{orderId:1001})` | success, status 102, stock add 2, coupon USABLE | response/order/coupon/service observations | P0 | lowest layer for business rules |
| MALL-PAY-AC09-BE-UT-002 | AC04 | prove paid order cannot use cancel path | order status 201 | order 1001 | call cancel | errno 725; no update, stock, or coupon side effect | response and Mockito interaction boundary | P0 | pure guard rule |
| MALL-PAY-AC09-BE-IT-001 | AC02/03 | prove persistence primitives retain compensation state | MySQL Testcontainer fixture | product and coupon rows | update compensation records and reload | persisted stock/coupon state matches expected | MySQL rows | P1 | real persistence boundary |
| MALL-PAY-AC00-CONTRACT-001 | AC05 | prove endpoint method remains POST | source and contract fixture available | `/order/cancel`, `orderId` | validate mapping and contract | method/path/payload match | contract assertions | P1 | consumer/provider boundary |
| MALL-PAY-AC09-FE-IT-001 | AC05 | prove client sends cancel request correctly | request spy installed | `{ orderId: 1001 }` | call client method | POST `/order/cancel` with body | captured request | P1 | frontend integration boundary |
| MALL-PAY-AC09-E2E-001 | AC01/02/03 | prove critical user journey | local Express harness | unpaid order and occupied resources | submit, inspect, cancel, inspect | visible cancelled status, stock restored, coupon usable | Playwright DOM evidence | P1 | browser journey |
| TEST-ORD-CANCEL-AC06-001 | AC06 | prove rollback on compensation failure | failure injection available | stock or coupon update failure | cancel and reload all rows | no partial state | database transaction evidence | P1 | currently deferred |

## Existing Case Decisions

| existing_case_id | obligation_id | decision | evidence_gap_or_reason |
| --- | --- | --- | --- |
| `MALL-PAY-AC09-BE-UT-001` | O01/O02/O03 | reuse | directly proves service happy path |
| `MALL-PAY-AC09-BE-UT-002` | O04 | reuse | directly proves paid-order guard |
| `MALL-PAY-AC09-BE-IT-001` | O02/O03 | extend | persistence primitives are separated from cancel orchestration |
| `MALL-PAY-AC00-CONTRACT-001` | O05 | extend | response schema and malformed payload are not complete |
| `MALL-PAY-AC09-FE-IT-001` | O05 | reuse | exact client request is asserted |
| `MALL-PAY-AC09-E2E-001` | O01/O02/O03 | reuse | bounded harness journey already exists |

## Consolidated Case Set

| case_id | origin | source_existing_case | primary_ac | evidence_layer | technique | status |
| --- | --- | --- | --- | --- | --- | --- |
| `MALL-PAY-AC09-BE-UT-001` | existing | same | AC01/02/03 | backend-ut | direct example | READY |
| `MALL-PAY-AC09-BE-UT-002` | existing | same | AC04 | backend-ut | decision table row | READY |
| `MALL-PAY-AC09-BE-IT-001` | extended | same | AC02/03 | backend-it | direct example | READY |
| `MALL-PAY-AC00-CONTRACT-001` | extended | same | AC05 | contract | direct example | READY |
| `MALL-PAY-AC09-FE-IT-001` | existing | same | AC05 | frontend-it | direct example | READY |
| `MALL-PAY-AC09-E2E-001` | existing | same | AC01/02/03 | e2e | use case | READY |
| `TEST-ORD-CANCEL-AC06-001` | new | - | AC06 | backend-it | cause-effect/failure example | BLOCKED |

## Coverage Gaps and Blockers

| obligation_id | status | reason | owner | next_action |
| --- | --- | --- | --- | --- |
| O06 | BLOCKED | no failure-injection test proves full cancellation rollback | test owner | add transaction-failure IT after product decision |
| O04 | PARTIAL | wrong-user, malformed body, and repeat-cancel cases are not in current selected suite | test owner | add focused guard cases if API hardening is in scope |

## Handoff

- `next_skill`: `test-case-quality-review`
- `planner_input`: six READY existing/extended cases; one blocked transaction case; two residual guard gaps
