---
name: test-case-quality-review
description: Use when a layer-neutral Test Case Design needs an independent approval decision before Test Strategy, especially for traceability, completeness, oracle quality, reuse evidence, duplication, and residual risk.
---

# Test Case Quality Review

## Purpose

Act as the approval gate between `test-case-design` and `test-strategy`.
Review the designed business cases; do not choose test layers, commands,
runtimes, execution profiles, mock boundaries, or target files.

Do not silently rewrite the design. Return findings to `test-case-design` when
a case must be added or changed.

## Required Context

- Requirement or Story Card
- Acceptance Criteria and Business Rule Table
- Test Case Design, including obligations, existing-case decisions, cases,
  technique artifacts, and blockers
- Relevant existing tests when a reuse, duplication, or repair decision needs
  confirmation

Optional sources such as a quality KB or code graph support discovery but do
not override current requirements and contracts.

## Review Checklist

Read [Review Rubric](references/review-rubric.md) when recording formal findings
or when a `PASS_WITH_RESIDUAL_RISK` decision is considered.

1. Verify every P0/P1 AC and obligation maps to a case or explicit blocker.
2. Verify every case cites a source and has an observable oracle.
3. Verify preconditions, data, actions, expected results, and cleanup are
   concrete enough for later implementation.
4. Verify applicable happy, negative, boundary, state, retry, ownership, and
   concurrency conditions are represented without applying a fixed checklist
   to unrelated features.
5. Inspect assertions for cases marked `reuse`, `extend`, `repair`, or
   `retire`; reject filename-only coverage claims.
6. Reject duplicate cases that prove the same obligation, data condition,
   action, and oracle.
7. Require a technique artifact only when that technique shaped the cases.
8. Confirm the design does not select layers, frameworks, commands, runtimes,
   target files, or execution profiles.

## Decisions

- `PASS`: the approved case set is ready for Test Strategy.
- `REVISE`: the design can be corrected without a product decision.
- `BLOCKED`: requirement, ownership, contract, or oracle clarification is
  required.
- `PASS_WITH_RESIDUAL_RISK`: a bounded non-blocking gap has an owner and
  follow-up trigger.

The decision cannot pass when a P0 obligation has no case or accepted blocker,
a P0/P1 case has no oracle, actions or expected results are not implementable,
or an existing-case decision is unsupported.

## Output

```markdown
## Test Case Quality Review
- design_id:
- design_version:
- decision: PASS | REVISE | BLOCKED | PASS_WITH_RESIDUAL_RISK
- scope_basis:

## Findings
| severity | case_id_or_obligation_id | issue | evidence | required_change | blocking |
| --- | --- | --- | --- | --- | --- |

## Coverage Verdict
| obligation_id | case_ids | status | residual_risk |
| --- | --- | --- | --- |

## Existing Case Verdict
| existing_case_id | decision | evidence | required_change |
| --- | --- | --- | --- |

## Approval
- approved_case_ids:
- excluded_case_ids:
- residual_design_risks:
- next_skill: test-strategy
```

`test-strategy` consumes only a passing design version. It chooses the
execution scope and evidence layers without changing the business cases.
