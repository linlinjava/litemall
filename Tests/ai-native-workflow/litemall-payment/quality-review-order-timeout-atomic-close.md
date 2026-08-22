# Quality Review：未支付订单超时关闭资源一致性

## Review Decision

- `review_mode`: `case-design-and-implementation`
- `overall_score`: 91
- `decision`: `approved-with-residual-risk`
- `blocking_findings`: none

## Findings and Evidence

| severity | area | finding | evidence | decision |
| --- | --- | --- | --- | --- |
| high | transaction consistency | timeout flow previously had no task-level transaction | `OrderTimeoutCompensationService.closeOrder()` is Spring-managed and transactional | fixed |
| high | rollback | resource failure could leave a closed order | Backend IT uses missing product failure and asserts order remains `101`, stock unchanged, coupon still used | fixed and proven |
| medium | duplicate compensation | repeated task could add stock twice | Backend IT runs close twice and asserts stock remains `7` | fixed and proven |
| medium | status guard | terminal orders must not compensate again | unit skip case plus repeated IT execution | fixed and proven |
| medium | observability | failure/skip/success needs order context | task logs order id, result and failure cause | implemented; structured assertion deferred |
| medium | concurrency | real multi-thread race is not separately exercised | optimistic-lock loss unit case proves safe loser behavior | partial; P1 follow-up |

## Case Review

| case_id | result | evidence |
| --- | --- | --- |
| `TEST-TIMEOUT-AC01-001` | passed | unit orchestration + MySQL commit |
| `TEST-TIMEOUT-AC02-001` | passed | terminal-state skip + repeated execution |
| `TEST-TIMEOUT-AC03-001` | passed | stock failure propagation + transaction rollback |
| `TEST-TIMEOUT-AC04-001` | passed | coupon update failure propagation unit |
| `TEST-TIMEOUT-AC05-001` | passed | repeated execution has no duplicate delta |
| `TEST-TIMEOUT-AC06-001` | partial | optimistic-lock loss covered; real concurrent IT deferred |
| `TEST-TIMEOUT-AC07-001` | partial | code/log review covered; structured log schema deferred |

## Residual Risk

1. Add a concurrent Testcontainers case for two real task invocations.
2. Define a structured task-result log schema and assert captured fields.
3. Add a multi-item fail-once adapter if the project later adopts compensating
   retries beyond database rollback.

## Handoff

- `next_skill`: `test-strategy`
- `selected_layers`: `backend-ut`, `backend-it`, plus full regression `all`
- `release_boundary`: approve the implemented rollback/idempotency scope; do not
  claim real concurrent execution or structured log collection as proven.
