# Fitbess project instructions

Read `project-context.md`, `.ai-playbook/core/engineering.md`, `.ai-playbook/core/architecture.md` and `.ai-playbook/workflows/development.md` before implementation. For Android tasks, also read `.ai-playbook/platforms/android-kotlin.md`.

- Communicate explanations and outcomes in Turkish. Follow the English engineering instructions.
- Use `.agents/skills/android-verification/SKILL.md` for Android runtime/build changes, `.agents/skills/code-review/SKILL.md` before completion, and `.agents/skills/git-checkpoint/SKILL.md` for authorized verified milestones.
- Skills live in `.agents/skills/`. If the current session does not discover them automatically, read the relevant SKILL.md explicitly. A fresh session may be needed for discovery.
- Use the repository Gradle wrapper and the commands in `project-context.md`. Never infer successful runtime verification from a build.
- Preserve the existing XML/Fragment application and published package identity. Broad architecture or Compose migrations require task scope that justifies them.
- Automatic commit/push to a `codex/` working branch is authorized for this project under `project-context.md`. Do not include unrelated user changes or write directly to the default branch.
- Do not modify signing files, release artifacts, user-specific IDE files or local SDK paths as part of routine implementation.
- For playbook updates, follow `.ai-playbook/README.md`; preserve project-specific context and record the exact upstream commit.
- Follow higher-priority instructions and explicit user intent when guidance conflicts; surface unresolved project-specific contradictions.
