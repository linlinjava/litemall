# Acceptance Criteria

Read this stage only when the approved requirement lacks observable acceptance
criteria and business rules.

## Purpose

Define observable business behavior without prescribing test layers,
frameworks, files, or commands.

## Required Context

- Story Card or authoritative requirement
- Product rules and contracts cited by the requirement
- Relevant source only when needed to confirm an existing public behavior

## Workflow

1. Write the primary success behavior.
2. Add applicable negative, boundary, permission, duplicate/retry, state,
   concurrency, and failure behavior.
3. State each criterion as Given/When/Then business behavior.
4. Define an observable oracle such as UI state, API response, persisted state,
   event, payload, or log. Do not select the future evidence layer.
5. Put ambiguous or conflicting behavior in Open Decisions rather than turning
   an assumption into an AC.

## Output

```markdown
## Acceptance Criteria
| ac_id | story_id | type | given | when | then | observable_oracle | source |
| --- | --- | --- | --- | --- | --- | --- | --- |

## Business Rules
| rule_id | condition | expected_result | risk_tag | source |
| --- | --- | --- | --- | --- |

## Open Decisions
| question | impact | owner |
| --- | --- | --- |

## Handoff
- next_skill: test-case-design
```

Stop for product clarification when an unresolved rule changes expected
behavior or makes the oracle contradictory.
