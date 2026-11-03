# Program day isolation — 2026-11-03

## Scope and result

Fixed `exercise_days` identity from `dayId` to `(level, dayId)`. All three bundled
catalogs contain days 1–21; the previous REPLACE insertion deleted another level's
same-numbered day. Database version 13 migrates existing version-12 rows without
clearing user data; version 11 still follows the existing 11→12 migration first.
Day insertion now runs in a transaction and merges metadata without reverting an
already-true access flag. Existing earned-access DataStore keys are unchanged.

## Executed verification

- `:app:assembleDebug`: passed.
- `:app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug`: passed
  after correcting a test-only Room Closeable API mismatch. 27 unit tests, zero
  failures; lint zero errors (existing warnings remain).
- Pixel_9 / emulator-5554 / API 36, APK installed with `adb install -r`; no data wipe.
- `ProgramDayIsolationTest`: 3/3 passed. Reproduced old-schema cross-level deletion;
  asserted 63 distinct days, independent level access, advanced days 6/7 staying
  unlocked across repeated catalog writes and database reopen; verified actual
  Room 12→13 upgrade/schema validation and a preserved workout-session row.
- `WorkoutFlowDesignTest#levelWeeksAndDayData`: 1/1 passed on the installed app:
  opened each level, checked 21 days, switched weeks, opened day 1 and returned.
- Scoped `git diff --check`: passed.
- Self-review: checked migration registration, table columns, DAO callers and
  level-filtered reads/writes. No additional actionable finding in this scope.
  This was not an independent review.

## Limits / handoff

Previously overwritten rows cannot be reconstructed by this migration alone.
Only records still present are guaranteed to be preserved. Real rewarded ads,
purchases and the remaining release acceptance flows are deferred to the user's
internal testing as requested. No release AAB/signing/store publication performed.
Changes remain in the working tree alongside the earlier UI/catalog work; no
partial Git checkpoint was made because the verification test uses the existing
uncommitted catalog parser and the checkout contains mixed prior work.
