---
name: android-verification
description: Build and test Android changes and verify affected behavior on an Android emulator after runtime, resource, manifest, dependency or build changes.
---

# Android verification

Use the consuming project's context for actual commands, build variant, application ID, emulator and acceptance scenarios. Do not assume task names or APK paths.

1. Inspect existing tests and tooling. Select checks by the changed behavior: unit tests for policy/state, integration tests for data boundaries/migrations, UI tests for interaction. Include relevant lint/static analysis and compilation.
2. Run commands using the repository Gradle wrapper and compatible JDK. Record commands, exit outcomes and relevant reports. Fix introduced failures; distinguish unrelated baseline failures.
3. Discover available Android SDK tools and emulators. Prefer the context's emulator; identify a specific serial when several targets exist. Do not substitute a physical phone without user direction.
4. Start an existing configured emulator if needed and permitted; wait for boot readiness. Never wipe data, alter another session's device or install large system images without appropriate authorization.
5. Build/install the intended variant, launch its actual launcher activity and exercise the changed flow. Use available instrumentation/UI automation to interact and inspect results. A launch command or absence of crash logs alone does not prove the flow.
6. Check relevant failure/empty/permission scenarios and runtime errors. UI changes require visual inspection when tools permit. Record emulator/API, steps, expected/observed behavior and any test reports or screenshots that materially support the result.
7. If tools cannot interact with the emulator, report the missing capability. Supply the exact manual verification steps; do not claim they were executed. Required runtime verification remains blocked until evidence exists.
8. After fixes, rerun affected checks. Report passed/failed/blocked/not-run separately. Avoid unrelated suite expansion.

This skill verifies debug/development behavior. Release signing, store publication, purchases, real messages and production-data changes require separately authorized scope.
