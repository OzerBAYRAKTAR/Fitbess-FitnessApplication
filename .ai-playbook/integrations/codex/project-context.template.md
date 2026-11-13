# Project context

Complete this file from repository evidence and user decisions before enabling automatic Git.
Unknown values remain unknown; never invent commands, IDs or authorization.

## Identity and architecture

- Project and purpose: unknown
- Platform/profile: Android/Kotlin
- Architecture and dependency direction: unknown
- Existing UI/DI/persistence choices: unknown
- Application ID and variant: unknown
- Contracts/data that must be preserved: unknown

## Toolchain and checks

- Required JDK / AGP / Kotlin / Gradle / SDK: inspect project files
- Build command: unknown
- Static analysis command: unknown
- Unit/integration test commands: unknown
- Instrumented/UI test commands: unknown
- Known baseline failures and evidence: none recorded
- Runtime target: Android emulator
- Emulator/AVD selection: discover existing compatible AVD
- Acceptance flows and test data: define per task
- External effects requiring separate authorization: production writes, purchases, publication

## Git authorization

- Automatic commit/push: disabled until explicitly enabled by the project owner
- Authorized remote URL: unknown
- Working branch: codex/ followed by a task-specific name
- Meaningful milestone: cohesive outcome with required verification and review complete
- Commit convention: concise imperative English subject unless repository convention differs
- Default/protected branch writes: not authorized
- Merge/release/deploy: separate explicit authorization
- CI checks and inspection method: discover repository configuration

## Playbook

- Version: record the installed manifest version
- Source repository URL and exact commit: record on installation
- Last reviewed date: record on installation

## Optional PO-manager workflow

- Enabled: false until requested by the project owner
- User-facing coordinator: product-owner skill in the main conversation
- Implementation: one technical_manager subagent; one read-only reviewer when warranted
- Delegation authorization: record the project owner's decision before enabling
- Permissions: inherit the active session; opt-in automatic approval review is separate setup
- Model: inherit the user's current selection unless explicitly configured otherwise
- No standalone sidebar-chat messaging, background service, merge or release authorization is implied

## Optional separate owner-orchestrator topology

- Enabled: false until requested by the project owner; select instead of PO-manager for the same outcome
- Coordination authorization: record the user's request to find/create the orchestrator chat and exchange messages for this project
- Roles: owner plans and resolves in-scope decisions; orchestrator delegates execution and warranted review
- Active peer identities: keep verified chat/host IDs in chat context, not in this committed template
- Actual task, Git and external-action authorization: preserve explicit current user/project grants; role names do not expand them
