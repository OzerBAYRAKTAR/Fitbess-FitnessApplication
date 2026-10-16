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

- Gradle wrapper: 8.11.1. AGP: 8.9.1. Kotlin declared in root build.gradle: 1.9.0; inspect resolved plugin versions before upgrades.
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
- Meaningful milestone: cohesive completed outcome with required checks and review complete. Runtime changes require the relevant emulator verification before automatic checkpoint.
- Commit convention: concise imperative English subject, unless the repository establishes another convention.
- Stage only owned task changes by explicit paths/hunks. Preserve pre-existing staged and unstaged changes. Generated APK/AAB files and local IDE configuration are excluded from routine checkpoints.
- Default/protected branch writes, merge, release and deployment: separate explicit authorization.
- CI: no GitHub Actions workflow was found at installation. Recheck on future tasks and report unavailable/pending checks accurately.
- Tool or environment approvals still apply; this setting does not bypass sandbox restrictions.

## Playbook provenance

- Version: 0.1.0.
- Source: `https://github.com/OzerBAYRAKTAR/ai-engineering-playbook`.
- Source branch: `main`.
- Pinned commit: `653d57d05071a26fa6451c17b8dc910b7757a150`.
- Installed/reviewed: 2026-10-16.
- Managed content: `.ai-playbook/core/`, `.ai-playbook/platforms/`, `.ai-playbook/workflows/`, `.ai-playbook/VERSION`, `.ai-playbook/SOURCES.md`, and the three directories in `.agents/skills/`.
- Project-owned files: root `AGENTS.md`, this file and `.ai-playbook/README.md`; preserve their local decisions during upgrades.
