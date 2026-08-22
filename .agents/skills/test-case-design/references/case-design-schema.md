# Case Design Schema

Read this when producing or validating the Test Case Design handoff.

```markdown
## Test Case Design
- design_id:
- design_version:
- feature_scope:

## Coverage Obligations
| obligation_id | source | risk | oracle | priority | status |
| --- | --- | --- | --- | --- | --- |

## Existing Case Decisions
| existing_case_id | obligation_id | decision | evidence_gap_or_reason | source |
| --- | --- | --- | --- | --- |

## Test Cases
| case_id | obligation_id | origin | purpose | preconditions | test_data | actions | expected_result | oracle | priority |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## Technique Artifacts
Include only artifacts for techniques that materially shaped the cases.

## Gaps and Blockers
| obligation_id | status | reason | owner | next_action |
| --- | --- | --- | --- | --- |

## Handoff
- next_skill: test-case-quality-review
```

## Completion Gate

- Every P0/P1 obligation maps to a Case or explicit blocker.
- Every Case has a source and observable oracle.
- Every relevant existing Case has a decision.
- Actions, data, expected results, and cleanup are implementable without
  repeating business analysis.
- No test layer, framework, command, runtime, target file, or execution profile
  is selected.
