---
name: git-checkpoint
description: Commit and push a cohesive verified milestone to an authorized working branch when automatic Git is enabled or the user requests a checkpoint.
---

# Git checkpoint

Use the project's Git settings and explicit user authorization. This skill alone does not grant remote-write permission.

1. Check repository root, status, current branch, remote URL, upstream and the initial work baseline. Confirm ownership of intended files/hunks and detect unexpected concurrent changes.
2. Require a coherent completed outcome, relevant passing checks and completed review. For runtime changes requiring emulator validation, do not create an automatic checkpoint while it is blocked. A user may separately request an explicitly labeled incomplete backup.
3. If automatic checkpoints are enabled for this project and target remote/branch are known, proceed without repeatedly asking. Otherwise obtain missing authorization or destination information.
4. Use the designated working branch. Create a codex/ branch when appropriate; do not switch a dirty checkout in a way that mixes unrelated work. Never commit directly on the default/protected branch through this workflow.
5. Inspect the exact diff and staged content. Stage only owned changes by explicit paths or hunks. Never use blanket staging when unrelated edits exist. Exclude secrets, signing keys, local config and generated build artifacts unless specifically required.
6. Create one descriptive commit for the cohesive outcome, using the project's convention or a concise imperative subject. Do not rewrite existing commits or alter the user's identity configuration.
7. Push the explicit working branch to the verified remote, setting upstream if needed. Do not force-push, bypass hooks/checks, merge or publish releases. On non-fast-forward, authentication or permission failure, preserve work and report; do not retry with destructive options.
8. Verify local commit and remote branch agree. If CI exists, inspect its result using available tools. Pending/unavailable CI must be reported as such; do not label the milestone fully green. Fix introduced CI failures within scope using a new commit.
9. Report branch, commit, remote link, verification and CI status in Turkish.

A meaningful milestone is a substantial, cohesive completed outcome. Group small edits and intermediate steps; do not automatically commit/push every change. Follow the project's configured cadence. Do not create a pull request unless the user explicitly requests one; checkpoint authorization covers the designated working branch only.
