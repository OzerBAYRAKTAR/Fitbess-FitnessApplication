# 1.8.1 (13) — R8 release verification

Verified 2026-11-05. Release minification and resource shrinking are enabled.

## Artifact measurements

| Metric | Previous local release AAB | Optimized release AAB |
| --- | ---: | ---: |
| Uncompressed DEX | 34,089,164 bytes | 7,223,728 bytes |
| DEX reduction | — | 78.81% |

The final bundle contains `BUNDLE-METADATA/com.android.tools/r8.json` and the
obfuscation mapping. R8 8.9.32 reports `noObfuscationPercentage: 7.68`
(92.32% eligible obfuscation), `noOptimizationPercentage: 8.16`, and
`noShrinkingPercentage: 7.85`. Google Play's final assessment remains authoritative.

## Compatibility protections

- Preserve the persisted Gson `ExerciseDay` field names and Navigation argument class names.
- Preserve 220 catalog resource names loaded dynamically by the JSON parsers.
- Prevent Hilt 2.51.1 ViewModel class merging while permitting name obfuscation.
  A candidate build exposed a duplicate ViewModel map key; the final build fixes it.

## Verification

`gradlew.bat :app:bundleRelease :app:assembleRelease :app:testReleaseUnitTest :app:lintRelease --console=plain`

- Final build passed; 28 release unit tests passed, including legacy persisted JSON round-trip.
- Lint: 0 errors, 1003 warnings; this is not a warning-free baseline.
- API 36 emulator: optimized release APK installed in place using a local debug-signed copy.
  No data clearing or production signing changes.
- Home, all three program day lists/details, exercise GIF/timer/rest/pause/instructions,
  partial completion and return to the originating list passed.
- Existing resume state survived the update. Regional list, level unlock dialog and
  offline purchase-unavailable screen passed. No new crash-buffer entries after final install.
- Live ads, purchases and every catalog movement were not exhaustively tested.
  Network was disabled for safe checks and restored afterward.

Local logs/screenshots/metadata are under `app/build/reports/r8-verification/`.

## Publishing handoff

The generated `app/build/outputs/bundle/release/app-release.aab` is **unsigned**.
It is not ready for Play upload. In Android Studio use **Build → Generate Signed
App Bundle / APK → Android App Bundle**, select the existing upload keystore and
release variant, and generate version **1.8.1 / code 13**. Do not create a replacement key.
Upload that signed AAB as the next release. This task did not submit or publish a release.
Keep the matching mapping at `app/build/outputs/mapping/release/mapping.txt`;
the bundle also embeds its mapping metadata.
