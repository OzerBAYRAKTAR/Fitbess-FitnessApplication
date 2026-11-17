# Native saved Favorites — verified 2026-11-18

The two approved references in `design/favorites-concepts-20261117` were implemented as native XML/Fragments with the existing forest/cream/lime palette, branded header, dynamic count, real exercise thumbnails, cards, heart controls and empty state. The subsequent explicit user decision supersedes every access badge/lock/open/unlock treatment in those references: **all already-saved items, including legacy locked favorites, offer only read-only Inspect**. No reward, offer, grant or workout-start action is introduced by this list.

## Scope and policy

- The actual bottom Favorites destination is `moveListFragment`, not the separate Home category catalog. The test enters through the real bottom navigation item and checks the selected destination.
- One RecyclerView contains header, saved rows and footer/empty content; long text and compact screens scroll without a fixed header shrinking the usable list.
- Count and rows come from Room, deduplicated by the existing exerciseId contract. No production fixture counts, filter or sorting feature. Unknown categories are omitted rather than guessed.
- Every row uses the actual stable-catalog-ID `ExerciseMedia` resolver, a48dp remove heart, and Inspect. Inspect opens only the existing GIF/description dialog. No access-status view or rewarded-ad host remains in the saved list. Catalog and workout access-policy Kotlin, entitlements, schema and old data were not changed.
- Existing GIF assets retain their baked `WELCOME TO FITBESS` watermark. Their pixels were not replaced or misrepresented as new artwork; the visible app UI uses Get Fit at Home.
- Removal still requires the existing Yes/No confirmation and updates all copies of the selected exerciseId. UI publishes removal only after the existing database operation/requery succeeds. One in-flight load is retained across recreation; removal is disabled/guarded while loading, and refresh cannot overlap removal. No general framework/dependency was added.
- `info_favourite_layout.xml` is shared by saved-item, DetailDay and category information dialogs. Its3IDs/GifImageView type are preserved; the new scroll wrapper and180dp fitCenter preview improve compact presentation for all callers. Their Kotlin access guards are unchanged. The saved-item behavior change is explicitly limited to read-only stored favorites.

## Checks

- First wrapper ran assembleDebug/testDebugUnitTest/lintDebug/assembleDebugAndroidTest. Unit XML results:47 tests,0 failures. Lint found the RU count-with-adjacent-label ImpliedQuantity issue. It was fixed without suppression: the complete formatted quantity is one plural TextView, with the real number styled large/lime and the noun phrase smaller/white; RU one/few/many remains native Android plural selection.
- `quantity-build.log`: `gradlew.bat :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest --console=plain` passed,1m44s. Business/ViewModel behavior was unchanged after the unit run.
- Initial `runtime-normal.log` failed at an incorrect Home Back test expectation. Existing HomePageFragment33–39 always opens an exit confirmation; it does not pop Favorites. Production navigation was not changed. The test now verifies that existing dialog, cancels it, then returns through the actual Favorites bottom tab. The original failure log is retained.
- Only test APK was rebuilt after correcting that expectation, reinstalling the isolated ViewModel factory callback after Activity recreation, and waiting for RecyclerView pending layout/adapter changes before capturing the top header. `test-layout-build.log`: assembleDebugAndroidTest passed,15s. No checks were weakened or skipped.
- `runtime-final-normal.log`:1/1 focused test passed,100.826s, executing all four locales.
- `runtime-final-compact.log`: the same app/test APK,1/1 passed,97.271s, executing the same TR/EN/DE/RU flow at945x1680,density420,font1.3 (360x640dp).
- Runner: `adb -s emulator-5554 shell am instrument -w -r -e class com.bayraktar.healthybackandneck.SavedFavoritesDesignTest -e variant final-normal com.bayraktar.healthybackandneck.test/androidx.test.runner.AndroidJUnitRunner`; compact variant is final-compact. Installation used install-r only, never connected/UTP/uninstall/clear/wipe.
- Pixel_9/API36. The compact command's finally block restored physical1080x2424,density420,font1.0 (`restored-settings.txt`). Previous nullable language preference and default Locale are restored by test finally. No production ads or purchase interactions were performed.
- App APK SHA256 `21AB560C5AFDE595F92399597128962A9EA81BEF7D741EFEF8208B2EE7E399E1`; test APK `3116342D44A502165E8A2F2CF5FBD9CFF994CB7BBF069127AA7137153AE0A3D5`. Installed base.apk hash matched after both runs. Intended debug package remains installed, pre-existing version15/1.8.3 and firstInstallTime2026-11-13 21:04:03 preserved.

The focused test covers empty→four stored rows/three distinct moves; real media; no access chrome; legacy Plank Extensions read-only preview without reward/offer; detail close and Back; unchanged persisted entitlement IDs; remove cancel; removal of both program/catalog duplicates; subsequent database query; Activity recreation; deleting remaining favorites; empty Home discovery; baseline Home exit-dialog cancel; actual Favorites bottom-tab return; all four localized presentations at normal and compact/large-font settings. Screenshots wait for asserted data, RecyclerView layout and a committed frame.

Fixtures and favorite writes are confined to a separate in-memory Room database through an isolated ViewModel factory, including future tab entries after Activity recreation. The existing fitness_db is not seeded with test favorites or used for test removals. Entitlement IDs are read before/after and asserted unchanged; no entitlement writes are made by this flow. No byte-identical database/preference claim is inferred from file metadata. Verification used the shared working tree, which includes unrelated prior engagement/version edits excluded from this milestone; APK hashes identify the tested artifacts rather than a clean Git checkout.

## Curated proof

- `proof-normal-populated-header-tr.jpg`
- `proof-normal-populated-rows-tr.jpg`
- `proof-normal-empty-card-tr.jpg`
- `proof-compact-populated-rows-ru.jpg`
- `proof-compact-empty-card-ru.jpg`
- `comparison-favorites-populated.jpg`
- `comparison-favorites-empty.jpg`

The7JPEGs plus this README are the proposed checkpoint evidence. Comparisons label the concept access chrome as superseded by the later instruction; native screenshots are separate real scroll positions. Additional four-locale PNG/JPEGs, read-only information dialog captures, initial/final logs, hashes, scripts and device metadata remain local. Header viewer caching was inconsistent across tools; unique JPEGs were decoded directly from final PNGs and independently checked. No product/test change was made to conceal a rendering issue.

## Owned files

`MoveListFragment.kt`, `MoveListAdapter.kt`, `MoveListViewModel.kt` under `ui/Favourite/FavouriteMoveList`; `fragment_move_list.xml`; new `item_favorites_header.xml`, `item_saved_favorite.xml`, `item_favorites_empty.xml`, `item_favorites_footer.xml`; shared `info_favourite_layout.xml`; `dialog_delete_favourite.xml`; new4locale `favorites_saved.xml`; new `fs_heart_outline.xml`, `fs_heart_filled.xml`, `fs_empty_art.xml`; `SavedFavoritesDesignTest.kt`.

No dependency/schema/package/version/entitlement/catalog guard changes. Prior engagement, ad XML/Kotlin, staged branding and unrelated marketing edits were preserved. Independent review is coordinated by the parent. Implementation agent performed no Git writes; coordinator owns the scoped checkpoint. Release/store/physical-device/CI checks are not claimed.
