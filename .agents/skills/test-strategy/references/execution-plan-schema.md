# Execution Plan Schema

Every executable row must contain:

| Field | Contract |
| --- | --- |
| `execution_id` | Unique to this planned run or implementation action |
| `case_id` | Approved layer-neutral business Case |
| `test_case_id` | Stable executable identity for one Case and one layer |
| `layer` | Exactly one supported project layer |
| `layer_reference` | Test Execution reference to load for that layer |
| `action` | `run-existing`, `extend-existing`, `create-new`, `repair-test`, or `defer` |
| `target` | Existing or planned test asset |
| `command` | Exact project command |
| `command_source` | Location in `Tests/test-index.yml`, README, or runner |
| `runtime` | Required execution environment |
| `expected_evidence` | Observation and artifact needed to prove the oracle |

The plan is `READY` only when every non-deferred row is resolvable without
inventing a command, Case, oracle, target, or runtime.

```markdown
## Test Strategy
- strategy_id:
- design_id:
- design_version:
- execution_profile:
- selection_basis:
- gates:

## Execution Plan
| order | execution_id | case_id | test_case_id | layer | layer_reference | action | existing_test | target | command | command_source | runtime | expected_evidence |
| ---: | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## Deferred Cases
| case_id | reason | residual_risk | owner | follow_up |
| --- | --- | --- | --- | --- |

## Strategy Gate
- all_execution_items_reference_approved_cases:
- distinct_evidence_claim_per_added_layer:
- commands_resolved_from_project_sources:
- runtimes_available_or_explicitly_blocked:
- decision: READY | REVISE | BLOCKED
```

Every added layer must prove a distinct claim. Every deferment needs a reason,
owner, follow-up, and residual risk. The Strategy must not introduce a new
business Case or changed oracle.
