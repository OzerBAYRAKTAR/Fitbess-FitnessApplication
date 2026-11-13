# Home refresh verification — 2026-11-13

Implemented cream/sage native XML home from selected 02-krem-yesil concept. Three photo cards route to existing level hub, region picker, warm-up and breathing. Native bottom navigation icons updated without destination/order changes. Compact widths (<360dp) or font scale >1.15 use wide text and vertically stacked wellness actions; normal design remains unchanged. Existing monetization/runtime policy and workout data preserved.

## Checks
- Final wrapper invocation `:app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --console=plain`: PASS, 3m8s (`build-adaptive.log`). Initial lint failures were caused by introducing a values-tr locale directory into this Turkish-default repository. Fixed by preserving Turkish in values/ and EN/DE/RU overrides and removing empty values-tr directory; no lint suppression/baseline added.
- Pixel_9 emulator, emulator-5554, normal1080x2424/420dpi/font1.0: HomeRefreshTest + FocusRegionDesignTest, 4 tests PASS,68.712s. Covers four home actions, four bottom tabs, four locales, region mappings/selection/recreation and original first-set data.
- PlayerLayoutMediaTest adapted to new regional entry:1test PASS,12.901s. Animated Crab Walk and fixed player controls assertions reached.
- Final compact840x1800/420dpi/font1.3: HomeRefreshTest2tests PASS,41.562s. Four locales plus navigation. Visually inspected final adaptive German bottom: text and buttons readable without broken-word columns.
- Normal TR/EN/DE/RU home and regional screenshots inspected. Root reviewed/accepted normal TR home. Screenshots in final-normal/ and adaptive/.
- Emulator size and font restored; locale restored by test finally block. No data wipe, purchase or real ad interaction.
- Independent read-only review: identified obsolete player-test home-grid assumption, fixed and runtime-tested. Final review including compact helper: no open actionable findings.
- No CI workflow present. Release signing/build/live ads/purchases/physical-device tests not in scope.

## Assets
Four existing generated photo backgrounds were recovered from prior interrupted task, resized to1200px JPEG and integrated in drawable-nodpi. No external service dependency. Native vector symbols and localized strings added.
Six store compositions generated with built-in imagegen using actual localized final UI captures: marketing/google-play/2026-11-13/home-region-refresh/{tr,en,de}/01-home.png and02-regions.png. 1080x1920 PNG,2.5–3.1MB each. All visually inspected. Prompt/source log in that folder's README. No Console upload.

Preserved unrelated app/build.gradle version15/1.8.3 edit and prior untracked design/marketing/verification work. No merge, PR or release.
