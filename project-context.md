# Fitbess project context

## Identity and architecture

- Purpose: Android fitness application with exercises, favorites, progress statistics, nutrition information and reminders.
- Platform/profile: Android/Kotlin; one `:app` module.
- Existing architecture: Fragments/Activities with XML layouts and ViewBinding; ViewModels, repositories and Room. No separate domain module is established.
- Dependency injection: Hilt. Navigation: AndroidX Navigation and Safe Args. Background work: WorkManager. Networking: Retrofit/OkHttp. Preferences: DataStore and existing legacy preferences.
- Preserve the existing architecture during scoped changes; introduce domain policies or additional modules only for concrete needs.
- Namespace and published application ID: `com.bayraktar.healthybackandneck`.
- App label: Fitbess. Debug variant: `debug`; debug package currently has no applicationIdSuffix.
- Launcher activity declared in the manifest: `com.bayraktar.healthybackandneck.ui.SplashScreen.SplashScreen`.
- Preserve Room data and the `fitness_db` database, persistence keys, navigation argument contracts and published app identity. Inspect actual database versions before migrations.

## Toolchain and commands

The following values describe the working checkout at installation on 2026-10-16, including existing uncommitted changes. Read build files again before use; these values are not an instruction to overwrite other revisions.

- Gradle wrapper: 8.11.1. AGP: 8.9.1. Kotlin: 2.0.10, matching the previously resolved Safe Args plugin classpath; inspect resolved plugin versions before upgrades.
- compileSdk/targetSdk: 36. minSdk: 24. Java/Kotlin bytecode target: 17.
- Use a compatible JDK (17 minimum for this AGP). The prior successful local wrapper invocation used Android Studio's bundled JBR 21. Configure JAVA_HOME/Gradle JDK locally; do not commit an absolute machine path.
- SDK: resolve from local.properties or Android SDK environment. In Android Studio use the Gradle wrapper.
- Windows PowerShell: use `.\gradlew.bat`. Unix-like shells: use `./gradlew`. Run commands from the repository root.

| Check | Windows command |
| --- | --- |
| Wrapper version | `.\gradlew.bat --version` |
| Debug build | `.\gradlew.bat :app:assembleDebug --console=plain` |
| Local unit tests | `.\gradlew.bat :app:testDebugUnitTest --console=plain` |
| Android lint | `.\gradlew.bat :app:lintDebug --console=plain` |
| Emulator instrumentation | `.\gradlew.bat :app:connectedDebugAndroidTest --console=plain` |
| Install debug | `.\gradlew.bat :app:installDebug --console=plain` |

- Unit test source: `app/src/test/`. Instrumented test source: `app/src/androidTest/`.
- At installation, existing example tests only check arithmetic and application context; they do not establish exercise, favorites, persistence or UI correctness. Add behavioral tests appropriate to future changes.
- Earlier in this session, debug build and existing unit tests passed. Deprecation/Kapt/Jetifier warnings were observed. Lint and runtime behavior have not been established as a clean baseline by this integration.
- Pre-existing application/build/IDE edits are present. Inspect git status at task start and keep them out of unrelated commits.

## Runtime acceptance

- Target: Android emulator, never automatically substitute a physical phone.
- An AVD named `Pixel_9` and serial `emulator-5554` were observed during setup; rediscover AVDs/devices and API level each session rather than assuming a stable serial or version.
- Check boot readiness, install the intended variant and launch the actual launcher. Target adb commands to the selected serial.
- Exercise the feature changed by the task and record expected/observed results. For a first-launch scenario, inspect onboarding state without clearing existing emulator data.
- Relevant flows include app launch/navigation, exercise detail, favorites and statistics; choose flows by scope. Use synthetic data and avoid real ad interactions, purchases or production writes.
- Do not claim visual or interaction verification unless performed. Documentation/instruction-only changes do not require an application launch.

## Git authorization

- Automatic commit/push: enabled by the project owner in this conversation, 2026-10-16.
- Authorized remote: `origin`, `https://github.com/OzerBAYRAKTAR/Fitbess-FitnessApplication.git`.
- Working branches: `codex/` plus a task-specific name. Default branch: `main`.
- Automatic checkpoint cadence: only substantial, cohesive completed milestones after required checks and review; do not commit/push every edit, small adjustment or intermediate step. Runtime changes require relevant emulator verification.
- Pull requests: do not create a PR automatically. Commit/push only the authorized working branch; a PR requires a separate explicit user request.
- Commit convention: concise imperative English subject, unless the repository establishes another convention.
- Stage only owned task changes by explicit paths/hunks. Preserve pre-existing staged and unstaged changes. Generated APK/AAB files and local IDE configuration are excluded from routine checkpoints.
- Default/protected branch writes, merge, release and deployment: separate explicit authorization.
- CI: no GitHub Actions workflow was found at installation. Recheck on future tasks and report unavailable/pending checks accurately.
- Tool or environment approvals still apply; this setting does not bypass sandbox restrictions.

## Playbook provenance

- Version: 0.4.0.
- Source: `https://github.com/OzerBAYRAKTAR/ai-engineering-playbook`.
- Source branch: `codex/po-manager-workflow` (upstream PR #2 awaiting merge at installation).
- Pinned commit: `58c2c108e4b15d1e0b7ce22745c519ac558fc597`.
- Installed/reviewed: 2026-11-13.
- Managed content and exact source provenance: `.ai-playbook-manifest.json`; seven project-local skills in `.agents/skills/`. Explicit preserved overrides remain project-owned.
- Project-owned files: root `AGENTS.md`, this file and `.ai-playbook/README.md`; preserve their local decisions during upgrades.


## PO-manager workflow

- Native PO-manager fallback was enabled on 2026-10-16. As of 2026-11-13 use it only when the user explicitly selects it instead of the separate owner-orchestrator topology.
- User-facing coordinator: product-owner skill in the main conversation; English execution briefs and Turkish explanations.
- Native delegation to one technical_manager subagent is authorized for scoped product implementation requests. A read-only reviewer may be used for meaningful changes. Keep one implementation writer and serialize builds/emulator use.
- Delegated managers/reviewers execute their assignment directly; do not spawn another PO or recursively delegate the same role.
- Automatic working-branch Git remains authorized as above. Product scope changes, merge/release, data deletion and security changes are not granted by a brief.
- Project .codex/config.toml requests on-request approvals with auto_review. Existing sandbox/profile and managed requirements remain authoritative. This installation does not prove the current chat's permission selection changed; verify Approve for me in the active client.
- Do not change global config, install allow-all hooks or enable Full Access for ordinary delegated work. The legacy PO-manager fallback does not create separate sidebar chats or a background service.
- Model selection is inherited from the user's current session.

## Separate owner-orchestrator topology

- Enabled by the user's integration request on 2026-11-13; this is the selected topology on explicit owner/orchestrator designation.
- The owner uses `.agents/skills/owner/SKILL.md` and the shared `.ai-playbook/workflows/owner-orchestrator.md`; the orchestrator uses `.agents/skills/orchestrator/SKILL.md`.
- The user's requested workflow authorizes finding/creating one orchestrator chat for this project, exchanging scoped coordination messages with the owner, and delegating scoped execution/review to subagents. Do not start a competing PO-manager coordinator for the same outcome.
- Activating a role alone establishes coordination and waits for an objective. This integration did not create or pair live chats; verify exact identities, source provenance and readiness when activating.
- Owner resolves in-scope product/architecture decisions. Existing Git cadence, external-action boundaries, active runtime permissions and current human direction remain authoritative.
- Synchronize through `.ai-playbook/scripts/sync_playbook.py`; the manifest owns source version/hash tracking. Preserve the five listed local overrides and project-specific instructions.
- No daemon, email bridge, global permission changes, publication or unlimited-runtime guarantee is installed.
