# Development workflow

1. Establish the requested outcome and observable acceptance behavior. Inspect repository instructions, project context and relevant code. Use the smallest useful context set and expand exploration only when dependencies or evidence require it.
2. Choose a proportionate approach. For a clear small fix, proceed with a focused change. For ambiguous, broad or risky work, capture the decision, constraints, acceptance evidence and tradeoffs before editing. Keep plans short and actionable; do not require a separate design/approval ceremony for routine work.
3. Implement a cohesive change with meaningful tests. For a bug, show that the regression test fails before the fix when feasible.
4. Run relevant static/build/test checks. For Android runtime changes, use android-verification and observe the changed behavior on an emulator.
5. Use code-review on the final diff. Address actionable findings and rerun affected validation.
6. Use git-checkpoint only for a substantial, cohesive completed milestone when automatic Git is enabled. Group small edits and intermediate steps into that milestone; do not commit/push each change. Honor the project's checkpoint cadence and PR policy. Do not create a PR without explicit authorization.
7. Report the outcome and verification evidence in Turkish. State outstanding environment, runtime or CI gaps.

For multi-step tasks, give brief progress updates that identify the current finding and next action. Make the final report easy to scan, and expand it to match the user's requested depth. Keep evidence links and file references close to the claims they support.

Do not impose full runtime testing on documentation-only changes. Do not mark an application behavior task verified when required runtime checks are blocked. Continue useful safe work, then explain the exact blocker instead of fabricating success.

For long tasks, keep brief progress notes only when needed to preserve decisions and remaining work. Avoid mandatory planning artifacts for every edit.
