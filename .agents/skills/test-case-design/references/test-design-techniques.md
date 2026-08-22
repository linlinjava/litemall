# Test Design Techniques

Load only the technique that materially shapes business case selection. These
techniques belong to Test Case Design; evidence-layer selection belongs to
Test Strategy.

## Selector

| Obligation shape | Technique | Required artifact |
| --- | --- | --- |
| one explicit behavior or known defect | direct example or regression reproduction | source-to-case link |
| many values treated alike | equivalence classes | valid/invalid partition table |
| limits, thresholds, dates, lengths, ranges | boundary values | ordered boundary set |
| rules combine conditions | decision table | normalized condition/action table |
| combinations are too large | pairwise | parameter/value model and generated rows |
| behavior depends on state and event order | state transition | state/event/next-state table |
| interacting numeric ranges | domain analysis | domain matrix and boundaries |
| an actor completes a goal | use case | main and alternate flow cases |
| branch-heavy implementation must be audited | control flow | branch obligations linked to business sources |
| value lifecycle or cleanup matters | data flow | definition/use/cleanup obligations |
| requirements are weak or unknown risks dominate | exploratory charter | mission, scope, heuristics, and stop condition |

## Equivalence Classes

Partition inputs only when the system is expected to treat all members of a
partition equivalently. Include at least one representative per valid and
invalid class and split a class when rules or outcomes differ.

```markdown
| partition | rule | representative | expected_result |
| --- | --- | --- | --- |
```

## Boundary Values

For each ordered boundary `b`, consider the smallest meaningful set among
`b-1`, `b`, and `b+1`, adjusted for the domain. Include lower and upper bounds,
empty/zero when meaningful, and overflow or precision boundaries when relevant.

```markdown
| boundary | below | at | above | expected_results |
| --- | --- | --- | --- | --- |
```

## Decision Tables

Normalize conditions before generating rows. Mark impossible combinations and
constraints explicitly; do not create arbitrary Cartesian products.

```markdown
| rule | condition_1 | condition_2 | action | expected_result |
| --- | --- | --- | --- | --- |
```

Each distinct feasible action/outcome row must map to a Case or documented
equivalence.

## Pairwise

Use pairwise only after defining parameters, values, exclusions, and seeds.
Preserve mandatory business combinations even when a generator would omit
them. Save the model and generated rows as the technique artifact.

```markdown
| parameter | values | constraints |
| --- | --- | --- |
```

## State Transition

List states, valid events, guards, next states, and invalid transitions. Cover
required valid paths plus high-risk invalid, duplicate, retry, and terminal
state transitions.

```markdown
| current_state | event | guard | next_state | observable_result |
| --- | --- | --- | --- | --- |
```

## Domain Analysis

Use when multiple numeric dimensions jointly define valid and invalid regions.
Identify on-points and adjacent off-points without exploding all combinations.

```markdown
| domain | dimensions | on_point | off_points | expected_result |
| --- | --- | --- | --- | --- |
```

## Use Case

Derive cases from the actor's main flow, alternate flows, exceptions,
preconditions, and postconditions. Keep implementation navigation out unless
it is part of the approved user-visible behavior.

## Control and Data Flow

Use white-box evidence only when the source or risk requires structural
coverage. Trace every structural obligation back to an approved business rule,
contract, or risk; implementation structure alone must not invent behavior.

## Exploratory Charter

Use exploration to discover questions and candidate cases, not as automatic
passing evidence.

```markdown
- mission:
- scope:
- risks_and_heuristics:
- data_or_tools:
- timebox_or_stop_condition:
- observations_to_capture:
```

## Technique Rules

- Start from coverage obligations, not a method checklist.
- Normally use no more than two formal techniques for one focused change.
- A technique name without its artifact does not justify the generated cases.
- Techniques generate layer-neutral business cases; do not add framework,
  command, runtime, target file, or evidence-layer choices here.
