---
name: test-execution
description: Use when a READY Test Strategy must be implemented and run through selected project test layers, producing current-source command evidence without redesigning Cases or changing layer choices.
---

# Test Execution

## Purpose

Consume a `READY` Test Strategy and turn its Execution Plan into test code,
commands, and evidence. Do not select layers, change an oracle, add business
Cases, or load unselected layer references.

## Required Input

- Approved Test Case Design and exact `design_version`
- `READY` Test Strategy
- Execution Plan rows containing `execution_id`, `case_id`, `test_case_id`, `layer`,
  `layer_reference`, `action`, target, command, runtime, and expected evidence
- Project `Tests/test-index.yml`, test README, and runner

Read [Execution Evidence Schema](references/evidence-schema.md) when recording
or validating results.

## Selected Layer References

Load only references named by the Execution Plan:

| Strategy layer | Reference |
| --- | --- |
| `backend-ut` | `references/layers/backend-ut.md` |
| `frontend-ut` | `references/layers/frontend-ut.md` |
| `contract` | `references/layers/contract.md` |
| `frontend-it` | `references/layers/frontend-it.md` |
| `backend-it` | `references/layers/backend-it-testcontainers.md` |
| `e2e` | `references/layers/e2e.md` |

The project CLI is the execution authority. A layer reference explains how to
implement and verify that layer; it does not replace `Tests/test-index.yml` or
invent commands.

## Execution Workflow

For each Execution Plan row in order:

1. Verify the `case_id` exists in the approved design, `test_case_id` matches
   the selected layer and project format, and the row has exactly one layer.
2. Load only the selected layer reference and inspect the target source, existing
   tests, fixtures, and project runner entry.
3. Apply the planned action:
   - `run-existing`: do not generate a duplicate test.
   - `extend-existing`: make the smallest assertion, data, or case extension.
   - `create-new`: create one focused executable test for the approved case.
   - `repair-test`: repair setup or assertion without changing the oracle.
   - `defer`: do not implement or run; preserve owner and residual risk.
4. For changed tests, run the narrowest available focused command first.
5. Run the Strategy command or declared layer gate.
6. Prove the intended current-source test ran; zero selected tests, stale
   artifacts, swallowed setup failures, or undeclared skips are not passing.
7. Record runtime, fixture/reset state, exit status, report paths, and the
   observation that satisfies or falsifies the approved oracle.

Shared implementation rules:

- Test observable behavior rather than private methods or incidental calls.
- Keep data deterministic and reset mutable state.
- Mock only outside the behavior the selected layer must prove.
- Fail loudly on missing dependencies, setup errors, and selector mismatch.
- Do not modify production behavior unless the user explicitly authorized an
  implementation or defect fix.

Produce the report defined by
[Execution Evidence Schema](references/evidence-schema.md). Send test,
fixture, runner, or environment failures to the workflow repair stage. Return
a wrong Case or oracle to Case Design review and a wrong layer, command,
target, runtime, or order to Test Strategy.
