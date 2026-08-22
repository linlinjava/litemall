# Test Agent Quality KB Builder

Load only for a testing knowledge-base build or refresh.

## Purpose

Build a deterministic project testing knowledge base from project artifacts, then let AI add semantic summaries only after the structural graph is valid.

This skill follows a hybrid pattern: static extraction owns facts, AI owns interpretation. The goal is a KB that teaches the team how business chains, tests, risks, skills, and evidence fit together.

## Required Context

Read these when present:

- Project `Tests/test-index.yml`
- `.agents/skills/test-agent-workflow/references/workflow-registry.yml`
- Project `Tests/README.md`
- `AI Native/workflows/*/workflow-pack.json`
- Project `Tests/ai-native-workflow/*/workflow-pack.md`
- Existing backend, frontend, contract, and E2E tests
- Product MD source paths listed in `test-index.yml`

## Workflow

1. Resolve the project-declared deterministic KB builder under
   `AI Native/scripts/` or the project testing directory; do not assume a
   generic filename exists.
2. Run that exact builder and inspect the output path it reports.
3. Check graph reviewer output:
   - no duplicate node IDs
   - no dangling edge references
   - each core chain has risk tags, source docs, and at least one test/evidence link
   - each Skill path in `workflow-registry.yml` exists
   - each workflow pack validates and contributes workflow/story/AC/test case/review/repair nodes
4. Add AI semantic notes only as `semantic_notes` fields or a separate Markdown summary; do not overwrite extracted facts.
5. Commit or share the KB when it is green so teammates and future AI loops start from the same map.

## Node Types

| Node type | Meaning |
| --- | --- |
| `chain` | Business chain such as payment callback, mobile order API, stock compensation, coupon release, groupon status, refund lifecycle, or browser smoke |
| `risk` | Project risk tag used by planner and quality review |
| `skill` | AI Native skill and owned quality surface |
| `test` | Concrete backend UT, frontend UT, contract, frontend IT, backend IT, or E2E asset |
| `test_case_design` | Layer-neutral business Case with stable `case_id` |
| `executable_test` | Strategy-created stable `test_case_id` for one Case and layer |
| `execution_item` | Run-specific `execution_id`, action, target, command, and result |
| `workflow` | Canonical AI Native feature workflow pack |
| `story` | User story extracted from product or workshop prompt |
| `acceptance_criterion` | AC linked to one story and one or more test case designs |
| `evidence` | Observable evidence expected from tests |
| `doc` | Source MD or architecture/testing documentation |

## Review Rules

- Treat the project `Tests/test-index.yml` as the test asset and command source of truth.
- Prefer exact file paths and commands over generated descriptions.
- Keep generated facts reproducible; if an AI inference is needed, mark it as an inference.
- Regenerate the KB after changing skills, tests, product docs, or `test-index.yml`.

## Output

Produce a short report:

```markdown
## Quality KB Build
- command:
- output:
- node_count:
- edge_count:
- reviewer_status: pass | needs-fix

## Reviewer Issues
| severity | issue | fix |
| --- | --- | --- |

## AI Semantic Additions
| node_id | note | confidence |
| --- | --- | --- |
```
