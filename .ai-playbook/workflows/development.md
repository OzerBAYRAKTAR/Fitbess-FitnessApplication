# Development workflow

1. Establish the requested outcome and observable acceptance behavior. Inspect repository instructions, project context and relevant code.
2. Choose a proportionate approach. A small fix may need only a short explanation; plan larger or risky changes before editing.
3. Implement a cohesive change with meaningful tests. For a bug, show that the regression test fails before the fix when feasible.
4. Run relevant static/build/test checks. For Android runtime changes, use android-verification and observe the changed behavior on an emulator.
5. Use code-review on the final diff. Address actionable findings and rerun affected validation.
6. Use git-checkpoint when a meaningful unit of work is complete and automatic Git is explicitly enabled in project context or authorized in the session.
7. Report the outcome and verification evidence in Turkish. State outstanding environment, runtime or CI gaps.

Do not impose full runtime testing on documentation-only changes. Do not mark an application behavior task verified when required runtime checks are blocked. Continue useful safe work, then explain the exact blocker instead of fabricating success.

For long tasks, keep brief progress notes only when needed to preserve decisions and remaining work. Avoid mandatory planning artifacts for every edit.
