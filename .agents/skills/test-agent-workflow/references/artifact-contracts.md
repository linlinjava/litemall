# Artifact Contracts

Read this when validating a stage handoff or tracing a final result.

| Producer | Required output | Consumer |
| --- | --- | --- |
| Requirement stages | Story, AC, rules, observable oracles, open decisions | Test Case Design |
| Test Case Design | versioned design, obligations, existing-case decisions, stable layer-neutral `case_id` values | Quality Review |
| Quality Review | decision, findings, approved design version and Case IDs | Test Strategy |
| Test Strategy | `execution_id`, `case_id`, layer-specific `test_case_id`, layer, action, target, command source, runtime, order, expected evidence | Test Execution |
| Test Execution | changed test assets, commands, runtime, result, oracle observation, evidence paths | Orchestrator |
| Orchestrator | AC-to-obligation-to-Case-to-execution traceability and residual risk | Human or CI gate |

## Identity

- `case_id` is a stable business Case and has no test layer.
- `test_case_id` is a stable executable Case for one `case_id` and one layer.
- `execution_id` identifies one planned run or implementation action.

## Gates

- Missing or contradictory product behavior blocks Case Design.
- Quality Review must approve the exact design version consumed by Strategy.
- Execution starts only from a `READY` Strategy.
- A result passes only when the intended current-source test ran and its
  observation satisfies the approved oracle.

## Repair Ownership

| Defect | Return to |
| --- | --- |
| Missing Case, wrong data/action/oracle | Test Case Design, then Review |
| Wrong layer, target, command, runtime, order, or gate | Test Strategy |
| Test code, fixture, runner, or environment | Failure Repair and targeted Test Execution |
| Product implementation | Report by default; fix only with explicit authorization |
