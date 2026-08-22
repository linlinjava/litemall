# Execution Evidence Schema

Read this when recording or validating execution evidence.

| Field | Required evidence |
| --- | --- |
| Identity | `execution_id`, `case_id`, `test_case_id`, and layer |
| Change | action, changed test file, and concise change reason |
| Command | exact command and declared command source |
| Runtime | framework/runtime versions and required service state |
| Selection proof | evidence that the intended current-source test ran |
| Oracle observation | actual observation satisfying or falsifying the approved oracle |
| Result | `PASSED`, `FAILED`, `BLOCKED`, `SKIPPED`, `NOT_PROVEN`, or `INCOMPLETE` |
| Artifacts | report, log, coverage, trace, screenshot, or video paths when produced |

## Result Status

| Status | Meaning |
| --- | --- |
| `PASSED` | intended current-source test ran and satisfied the oracle |
| `FAILED` | intended test ran and falsified the oracle |
| `BLOCKED` | requirement, dependency, runtime, or environment prevented evidence |
| `SKIPPED` | Strategy explicitly deferred or skipped the execution item |
| `NOT_PROVEN` | available evidence cannot prove the Case claim |
| `INCOMPLETE` | setup, selector, stale artifact, or partial run invalidated the result |

A zero-test selector, stale report, swallowed setup failure, or undeclared skip
cannot produce `PASSED`.

```markdown
## Test Execution Report
| execution_id | case_id | test_case_id | layer | action | test_file | command | runtime | result | oracle_observation | artifacts |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## Changed Test Assets
| execution_id | file | change | reason |
| --- | --- | --- | --- |

## Unproven or Deferred
| execution_id | case_id | test_case_id | status | reason | residual_risk | owner |
| --- | --- | --- | --- | --- | --- | --- |
```
