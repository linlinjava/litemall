# Failure Repair Loop

Read this stage after a failed or invalid execution result. Classify before
editing and return the defect to the stage that owns the faulty decision.

## Workflow

1. Capture the first failing command, meaningful error, `execution_id`,
   `case_id`, `test_case_id`, layer, runtime, and artifacts.
2. Classify the failure before editing:

| Classification | Default action |
| --- | --- |
| `PRODUCT_DEFECT` | report; modify production only with explicit authorization |
| `DESIGN_DEFECT` | return to Case Design, Review, then Strategy |
| `STRATEGY_DEFECT` | return to Strategy for layer/target/command/runtime/order repair |
| `TEST_DEFECT` | repair the executable test without changing the oracle |
| `FIXTURE_DEFECT` | repair deterministic setup, data, reset, or mock boundary |
| `RUNNER_DEFECT` | repair selector, stale-source handling, exit status, or reporting |
| `ENVIRONMENT_DEFECT` | repair in-scope runtime or report `BLOCKED` |
| `REQUIREMENT_CONFLICT` | stop for product clarification |

3. Make the smallest authorized change explaining the failure.
4. Rerun the focused execution item.
5. When green, rerun Strategy-declared adjacent or layer gates.
6. Confirm the intended current-source test ran and collect new evidence.

## Output

```markdown
## Failure Summary
- execution_id:
- case_id:
- test_case_id:
- command:
- first_error:
- classification:

## Repair
- authorization:
- changed_files:
- design_or_strategy_changed:
- reasoning:

## Verification
- commands:
- result:
- artifacts:

## Residual Risk
- status:
- reason:
- owner:
```

Do not delete assertions, broaden mocks across the selected evidence boundary,
skip a selected layer because its runtime is inconvenient, or accept a green
run that did not execute the intended test.
