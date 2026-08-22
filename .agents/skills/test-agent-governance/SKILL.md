---
name: test-agent-governance
description: Use when the testing workflow itself needs knowledge-base maintenance, benchmark evaluation, controlled Skill optimization, or CI quality-gate design rather than ordinary feature testing.
---

# Test Agent Governance

Operate the testing system's governance plane. This Skill is not a mandatory
tail of `test-agent-workflow` and does not design or execute feature Cases.

Load exactly one reference matching the request:

| Request | Reference |
| --- | --- |
| Build or refresh reusable project testing knowledge | [Quality KB Builder](references/quality-kb-builder.md) |
| Evaluate Skill or workflow quality against fixed scenarios | [Skill Benchmark](references/skill-benchmark.md) |
| Run a one-change-at-a-time optimization experiment | [Skill Self Optimization](references/skill-self-optimization.md) |
| Design PR, main, nightly, or release gates | [CI Quality Flow](references/ci-quality-flow.md) |

Keep deterministic facts in scripts and project inventories. Keep semantic
judgment reviewable. Never edit product business behavior during governance
work, and never weaken benchmark scenarios to make a candidate pass.
