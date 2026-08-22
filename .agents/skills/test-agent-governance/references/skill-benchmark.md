# Test Agent Skill Benchmark

Load only for a fixed-scenario Skill or workflow evaluation.

## Purpose

Benchmark the skills pipeline with fixed project scenarios, a fixed metric, and reviewable output. Use this before accepting changes to skills, prompts, benchmark data, or quality-review rules.

## Required Context

Read:

- `AI Native/quality-benchmark/skill-benchmark.json`
- `AI Native/quality-kb/project-quality-kb.json` if present
- `.agents/skills/test-agent-workflow/references/workflow-registry.yml`
- `AI Native/workflows/*/workflow-pack.json` when a scenario references `workflow_pack`
- The candidate Story/AC/Test Case/Review/Strategy output being evaluated

## Benchmark Contract

Each scenario must define:

- `chain`
- `story`
- `expected_risk_tags`
- `expected_layers`
- `acceptance_focus`
- `must_find`
- `min_quality_score`
- optional `workflow_pack`; when present, the pack must pass deterministic validation before the scenario can pass

Keep benchmark scenarios stable. Add new scenarios when product risk expands; do not rewrite old ones to hide regressions.

## Workflow

1. Validate the workflow structure:

```bash
ruby ".agents/skills/test-agent-workflow/scripts/validate-workflow.rb"
```

2. Resolve and run the project-declared KB builder and Skill benchmark under
   `AI Native/scripts/`; use their actual filenames and reported output paths.
3. If scoring an AI output pack, compare it manually or with a scorer against:
   - AC coverage
   - risk tag recall
   - layer fit
   - evidence observability
   - mock-boundary correctness
   - duplication and shallow-case rate
4. Fail the change if workflow validation fails or any critical scenario
   regresses.
5. Store the result in the benchmark's declared output path.

## Scoring Model

| Dimension | Points | Gate |
| --- | ---: | --- |
| Chain mapped in KB | 20 | Required |
| Expected risks known | 20 | Required for P0 |
| Expected layers available | 20 | Required |
| Must-find assets present | 20 | Required |
| Quality threshold valid | 20 | `min_quality_score >= 80` |

## Output

```markdown
## Skill Benchmark
- benchmark_file:
- kb_file:
- total_score:
- decision: pass | fail | needs-kb-refresh

## Scenario Results
| scenario | score | missing | recommendation |
| --- | ---: | --- | --- |

## Regression Decision
- accepted:
- reason:
- follow_up:
```
