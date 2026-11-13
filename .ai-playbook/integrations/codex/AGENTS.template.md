# Project instructions

Read `project-context.md`, `.ai-playbook/core/engineering.md`, `.ai-playbook/core/architecture.md` and `.ai-playbook/workflows/development.md` before implementation. For Android tasks, also read `.ai-playbook/platforms/android-kotlin.md`.

- Communicate outcomes and explanations in Turkish. Follow the English engineering instructions.
- Use android-verification for Android runtime/build changes, code-review before completion, and git-checkpoint for authorized verified milestones.
- Discover skills in `.agents/skills/`. If automatic discovery is unavailable, open the corresponding SKILL.md explicitly and report the integration limitation.
- Apply the shared engineering and development rules referenced above when relevant; the user does not need to name a skill for always-loaded project instructions to apply. Use task-specific skills as additional procedures when the agent discovers them.
- Use actual project commands and acceptance scenarios. Do not treat template examples or unknown context fields as executable facts.
- Automatic Git is enabled only by the completed project context or explicit session authorization. Respect tool/environment permission boundaries.
- When guidance conflicts, follow applicable higher-priority instructions and explicit user intent; surface unresolved project-specific contradictions.

When the consuming project explicitly enables the PO-manager workflow in project-context.md:
- Handle user product requests with the product-owner skill and delegate the scoped brief to one technical-manager subagent using available native delegation tools. This instruction requests delegation for that workflow.
- If you are already the delegated technical manager or reviewer, execute that assignment directly; do not create another PO or recursively delegate the same role.
- The technical manager may use one read-only reviewer for meaningful changes. Keep one implementation writer and serialize shared builds/emulator interactions.
- Respect analysis-only requests and actual tool permissions. Automatic Git follows the consuming project's authorization; no merge or release permission is implied.

When the consuming project selects the separate owner-orchestrator topology instead:
- On the user's designation of this chat as owner or orchestrator, read the corresponding `.agents/skills/owner/SKILL.md` or `.agents/skills/orchestrator/SKILL.md` and `.ai-playbook/workflows/owner-orchestrator.md`.
- Follow that workflow for authorized peer discovery, creation, messaging and delivery. The orchestrator is authorized to delegate scoped work to executor subagents and warranted read-only reviewers. Do not also start the PO-manager coordinator route.
- A role-only activation establishes coordination and waits for an objective; it does not authorize invented implementation work or broader execution permissions.
