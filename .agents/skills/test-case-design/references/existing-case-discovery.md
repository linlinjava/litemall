# Existing Case Discovery

Load this reference from `test-case-design` when existing coverage is unclear,
the repository is large, or reuse decisions need stronger evidence.

## Procedure

1. Build a feature fingerprint from AC terms, business objects, changed
   modules, routes, state transitions, events, contracts, fixtures, and risks.
2. Search `Tests/test-index.yml`, test files, workflow packs, fixtures, runner
   selectors, and a current code graph or quality KB when available.
3. Inspect each candidate's setup, action, assertion, data condition, and
   observable business outcome.
4. Record the current file, test name, layer, command, mock/real boundary, and
   source evidence as discovery facts. These facts describe existing coverage;
   they do not select the future execution strategy.
5. Map candidates to coverage obligations and classify each as `reuse`,
   `extend`, `new`, `repair`, `retire`, or `needs-clarification`.
6. Confirm `reuse` only when the obligation, data condition, action, and oracle
   match. A passing test for obsolete behavior is not reusable coverage.

## Discovery Output

```markdown
## Existing Coverage Inventory
| existing_case_id | file | test_name | current_layer | current_command | business_assertion | source_evidence |
| --- | --- | --- | --- | --- | --- | --- |

## Obligation Match
| obligation_id | existing_case_id | decision | evidence_gap_or_reason |
| --- | --- | --- | --- |
```

Merge these decisions into the parent Test Case Design. Do not emit a separate
lifecycle artifact and do not delete or retire tests without human approval.
