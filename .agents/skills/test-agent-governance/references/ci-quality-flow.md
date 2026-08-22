# Test Agent CI Quality Flow

Load only for CI gate placement, review flow, or release-quality decisions.

## Purpose

Turn AI Native testing evidence into CI gates, review flow, quality reports, and rollout decisions. This skill protects the team from expensive gates in the wrong place and from unreviewed skips.

## Required Inputs

Read:

- Workflow Run output from `test-agent-workflow`
- Test Strategy from `test-strategy`
- Latest run logs for `fast`, `infra`, `e2e`, and targeted commands
- Quality Review findings and residual risk
- `test-index.yml`
- Existing CI stages and runtime constraints

## Gate Model

| gate | recommended scope | reason |
| --- | --- | --- |
| PR Fast | backend UT, contract, frontend UT, frontend IT | quick feedback and no container runtime dependency |
| PR Infra | targeted backend IT for DB, optimistic lock, stock/coupon, or transaction risks | real integration evidence before merge when risk demands it |
| Coverage Review | coverage report generation after functional layers pass | gap analysis without treating coverage percentage as behavior proof |
| Main Smoke | key E2E smoke | golden path confidence after merge |
| Nightly | full backend IT, extended E2E, flaky detection | slower regression and environment confidence |
| Release | selected high-risk workflows plus verification report | release sign-off and residual-risk acceptance |

## Review Flow

| review | required evidence |
| --- | --- |
| Story Review | Story, AC, assumptions, human-gate decisions |
| Test Case Review | approved `case_id` values, oracles, steps, data, and existing-case decisions |
| Strategy Review | `case_id`, stable `test_case_id`, run-specific `execution_id`, selected layer, distinct evidence claim, target, command, runtime, and residual risk |
| Script Review | generated or modified files, assertions, fixture determinism |
| Coverage Review | functional test evidence, JaCoCo/Vitest report path, gap explanation |
| Feature Review | selected/skipped commands, run evidence, residual risk |
| CI Review | gate placement, runtime cost, retry/flaky policy |

## Output Format

```markdown
## CI Gate Plan
| gate | command | trigger | required | skip_policy |
| --- | --- | --- | --- | --- |

## Quality Report
| dimension | status | evidence | owner |
| --- | --- | --- | --- |

## Review Checklist
| review | reviewer | evidence_link | decision |
| --- | --- | --- | --- |

## Residual Risk Acceptance
| risk | reason | owner | expiration |
| --- | --- | --- | --- |
```

## Guardrails

- Do not put every test in PR if the result makes developers bypass the gate.
- Do not skip `backend-it` for DB, optimistic lock, stock/coupon, or transaction side-effect changes without an explicit owner and follow-up gate.
- Do not use E2E for rule combinations that belong in UT or IT.
- Do not treat coverage percentage as behavior proof; coverage review needs functional layer evidence plus gap explanation.
- Do not accept an AI-generated case or script without Quality Review evidence.
