# Final Test Report

Read this only when the orchestrator aggregates completed workflow artifacts.

```markdown
## Test Report
- workflow_id:
- requirement:
- design_id_and_version:
- strategy_id:
- overall_result: PASSED | FAILED | BLOCKED | PARTIALLY_PROVEN

## Requirement Traceability
| ac_id | obligation_id | case_id | test_case_ids | execution_ids | result | evidence |
| --- | --- | --- | --- | --- | --- | --- |

## Layer Results
| layer | new_tests | existing_regression | result | evidence |
| --- | ---: | ---: | --- | --- |

## Test Changes
| execution_id | test_case_id | action | file | change |
| --- | --- | --- | --- | --- |

## Unproven and Residual Risk
| ac_id_or_case_id | status | reason | owner | follow_up |
| --- | --- | --- | --- | --- |
```

Separate newly added tests from existing regression. Do not label all existing
regression as impact coverage. Never infer requirement success from a green
aggregate command alone; preserve blockers, skips, stale evidence, and
`NOT_PROVEN` boundaries.

## HTML Review Template

When the final report is rendered as HTML, use a review-first structure:

1. Put the final result on the first visible line:
   `PASS`, `BLOCK`, or `NEEDS_VERIFY`.
2. Render `01 · LAYER RESULTS`, `02 · STRATEGY TRACEABILITY`,
   `03 · CASE REVIEW`, and `04 · REVIEW DECISION` as native disclosure
   sections that are closed by default. Do not add an `open` attribute. Each
   closed summary must show the section name plus a short summary of the
   content inside it.
3. The `01` closed summary must show the layer count, total new-test count,
   existing-regression status, and current-run status. Do not use a generic
   “click to view details” sentence as the summary.
4. Inside the expanded layer section, show each layer's command, new tests,
   existing regression, last evidence result, current run status, and coverage
   judgment.
5. The `02` summary should expose the number of Cases and how many are
   proven versus partial, while the expanded content shows the Test Strategy
   to Test Case mapping before the detailed Case list.
6. The `03` summary should expose the Case count and result split. Render each
   Case as a separate disclosure section, also closed by default.
   The expanded content must include purpose, strategy layer, preconditions,
   steps, oracle, evidence, and unresolved gap.
7. The `04` summary should expose the number of P1 risks and environment
   verification items. Keep the review decision inside the expanded section,
   while the first visible status line remains outside all disclosures.

Canonical HTML shape:

```html
<div class="status">PASS | BLOCK | NEEDS_VERIFY</div>
<details class="layer-report">
  <summary>01 · LAYER RESULTS · 分层测试执行结果</summary>
  <!-- layer result table -->
</details>
<details class="section-report">
  <summary>02 · STRATEGY TRACEABILITY · 8 cases · 6 proven · 2 partial</summary>
  <!-- Strategy → Case mapping -->
</details>
<details class="section-report">
  <summary>03 · CASE REVIEW · 8 cases · 6 PASS · 2 PARTIAL</summary>
  <!-- expandable Case list -->
</details>
<details class="section-report">
  <summary>04 · REVIEW DECISION · 2 P1 risks · 1 environment check</summary>
  <!-- residual risks and decision -->
</details>
<details class="case">
  <summary>case_id · result · purpose</summary>
  <!-- case detail and evidence -->
</details>
<section id="review-decision">Residual risks and decision</section>
```

The report generator must preserve the distinction between historical
successful evidence and the current run. A blocked current run is
`NEEDS_VERIFY`, even when the previous evidence package was green.
