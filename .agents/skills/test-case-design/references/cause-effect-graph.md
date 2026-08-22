# Cause-Effect Graph Reference

Use for business rules where several inputs, guards, or state facts combine to
produce an outcome, especially when a decision table would otherwise be
written informally.

1. List atomic causes: input predicates, state predicates, permissions, and
   external responses.
2. List observable effects: returned result, state change, emitted event,
   persisted value, or user-visible outcome.
3. Connect causes to effects with explicit AND/OR/NOT logic. Record mutually
   exclusive, requires, and masks constraints.
4. Convert the constrained graph into a decision table. Remove infeasible
   rows and mark why each removed row is infeasible.
5. Select the smallest set of feasible rows that covers each required effect,
   each important cause, and each constraint boundary.
6. Turn selected rows into Case Specifications with concrete data and an
   oracle. The graph/table is the design evidence; the cases are the
   executable intent.

Minimum artifact:

```text
Causes: C1, C2, ...
Effects: E1, E2, ...
Constraints: C1 requires C3; C2 and C4 are exclusive
Rules: E1 = C1 AND (C2 OR C3); E2 = NOT C5
Selected rows: R01, R03, R07
Oracle: observable result for each selected row
```
