# Test Agent Skill Self Optimization

Load only for an explicit, benchmark-backed Skill optimization experiment.

## Purpose

Improve skills through a small, auditable loop: observe benchmark gaps, change one skill or resource, rerun the benchmark, accept only measurable improvement, and log the experiment.

## Hard Boundaries

- Do not modify backend or frontend business code during a skill optimization experiment.
- Do not edit benchmark scenarios to make a candidate pass.
- Do not accept a skill change that improves one scenario while regressing a P0 scenario.
- Do not delete user work or reset the repo automatically. If a change should be rejected, describe the revert patch or ask before destructive git operations.

## Workflow

1. Establish a structural and project benchmark baseline:

```bash
ruby ".agents/skills/test-agent-workflow/scripts/validate-workflow.rb"
```

Then resolve and run the actual project-declared KB builder and benchmark; do
not assume generic script filenames exist.

2. Read the benchmark output path reported by the runner, then identify the weakest scenario or missing asset.
3. Choose exactly one optimization target:
   - skill trigger clarity
   - quality rubric gap
   - benchmark coverage gap
   - KB extraction gap
   - existing case discovery gap
   - self-repair decision gap
   - output handoff completeness
4. Make the smallest useful edit.
5. Rerun the same commands.
6. Accept only if:
   - total score improves or a named gap closes
   - no critical scenario regresses
   - generated outputs remain traceable to Story/AC/Test Case/Review
   - existing case reuse decisions do not hide uncovered P0/P1 risk
   - self-repair changes do not alter business expectations without a human gate
7. Append the result to `quality-benchmark/skill-experiments.md`.

## Experiment Log Format

```markdown
## EXP-YYYYMMDD-NN
- hypothesis:
- changed_files:
- baseline_score:
- candidate_score:
- accepted: yes | no
- regression_check:
- reviewer_notes:
```

## Recommended Loop Cadence

- During the two-day workshop: one guided optimization at the end of day 2.
- During normal delivery: run when quality review finds repeated shallow cases or planner handoffs miss the same risk twice.
- Before sharing the skills broadly: run the benchmark and freeze the baseline.

## Review Checklist

- The skill description still triggers on real user language.
- The body teaches a decision that an AI might otherwise get wrong.
- Scripted checks cover deterministic facts.
- AI judgment is reserved for semantic review and tradeoff decisions.
- Benchmark history explains why the change was accepted.
