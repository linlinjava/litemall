# Test Strategy：未支付订单超时关闭资源一致性

## Test Strategy

- `design_id`: `MALL-ORD-TIMEOUT-001-DESIGN`
- `review_decision`: `approved-with-residual-risk`
- `strategy_status`: `targeted-release-evidence-complete`
- `changed_surfaces`: transaction service, timeout task delegation, stock/coupon compensation, failure logging
- `affected_contracts`: `none` (internal scheduled task)
- `primary_risks`: `status-transition`, `transaction-consistency`, `duplicate-compensation`
- `residual_risks`: real multi-thread race and structured log collection
- `execution_order`: focused UT → full `all` lane → workflow validation
- `required_runtimes`: Java/Maven; Docker/Colima for Backend IT; Node/Playwright for E2E
- `evidence_contract`: command, layer, test report, AC mapping, and explicit residual boundary

## Selected Cases and Layers

| case_id | AC | primary layer | evidence |
| --- | --- | --- | --- |
| `TEST-TIMEOUT-AC01-001` | AC01 | backend-ut/backend-it | orchestration unit + committed DB state |
| `TEST-TIMEOUT-AC02-001` | AC02 | backend-ut/backend-it | terminal-state no-op + repeated task |
| `TEST-TIMEOUT-AC03-001` | AC03 | backend-ut/backend-it | stock failure + transaction rollback |
| `TEST-TIMEOUT-AC04-001` | AC04 | backend-ut | coupon update failure propagation |
| `TEST-TIMEOUT-AC05-001` | AC05 | backend-it | repeated execution does not duplicate resource delta |
| `TEST-TIMEOUT-AC06-001` | AC06 | backend-ut | optimistic-lock loss no-op; concurrency IT deferred |
| `TEST-TIMEOUT-AC07-001` | AC07 | code/log review | task logs order id, result, and failure cause; structured assertion deferred |

## Commands

1. `./Tests/scripts/run-layered-tests.sh all`
2. `ruby .agents/skills/test-agent-workflow/scripts/validate-workflow.rb`

The `all` lane includes scripts, Backend UT, Contract, Frontend UT/IT,
Backend IT, and E2E. Backend IT uses the Colima Docker socket through
Testcontainers.

## Stop and Release Rules

- Do not approve if Backend UT or Backend IT fails.
- Do not infer transaction consistency from E2E; the order/stock/coupon DB
  assertions are the primary evidence.
- Mark AC06 and AC07 as partial until a concurrent integration harness and a
  structured log assertion exist.
