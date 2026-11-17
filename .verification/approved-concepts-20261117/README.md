# Approved concepts integration — verified 2026-11-17

The three approved references are in `design/progress-calculators-concepts-20261117`. The app uses native XML/Fragments, Material circular progress and vector/gradient accents. Design PNGs are not used as UI. The screenshots contain controlled test fixtures, not user measurements or fabricated production metrics.

## Final checks

- `frozen-build.log`: `gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain` passed, 3m49s. Unit XML reports: 47 tests, 0 failures, 0 errors, including pre-existing engagement tests.
- The subsequent change only corrected native wave drawable intrinsic size and rounded-corner clipping. `verified-build.log`: `gradlew.bat :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest --console=plain` passed, 1m24s. Business logic/formulas were unchanged after the unit run.
- Debug app and test APK were installed with `adb -s emulator-5554 install -r <apk>`. No connected-test/UTP task, uninstall, clear-data or wipe was used.
- `runtime-verified-normal.log`: direct instrumentation, 5/5 passed, 85.668s.
- `runtime-verified-compact.log`: the same app/test APK, 5/5 passed, 166.881s.
- Direct runner: `adb -s emulator-5554 shell am instrument -w -r -e class com.bayraktar.healthybackandneck.ProgressCalculatorsDesignTest -e variant approved-verified-normal com.bayraktar.healthybackandneck.test/androidx.test.runner.AndroidJUnitRunner`; compact uses variant `approved-verified-compact`.
- Pixel_9 emulator, API36. Normal: physical1080x2424, density420, font1.0. Compact: override945x1680 (360x640dp), density420, font1.3. `finally` restored no size override, density420 and font1.0; confirmed in `restored-settings.txt`.
- App SHA256: `AE18D9E26A3D3BB3D651916E8685BDBCAACCE2B10666327320D0BD42AD07DF7B`. Test APK: `056A26516DC7D3C743E1AAFA35D41484442ABE8F2CB039B9F69ABC45517C2276`. Installed base.apk hash matched the app hash after both runs (`final-device.txt`). The intended debug app remains installed and its real SplashScreen launcher was opened.
- Package firstInstallTime remains2026-11-13 21:04:03; version15/1.8.3 is the pre-existing local version change. Verification used the shared working tree including prior pending engagement/version edits; those unrelated edits are excluded from this outcome. This is not a claim that the APK came from a clean Git checkout.
- Final AndroidRuntime error buffer was empty. Independent source/visual review was reported clean by the coordinator. No release, physical-device, store or CI verification is claimed.

## Behaviors exercised

Empty progress; actual 7-day versus28-day aggregation; partial effort separate from completed workouts; selected chart range and full horizontal visibility of all7 columns; 3-level selector and true distinct-day ring; Progress→hub→BMI→returnHub→systemBack; selected Progress bottom navigation; Activity recreation; invalid/overflow input without persistence; atomic valid saves; stale-result invalidation on input/sex/activity edit; locale/legacy gender restoration; male optional hip and invalid logarithm guards; delayed initial preference read followed by saved-fragment recreation. All3 result screens and hub were captured in TR/EN/DE/RU at normal and compact/large-font settings. Screenshots wait for asserted state, layout, visible scroll target and committed draw frame.

The isolated in-memory Room fixture creates7-day daily minutes0/8/0/12/7/0/15, 3 completed workouts and24 actual moves (including a partial session). An older completion contributes the fourth distinct level1 day outside the7-day window. Therefore the real binding displays42/3/24 and4/21, matching the approved reference for comparison. No such numeric fixture exists in production code. The actual `fitness_db` is not written by this fixture.

Calculator tests use the existing RATE_KEY DataStore, not a UUID substitute. Before each preference test, the12 calculator keys are snapshotted; `finally` atomically restores original values or absence for height/weight/age/activity/gender/neck/belly/hip/BMI/category/calorie/fat. Previous nullable language preference and default Locale are also restored. No byte/hash identity claim is made for user databases/preferences; metadata alone would not prove that. No production ads, purchases or workout completions were exercised.

## Curated visual evidence

- `comparison-progress.jpg`, `comparison-hub.jpg`, `comparison-bmi.jpg`: approved reference beside native Android screenshots. Native screenshots are separate scroll positions, not a single flattened interactive screen.
- `pc-approved-verified-normal-progress-recorded-seven.jpg`
- `pc-approved-verified-normal-calculator-hub-tr.jpg`
- `pc-approved-verified-normal-bmi-result-tr.jpg`
- `pc-approved-verified-normal-activity-chart.jpg`
- `pc-approved-verified-normal-program-ring.jpg`
- `pc-approved-verified-compact-selected-range-detail.jpg`
- `pc-approved-verified-compact-bmi-result-de.jpg`

These10 JPEGs plus this README are the proposed curated evidence. All underlying PNGs, additional4-locale JPEGs, logs, baseline snapshots, build outputs and helper scripts remain local. Older `approved-normal` and `approved-final-normal` captures are superseded.

## Owned source scope

- `ui/Statistics/StatisticsFragment.kt`, new `BodyCalculatorsHubFragment.kt`, new `ActivityChartGrid.kt`.
- `ui/Statistics/DialogFragments/BodyCalculatorFragment.kt`: shared presentation, responsive fields/result metrics and explicit hub return only; formulas/storage unchanged.
- `ui/HomeActivity.kt`: ONLY the hub destination bottom-bar visibility condition and hub checked-Progress listener. Other pre-existing engagement hunks must not be staged. A pre-edit baseline copy is retained locally.
- `res/navigation/nav_pragh.xml`: dedicated hub destination, existing calculator destinations/actions retained; no new bottom tab.
- `res/layout/fragment_statistics.xml`, new `fragment_body_calculators_hub.xml`, `fragment_b_m_i.xml`, `fragment_daily_calorie.xml`, `fragment_body_fat_rate.xml`.
- New `res/values{,-en,-de,-ru}/approved_concepts.xml`.
- New `res/drawable/ac_hero.xml`, `ac_wave.xml`, `ac_pill.xml`, `ac_runner.xml`, `ac_tape.xml`, `ac_scale.xml`, `ac_weights.xml`, `ac_layers.xml`, `ac_icon_background.xml`.
- `androidTest/.../ProgressCalculatorsDesignTest.kt` and `design/approved-concepts-integration-20261117/README.md`.

No Gradle/dependency/schema/persistence-key/application-ID/formula/global-theme change. Existing pure data/calculation helpers, prior engagement changes, app/build.gradle, marketing and unrelated design work were preserved. Implementation executor made no Git writes; coordinator owns the scoped checkpoint.
