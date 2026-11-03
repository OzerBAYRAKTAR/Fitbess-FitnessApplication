# Evde Fit discovery redesign — verification record

Date: 2026-11-03
Branch: codex/exercise-runtime-ads-stability
Implementation owner: delegated technical manager; separate read-only review completed and both findings fixed.

## Delivered

- Home: generated local photo hero, truthful session-derived continuation target, three compact level actions, six body-region tiles, profile and existing warm-up/nutrition/breathing routes.
- Continue opens the existing level/day list with focusDay; it does not bypass the locked-day gate. Empty history starts at beginner day 1; partial sessions remain on their day; complete sessions advance one day, capped at 21. Existing WorkoutSession.isComplete semantics are used.
- Body-region discovery: selectable anatomical regions, highlighted silhouette, actual existing category/level workouts. Arms is labeled accurately; no invented shoulder program. Selection survives detail/back and recreation. Narrow/large-font layouts stack safely.
- Day detail: actual program/category heading, day, movement count, minutes and explicitly estimated calories; original movement graphics, access states and CTA/navigation preserved.
- Full bundled subexercise catalog is seeded on IO-backed repository reads with INSERT IGNORE, preserving favorites/completions. Capitalized ExerciseName in an existing asset row is supported.
- New interface copy supplied for Turkish, English, German and Russian. Compact level labels retain full accessible names.

## Evidence

Toolchain: repository Gradle 8.11.1 wrapper; Android Studio JBR; GRADLE_USER_HOME=C:/Users/BAYRAKTAR/.gradle.
A narrowly approved escalation was needed for the user Gradle cache lock and ADB startup. No security configuration was changed.

- assembleDebug / assembleDebugAndroidTest: PASS.
- testDebugUnitTest: 27 tests, 0 failures (includes 7 continuation-policy tests).
- lintDebug: 0 errors, 1227 warnings across the existing project. This is not a warning-free baseline. The last change after lint was compact localized labels and screenshot settling only.
- Pixel_9, emulator-5554, Android 16/API 36, 1080x2424, density 420: debug install -r succeeded without explicit uninstall/data clear.
- Four targeted Android tests: PASS. Covers home/program/day count; region/detail/back/recreation; all catalog categories with preserved favorite/completion; start workout without interstitial wait.
- Final UI regression after short labels: see final-ui-instrumentation.txt.
- Four-language font scale 1.3 verification: see large-font-instrumentation.txt. Original locale is restored in test finally; font_scale restored to 1.0 by shell finally.
- Independent review found and resolved missing favorites catalog initialization and region selection reset. No further blocking findings in recheck.
- git diff --check: no whitespace errors.

Screenshots are in discovery-verification/: home-tr.png, body-tr.png, day-tr.png; normal and large-* variants exist for tr/en/de/ru. These are actual emulator captures, not mockups.

APK: app/build/outputs/apk/debug/app-debug.apk (debug only; not a release/AAB).
Unit report: app/build/reports/tests/testDebugUnitTest/index.html.
Lint report: app/build/reports/lint-results-debug.html.

## Owned source paths

New: ui/HomePage/{TrainingCatalog,TrainingDiscoveryViewModel,RegionTileViews,BodyRegionFragment}.kt; data/Repository/SubExerciseCatalog.kt; layouts fragment_body_region, item_region_tile, item_region_workout; drawable discovery_*, region_*, body_map_*; drawable-nodpi/home_training_hero.png (parent-generated); values[-en,-de,-ru]/discovery.xml; values/discovery_styles.xml; tests ProgramResumeTargetTest, TrainingDiscoveryNavigationTest, SubExerciseCatalogTest.

Modified: ui/HomePage/HomePageFragment.kt; layouts fragment_home_page, fragment_detail_day, item_detailday; DetailDayFragment heading binding; DetailDayAdapter row numbering; nav_pragh.xml body destination/action; RoomRepository catalog read guard/constructor; MovesDao missing-only insertion; AppModule catalog injection; AdConfigurationTest scroll-root lookup updated to new layout.

## Git and limits

No commit/push/PR/merge/release. The working tree already contained substantial earlier runtime, binding, navigation and parser changes. This redesign depends on those uncommitted prerequisites; staging only the new task would not yield an independently verified build. All earlier work and local Gradle caches were preserved.

Existing GIF guidance is intentionally retained. The generated hero is decorative, not a replacement movement demonstration. No server/service/database migration or paid dependency was introduced. No real ad completion or purchase was performed in this verification.

## Final handoff result

Final short-label APK packaged successfully (last incremental build 7s). Final UI instrumentation passed 2/2 in 38.643s; font-scale 1.3 four-language pass completed 1/1 in 26.669s, restored font_scale=1.0. Actual launcher cold-start succeeded and reached the existing welcome/onboarding screen; UI hierarchy recorded in launcher-window.xml. No application fatal exception appeared in the inspected crash buffer. Screenshot settling was added to avoid capturing outgoing navigation frames.

Visual checks: final Turkish Home/body/day, German normal/large Home, Russian large body fallback, and German large day detail inspected. At 1.3 font scale the long German advanced label uses two lines without clipping; body discovery switches to stacked layout with scrollable workouts. No further implementation changes followed these checks.
