# Managing Android app updates

Fitbess uses Google Play in-app updates. For routine releases, the app first shows its own localized, dismissible “A newer version is available” prompt. Choosing **Update** opens Google Play's confirmation UI for a flexible update; the user can then keep using the app while it downloads and install it when ready. Choosing **Later** hides that prompt for the offered version. A remotely configured minimum version switches eligible older installations to Play's immediate flow.

## Set the minimum supported version

In Firebase Console, open the Firebase project linked to Fitbess, then go to **Run > Remote Config** (the menu may appear as **Engage > Remote Config**), add the numeric parameter `minimum_supported_version_code`, and publish it.

- Set it to `0` to turn forced updates off. Available updates remain flexible and dismissible.
- To require an update, set it to the Android `versionCode` of the release that users must reach. For example, if the required Play release has version code `9`, set the parameter to `9`. An older installation enters the immediate flow only when Google Play actually offers version 9 or newer and allows that flow.
- Set it back to `0` to return to flexible updates.

The app checks Remote Config when Play reports an update. It uses the locally cached/default value if the fetch fails. The production fetch interval is one hour, so a published change can take up to about an hour to reach an installation that fetched recently. If Play does not offer a matching update, or Play/Remote Config cannot be reached, the app stays usable and does not impose an update lock.

**Important:** versions published before the in-app update manager was added cannot be forced by this setting. First publish a version containing this manager and let users install it. After that, a later release's version code can be used as the minimum.

## Test on Google Play internal testing

The in-app update API requires an installation served by Google Play. A locally installed debug APK or Internal App Sharing build is not a valid test of the Play update flow.

1. Publish the first manager-enabled build to the internal testing track (the next app version code must be greater than the currently published code). Install it from its Google Play tester opt-in link on a test device and open it once.
2. Upload a newer version to the same internal track. Keep both builds signed and distributed through the same Play app/track. Wait until Play makes the new version available to that tester.
3. For the flexible case, publish `minimum_supported_version_code = 0`. Start the older Play-installed build. Verify the localized app prompt appears, **Later** dismisses it, and reopening does not repeat the prompt for that same offered version. On a separate old installation, choose **Update**, accept Play's confirmation UI, then verify the app remains usable during download and offers **Install now** after download.
4. For the immediate case, set the parameter to the newer track release's version code. Start an older Play-installed build for which that release is available. Verify the full-screen Play update starts; cancel once to verify the retry/exit prompt, then retry and complete it.
5. Restore `minimum_supported_version_code = 0` after the test. Remote Config settings apply to every installation using this Firebase app unless a Remote Config condition is used to limit the parameter to a test group.

Use separate test devices/installations for the flexible and immediate scenarios where possible: once an installation updates, it can no longer reproduce the old-version case. A debug sideload is still useful for checking that the app continues to work when Play update information is unavailable, but it does not validate the update UX.
