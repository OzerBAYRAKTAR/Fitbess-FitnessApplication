# RevenueCat setup for ad-free access

The app-side integration expects one Google Play non-consumable product and one active RevenueCat entitlement:

- Play product ID: `ad_free_lifetime`
- RevenueCat entitlement ID: `ad_free`
- Current RevenueCat Offering must include the `ad_free_lifetime` product.

The app does not grant access from a local preference. RevenueCat's active `ad_free` entitlement is the source of truth. This entitlement turns off banner, interstitial, and rewarded ads and bypasses rewarded movement/day gates.

## Configure RevenueCat and Google Play

1. In Play Console, create `ad_free_lifetime` as a one-time, non-consumable in-app product for package `com.bayraktar.healthybackandneck`. Set its localized title, description, and price.
2. In RevenueCat, add the Google Play app with the same package name. Import `ad_free_lifetime`, configure it as non-consumable, create the `ad_free` entitlement, attach the product to it, and add it to the Current Offering.
3. Connect Google Play to RevenueCat with a dedicated service account and grant the documented Play Console permissions. In the same Google Cloud project, grant the service account the `Pub/Sub Editor` and `Monitoring Viewer` roles. Upload the service-account JSON to RevenueCat's dashboard only; never store that private file in this repository. See [RevenueCat's Play service credentials guide](https://www.revenuecat.com/docs/service-credentials/creating-play-service-credentials).
4. Configure Google Play Real-time Developer Notifications (RTDN). Enable Cloud Pub/Sub in the Google Cloud project used for the RevenueCat service account. In RevenueCat's Google Play app settings, connect to Google and create/select a Pub/Sub topic, then copy its topic ID. In Play Console, open **Monetize > Monetization setup > Real-time developer notifications**, enter the topic ID, select **Subscriptions, voided purchases, and all one-time products**, and save. If Google Play's test notification cannot publish, grant `google-play-developer-notifications@system.gserviceaccount.com` the `Pub/Sub Publisher` role on that topic. Send a test notification from Play Console; then return to RevenueCat, enable RTDN for the app configuration, and confirm a recent **Last received** timestamp. This is especially important for this one-time product: it lets refunds and voided purchases update RevenueCat automatically; without RTDN they may not sync until the customer opens the app and purchases sync. Follow [RevenueCat's RTDN setup guide](https://www.revenuecat.com/docs/platform-resources/server-notifications/google-server-notifications) and [Google's RTDN setup documentation](https://developer.android.com/google/play/billing/getting-ready).
5. In RevenueCat's project settings, copy the Android **public app-specific SDK key** (the production Google Play key). Do not use a secret API key or a Test Store key.
6. Supply the public key at build time as a Gradle property or environment variable named `REVENUECAT_ANDROID_API_KEY`. For example, keep it in the developer's untracked Gradle user properties or CI secret variables. The app intentionally disables the purchase UI when the key or Offering is missing, and rejects Test Store keys.
7. Upload a signed build to a Play testing track, add license/test users, and install the build from the Play Store track. Test purchase, cancellation, restore/no-purchases, reinstall/restore, and every ad surface before release.

The current checkout has no RevenueCat public key or Play product configuration. Therefore the feature is implemented but store purchases cannot be exercised or considered ready for publication until these dashboard steps and Play test-track verification are complete. No subscription products are configured by this integration.
