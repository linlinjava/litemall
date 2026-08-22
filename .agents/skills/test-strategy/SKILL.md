---
name: test-strategy
description: Use when an approved Test Case Design needs test-layer selection and an executable plan covering case scope, implementation action, targets, project commands, runtimes, order, gates, and residual risk.
---

# Test Strategy

## Purpose

Decide **how, where, and when approved business cases will be tested**.
This is the sole owner of test-layer selection and execution planning.

Do not invent a business case, change an oracle, or add a test-design technique.
Return design gaps to `test-case-design` through quality review.

## Required Context

- Passing Test Case Quality Review
- Approved Test Case Design and `case_id` values
- Changed files or feature scope and affected contracts
- `Tests/test-index.yml`, `Tests/README.md`, and the project runner
- Available runtimes and requested delivery profile

Read [Layer Capabilities](references/layer-capabilities.md) when selecting
evidence layers, [Test Pyramid](references/test-pyramid.md) when balancing the
portfolio, and [Execution Plan Schema](references/execution-plan-schema.md)
when producing the handoff. Read only project inventory and runner entries
relevant to candidate layers.

## Strategy Workflow

1. Confirm the exact approved `design_id` and `design_version`.
2. Select the smallest case set required by the requested profile and risks.
3. For each selected `case_id`, choose one or more evidence layers only when
   each layer proves a distinct property.
4. Assign a stable, layer-specific `test_case_id` to every case-layer variant,
   using the project format. Preserve an existing `test_case_id` when reusing
   or extending the same executable test.
5. Assign an `execution_id` to every row in this Strategy run. The business
   `case_id` and executable `test_case_id` remain distinct.
6. Choose one implementation action:
   - `run-existing`
   - `extend-existing`
   - `create-new`
   - `repair-test`
   - `defer`
7. Resolve the layer reference, target asset, exact command, runtime, prerequisites,
   and expected evidence from the project test inventory and runner. Never
   infer a command from a layer name.
8. Order focused, deterministic checks before infrastructure and browser
   checks while preserving real prerequisites.
9. Record gates, deferred cases, owners, and residual risk.

Produce the handoff and apply the READY gate defined by
[Execution Plan Schema](references/execution-plan-schema.md). Send only a
`READY` plan to `test-execution`.
