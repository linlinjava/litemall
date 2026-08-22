# Project Agent Skill Entrypoints

Codex discovers this repository's testing Skills from `.agents/skills`. The
root `skills` path is a compatibility link to that canonical directory.

## Public testing Skills

| Intent | Skill |
| --- | --- |
| Complete testing lifecycle | `test-agent-workflow` |
| Layer-neutral Test Case Design | `test-case-design` |
| Independent Case Design approval | `test-case-quality-review` |
| Layer selection and execution planning | `test-strategy` |
| Test implementation, execution, and evidence | `test-execution` |
| KB, benchmark, Skill optimization, or CI governance | `test-agent-governance` |

Normal workflow order is requirement and AC preparation when needed, Test Case
Design, Quality Review, Test Strategy, Test Execution, classified repair when
needed, and final reporting. `test-agent-governance` is explicit-only and not
part of every feature run.

Internal stages and execution-layer instructions are references rather than
independently discoverable Skills. Test Strategy owns layer selection. Test
Execution resolves commands from `Tests/test-index.yml` and the project runner.
The workflow does not depend on Test Harness method or level routers.

The optional project custom agent `test_engineer` is defined in
`.codex/agents/test-engineer.toml`. Use it for independent coverage analysis,
Case review, or evidence reproduction when isolation is useful. Ordinary
workflow runs do not require a subagent.
