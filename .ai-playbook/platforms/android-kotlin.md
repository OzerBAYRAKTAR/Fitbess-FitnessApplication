# Android / Kotlin profile

Read the project context and relevant official sources before applying version-sensitive guidance.

## Toolchain and dependencies

- Inspect the Gradle wrapper, AGP, Kotlin, JDK, compile/target SDK and dependency versions together. Verify compatibility before upgrading; use the project wrapper.
- Keep machine-specific SDK/JDK paths out of shared configuration. Document prerequisites and the IDE wrapper selection when relevant.
- Prefer stable supported dependencies appropriate to the project. Do not upgrade unrelated libraries to satisfy a generic "latest" requirement.
- Do not add deprecated APIs in new code when a compatible supported alternative exists. Migrate existing APIs only within justified scope.

## UI and state

- For new UI, consider Jetpack Compose according to project context. Maintain existing XML/View screens without an automatic rewrite.
- Expose immutable UI state from a ViewModel; represent meaningful loading, content, empty and failure states. Keep database/network access behind a data boundary.
- Use unidirectional state flow. Keep navigation and transient user effects lifecycle-aware and prevent accidental replay of destructive actions.
- Collect Flow using lifecycle-aware APIs appropriate to Compose or Views. Clear Fragment view bindings at view destruction; do not retain views in ViewModels.
- Handle configuration change and process recreation according to persistence needs; a ViewModel alone does not survive process death.
- Include accessibility semantics, touch targets, content scaling, localization and relevant screen sizes in UI acceptance checks.

## Coroutines and data

- Use structured concurrency and lifecycle-appropriate scopes. Avoid GlobalScope and unmanaged work; use WorkManager for suitable durable background tasks.
- Keep blocking IO off the main thread. Make data APIs main-safe and inject dispatchers where deterministic testing or alternate execution is needed.
- Preserve cancellation; do not swallow CancellationException in generic error handling. Assess shared-state races and duplicate submissions.
- Expose read-only state, define a source of truth and handle cache/network synchronization deliberately.
- Test Room migrations for data-preserving changes. Do not introduce destructive migration as an incidental fix.
- Use explicit network timeouts and error mapping. Retry only when safe, bounded and justified; consider idempotency.

## Platform behavior

- Preserve the published applicationId unless the user requests a separate app. Package/namespace and branding are related decisions, not interchangeable identifiers.
- Review target SDK behavior changes, runtime permissions, notifications, background restrictions, edge-to-edge/insets and large-screen behavior when relevant.
- Minimize permissions; use secure transport and avoid logging tokens or sensitive payloads. Treat backup/exported-component configuration according to the data involved.
- Measure performance when there is a concrete concern; use release-like benchmarks for performance claims, not debug timing alone.
- Verify through the android-verification skill. Emulator checks do not establish all physical-device behavior or release readiness.
