# API 36 ekran ve egzersiz geçişi doğrulaması

16 Ekim 2026. Mevcut XML/Fragment mimarisi, paket kimliği ve kullanıcı verileri korunur.

## Değişiklik

API 35+ cihazlarda üç Activity kökü sistem çubuğu/cutout inset'lerini bir kez uygular; aynı inset alt görünümlerde tekrar uygulanmaz, klavye inset bilgisi korunur. API 35 öncesinin pencere davranışı değiştirilmez. Android 16 edge-to-edge opt-out çözüm olarak kullanılmaz.

Ready, egzersiz ve dinlenmede timer/click aynı view ömrüne ait tek geçiş kapısını paylaşır. Geçiş mevcut destination ve RESUMED durumunda bir kez yapılır; sayaç önce iptal edilir. onPause/onDestroyView sayaçları iptal eder, odak dönüşü sayacı devam ettirir. Ready timer ve buton ilk hareketi (indeks 0) açar. Son hareket bitiş ekranına gider. Bitiş ekranının çıkış dialogu doğru navigation action'ını kullanır.

## Gözlenen kontroller

- Wrapper: `:app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --console=plain` başarılı; 1 yerel birim testi; lint 0 hata, 1160 mevcut uyarı.
- Pixel_9, emulator-5554, Android API 36: 11 instrumented test başarılı (6 egzersiz, 1 inset, önceki 3 reklam regresyonu ve 1 mevcut context testi).
- Gerçek Hilt Fragment/NavController ile yinelenen Next/Skip Rest, timer/click yarışında her iki kazanma sırası, ayrılmış view'ın eski callback'i, 7/7 sonrası bitiş, back sonrası callback, PAUSED→RESUMED ve Ready timer-first doğrulandı. Timer bitişi deterministik olarak tetiklendi; testler gerçek saniyelerce bekleme gerektirmez.
- Home, Ready, egzersiz, Rest, 7/7 ve End gerçek render edildi; ilgili kontrollerin sistem çubuğu sınırları test edildi, altı ekran görüntüsü görsel incelendi. Gerçek Splash launcher'dan cold launch sonrası Home ve alt menü ayrıca doğrulandı.
- Sentetik tamamlamalardan önce alınan emülatör uygulama DB/prefs/files snapshot'ındaki 9 dosya testlerden sonra byte-for-byte geri doğrulandı. Uygulama verileri temizlenmedi; debug test reklamları kullanıldı.
- Bağımsız salt okunur review PAUSED-only timer sorununu buldu; onPause/onResume düzeltmesi ve regresyon testinden sonra yeniden incelemede somut bulgu kalmadı.

## Sınırlar

Yalnız mevcut API 36 emülatörü vardı. Eski API, landscape/yatay cutout, üç tuşlu gezinme, onboarding'in görsel akışı ve gerçek klavye açılması çalıştırılmadı. Sentetik inset testi tekrar uygulama, yan inset ve IME bilgisini kapsar; bütün cihaz kombinasyonlarını kanıtlamaz. Mevcut yerel API36/AGP/Gradle/Kotlin yapılandırma değişiklikleri önceden vardı ve bu checkpoint'e dahil edilmez; temiz PR checkout'u ayrıca doğrulanmadı. CI sonucu ayrıca raporlanır.

## Resmî kaynaklar

- [Views edge-to-edge ve inset kullanımı](https://developer.android.com/develop/ui/views/layout/edge-to-edge)
- [Android 16 hedef SDK davranışları](https://developer.android.com/about/versions/16/behavior-changes-16)
