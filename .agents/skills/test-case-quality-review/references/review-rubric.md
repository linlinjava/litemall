# Test Case Design Review Rubric

Use this rubric for a formal review. It evaluates design quality, not test
layers, implementation files, commands, frameworks, or runtime readiness.

| Dimension | Passing evidence | Blocking condition |
| --- | --- | --- |
| Traceability | Every P0/P1 obligation maps to a Case or accepted blocker | Unmapped P0 obligation |
| Oracle | Expected behavior is observable and source-backed | Missing or contradictory P0/P1 oracle |
| Case specification | Preconditions, data, action, expected result, and cleanup are implementable | Material behavior must be guessed |
| Existing coverage | Reuse/extend/repair decision is based on inspected assertions | Filename-only or stale coverage claim |
| Completeness | Applicable positive, negative, boundary, state, retry, ownership, and concurrency risks are addressed | Relevant high-risk condition omitted |
| Economy | Cases are deduplicated by obligation, condition, action, and oracle | Duplicate Cases inflate coverage |

`PASS_WITH_RESIDUAL_RISK` requires a bounded non-P0 gap, named owner, and
follow-up trigger. Review findings return to Test Case Design; reviewers do not
silently rewrite Cases.
