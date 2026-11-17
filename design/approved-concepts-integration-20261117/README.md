# Approved concept integration

The three user-approved images in `design/progress-calculators-concepts-20261117` are visual references, not production UI assets. The implementation remains native XML/Fragments with existing Room/DataStore bindings. Example badges/numbers are not production content.

- Progress places active minutes first, completed workouts/moves second. The existing trusted-session 7/28-day aggregation remains unchanged. The selected bar, numeric axis/grid and full date detail use one scale. Program ring has an explicit 1–3 level selector; no persisted active-program state is invented. All three levels remain accessible.
- A dedicated calculator hub is reachable from Progress without a new bottom tab. Progress remains selected; system Back and form return preserve the navigation stack. Existing direct calculator entry remains valid.
- The three shared forms retain IDs, validation, formulas, localized legacy sex aliases, atomic preference saves and restoration semantics. BMI puts fields side by side at normal width and stacks below 360dp or at font scale 1.2+. Results use the approved forest/lime treatment and explicit return to hub.
- Native gradient/vector wave accents and Material circular progress are used. No screenshot or flattened design PNG is used as an interactive screen.
- Test-only in-memory Room records may reproduce the approved 42 minutes / 3 completed workouts / 24 actual moves and four distinct level-1 days. These values exist only in the instrumentation fixture; the app's real database is not written by that fixture.

Prior engagement, version and unrelated design/marketing changes are outside this outcome. HomeActivity's pre-edit snapshot is retained locally so only hub navigation hunks can be checkpointed by the coordinator.
