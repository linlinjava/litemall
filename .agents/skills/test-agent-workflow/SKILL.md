---
name: test-agent-workflow
description: Use when a requirement or feature needs the complete testing lifecycle from acceptance analysis through test design, strategy, execution evidence, repair routing, and final reporting.
---

# Test Agent Workflow

Coordinate the testing lifecycle and enforce artifact gates. Delegate design,
review, strategy, and execution decisions to their owning Skills; do not make
those decisions in the orchestrator.

## Route

1. If an approved requirement is absent, read
   [User Story Intake](references/stages/01-user-story-intake.md).
2. If observable acceptance criteria are absent, read
   [Acceptance Criteria](references/stages/02-acceptance-criteria.md).
3. Use `test-case-design` to produce layer-neutral business Cases.
4. Use `test-case-quality-review`; continue only for `PASS` or an explicitly
   accepted `PASS_WITH_RESIDUAL_RISK` design version.
5. Use `test-strategy` to select cases, layers, actions, targets, commands,
   runtimes, order, and gates. Continue only when the decision is `READY`.
6. Use `test-execution` to implement and run only the READY plan.
7. On failure, read [Failure Repair Loop](references/stages/07-failure-repair-loop.md)
   and return the defect to its owning stage.
8. Aggregate AC-to-evidence traceability using
   [Final Test Report](references/final-test-report.md).

Read [Artifact Contracts](references/artifact-contracts.md) when validating a
handoff, and [Workflow Registry](references/workflow-registry.yml) when routing
or validating paths. For independent coverage analysis, Case review, or
evidence reproduction, delegate to the project custom agent `test_engineer`
defined in `.codex/agents/test-engineer.toml`; ordinary workflow runs do not
require a subagent.

## Boundaries

- Case Design owns obligations, existing-case decisions, techniques, Cases,
  and oracles; it never selects a test layer.
- Quality Review approves or returns the design; it never rewrites Cases.
- Test Strategy is the sole owner of layer and execution planning.
- Test Execution follows project commands declared by `Tests/test-index.yml`;
  it never invents a command or changes a Case oracle.
- Product-code changes require separate authorization. A product failure is a
  reportable defect by default.
- Governance is optional. Use `test-agent-governance` only for KB, benchmark,
  Skill optimization, or CI-quality-flow work.

Run `ruby .agents/skills/test-agent-workflow/scripts/validate-workflow.rb`
after changing this workflow.
