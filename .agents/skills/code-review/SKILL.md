---
name: code-review
description: Review a completed code change or requested diff for concrete correctness, regression, architectural and verification issues before handoff or a Git checkpoint.
---

# Code review

Read applicable project rules, acceptance criteria and the actual diff, including new files. Inspect surrounding implementation and callers where needed.

- Review correctness and requirement fit first, then compatibility/data safety, lifecycle/concurrency, security/privacy and error handling.
- Assess architecture and maintenance cost: unnecessary layers, redundant abstractions, scope creep, new dependencies and unrelated churn.
- Review tests as code. Ask whether they would fail on the broken behavior; identify significant missing scenarios and distinguish actual runtime evidence from build success.
- Verify suspected findings against code, dependency versions or a focused reproduction. Avoid invented APIs, speculative failure chains and stylistic findings framed as bugs.
- For each actionable finding, provide severity, precise location, trigger, user/developer impact and a concise correction direction. Separate optional suggestions.
- Fix findings within authorized scope; rerun affected validation and review the resulting diff. Stop when findings are resolved or a specific unresolved issue needs user input.
- Report review scope and remaining risks. Say when no actionable findings were found; do not invent a quota.
- A self-review is not an independent review. Use an independent reviewer only when available and authorized; never imply one ran when it did not.

Report in Turkish. Do not approve merge or publication merely because an AI review found no issues.
