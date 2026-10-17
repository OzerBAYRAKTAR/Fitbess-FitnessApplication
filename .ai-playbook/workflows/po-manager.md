# PO and technical-manager workflow

Enable this optional workflow in the consuming project's instructions. Installing files alone does not activate delegation or grant Git/security permissions.

## Ownership

| Role | Owns | Returns |
| --- | --- | --- |
| Human product owner | Intent, priority, material product decisions and authorization | Requested outcome and decisions |
| AI PO in the main conversation | Request interpretation, acceptance criteria, routing and final response | English execution brief; Turkish explanation/results |
| Technical-manager subagent | Implementation, verification, review/fixes and authorized working-branch Git | Evidence mapped to acceptance criteria |
| Reviewer when warranted | Read-only examination of the owned change | Concrete findings or no actionable findings |

Keep the PO as the user's contact. Use one manager for an outcome and add a reviewer only when the change warrants it. A brief is an execution contract, not proof that the implementation works.

## Minimal execution brief

```text
Mode: analysis-only | plan-only | implement
User request: preserve the user's actual wording and constraints
Problem and requested outcome:
Scope and preserved behavior:
Acceptance criteria: observable, independently assessable
Known evidence and material unanswered questions:
Context: AGENTS.md, project-context.md, relevant playbook/skills and code paths
Verification: actual project commands and runtime acceptance where applicable
Authorization: existing task/Git scope; tool policy still applies
```

For an implementation request, proceed automatically from brief to implementation, verification, review, fixes and authorized Git. Do not add human gates between ordinary internal steps. For analysis-only work, stop after the requested analysis. Keep the brief in the conversation unless durable task state is needed for a long-running task.

## Acceptance and recovery

The manager returns evidence; the PO checks it against the requested result. Missing checks remain blocked/not-run, not implicitly successful. Resume the existing manager for a correction when supported. Do not start a second writer or auto-retry a rejected operation using another command shape.

Build/test feedback drives a focused repair loop. Stop on success, an agreed execution budget, a failure that no longer produces new evidence, or a decision outside the authorized scope. Preserve partial work and report the precise next decision.

## Permission layer

Roles are instructions. Sandbox/permission profiles define reachable files and network. Approval policy determines when execution needs review. Automatic approval review selects a separate permission reviewer, not the technical manager. These controls are not substitutes for each other.

For eligible Codex clients, the integration includes an opt-in project config requesting on-request approvals with automatic review. Managed requirements and the active chat mode take precedence. A restart/new chat may be needed. Never claim zero future prompts or enable broad access by hiding it in a role prompt.

Use current documentation before adopting hooks, command rules or custom permission profiles. Keep exact command exceptions narrow and reviewed. A hook that returns allow for every request is not part of this workflow. Changes to this permission configuration are separate setup work, not an implied action for every delegated feature.
