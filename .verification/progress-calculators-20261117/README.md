# Progress and calculators verification — 2026-11-17

## Result and scope

Progress now separates completed workouts, actual active duration and completed movements; partial workout effort remains visible without counting as a completed workout. Seven local days and four consecutive seven-day periods use recorded Room sessions, with a common trustworthy-record filter for totals, program progress and history. Calculator cards precede long history. The existing BMI, daily calorie and body-fat destinations use consistent unit-labelled forms, inline validation and persistent on-page results. The four locales are TR, EN, DE and RU.

Existing formulas and DataStore keys are preserved; adult BMI classification uses raw CDC boundaries before display rounding. Inputs/results save atomically, stale results are hidden after edits, legacy sex labels restore across languages, and an unfinished initial read retries after view recreation without overwriting an edited draft. No dependency, Room schema or package identity change.

Research and adaptations: [design note](../../design/progress-calculators-20261117/README.md). Public competitor images in the local `research/` cache were inspected but are not included in the application or checkpoint.

## Final checks

- Repository wrapper: `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest :app:installDebug --console=plain` — passed (`final-build.log`). 47 unit tests, zero failures, including 12 new calculation/aggregation tests. This count includes existing working-tree tests from the separately pending engagement work; those unrelated files are excluded from this checkpoint.
- Final test-only capture/assertion update: `:app:assembleDebugAndroidTest` — passed (`final-test-apk.log`).
- Pixel_9, `emulator-5554`, API 36. Direct `adb install -r` of the test APK and `adb shell am instrument -w -r -e variant final-normal -e class com.bayraktar.healthybackandneck.ProgressCalculatorsDesignTest com.bayraktar.healthybackandneck.test/androidx.test.runner.AndroidJUnitRunner` — **OK (5 tests)**, no failures/skips (`final-runtime-normal.log`).
- Compact scenario: temporary 945×1680 pixels at density 420 (360×640dp), font scale 1.3; period/chart/hub and four-locale calculator-result methods — **OK (2 tests)**, no failures/skips (`final-runtime-compact.log`). Settings restored in `finally`; verified physical 1080×2424, density 420, font scale 1.0 afterward.
- Actual manifest SplashScreen cold launch: `Status: ok`. Debug application remains installed. First install time stayed 2026-11-13 21:04:03; no uninstall, wipe or app-data clear was used in this objective.

Application APK SHA-256: `408ECD056DC3B27064816512931419F9FF0ECD2033DF25D8F003479B5E48023E`.

Test APK SHA-256: `7D3803E032AE9FD2F3AC15AC20984459384985583EED09261D930784EA8D5F72`.

## Runtime and visual evidence

Tests cover empty data, complete plus partial sessions, exact 7/28-day totals, checked period, selected date range, four weekly buckets, calculator placement before history, invalid/overflow input, valid atomic saves, stale-result hiding, back navigation, activity/view recreation, delayed initial-read recreation, locale restoration and male body-fat input without unused hip. Activity/view state recreation is tested; no OS process-kill scenario is claimed.

Final images are `pc-final-normal-*.jpg` and `pc-final-compact-*.jpg`. Main evidence retained in this checkpoint:

- `pc-final-normal-progress-recorded-seven.jpg`: real rendered fixture totals 1 completed workout / 1 active minute / 3 movements, with daily bars.
- `pc-final-normal-progress-recorded-four-weeks.jpg` and `pc-final-normal-selected-range-detail.jpg`: checked four-week range and full selected start/end dates, duration on the next line. Exact first-line date assertion and no-ellipsis checks passed.
- `pc-final-normal-progress-calculator-hub.jpg`: three calculator cards followed by explicitly complete/partial history.
- `pc-final-normal-bmi-result-tr.jpg`, `pc-final-normal-calories-result-tr.jpg`, `pc-final-normal-fat-result-tr.jpg`: visible values, units and estimate interpretation.
- `pc-final-compact-selected-range-detail.jpg`, `pc-final-compact-bmi-result-tr.jpg`, `pc-final-compact-calories-result-de.jpg`, `pc-final-compact-fat-result-ru.jpg`: narrow viewport and enlarged text remain readable and scrollable.

Normal and compact result/hub captures were generated for all four locales. Earlier `pc-normal-*` and intermediate `pc-verified-normal-*` files are not final evidence: early captures preceded committed drawing/scrolling and the original synthetic LiveData fixture could be overwritten by Room. The final test uses an isolated in-memory Room repository before view creation, asserts persistent state, waits for committed drawing, scrolls using positive laid-out geometry, and verifies visibility. No production logic was weakened for screenshots.

## Data isolation and review

Progress fixture writes only to a separate in-memory Room database. Calculator tests snapshot the original values/absence of 12 relevant RATE_KEY measurement/result keys and restore only those keys atomically in `finally`. The language preference and default locale are also restored. No preference-wide clear is used. Baseline metadata and preserved first-install time are recorded; no claim is made that all live app files are byte-identical after normal SDK/application activity.

Independent read-only review found two P2 issues (inconsistent record filtering and unfinished initial read on recreation); both were fixed and independently re-reviewed. The owner independently reviewed the final small localized-label/date-detail patch and actual result/date images. The coordinator visually checked the final normal and compact evidence. No open actionable finding remains. The executor reached its usage limit near final verification; the coordinator completed the remaining focused checks sequentially without changing model/security settings.

Prior engagement/ad changes, existing version changes, source marketing deletions and other unrelated work are preserved and excluded from this checkpoint. No Play publication, purchase or real review submission occurred. No CI workflow is configured in this checkout; local verification is reported separately from CI.
