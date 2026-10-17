# Reklam ve favori gezinmesi doğrulaması

## Sorun ve uygulanan davranış

Geçiş reklamları tıklama sonrasında yükleniyor, yükleme callback'inde gösteriliyor ve bazı hata yolları kullanıcıyı devam ettirmiyordu. Artık ekran açılırken ön yükleme yapılır. Kullanıcı eyleminde reklam hazırsa gösterilir; hazır değilse gezinme hemen devam eder. Daha sonra tamamlanan yükleme kendiliğinden reklam göstermez. Kapanma/gösterim hatası devam işlemini yalnızca bir kez çalıştırır.

SDK uygulama başlangıcında bir kez arka planda başlatılır. Reklam yükleme istekleri SDK'nın hazır olmasını bekler; gezinme bu sinyali beklemez. Banner'lar mevcut konumlarını korur ve Fragment görünümünün yaşam döngüsünde duraklatılır/temizlenir. Debug varyantı Google test reklam birimlerini, release mevcut yapılandırılmış reklam birimlerini kullanır. Ödüllü reklamlarda kilit açma yalnızca ödül callback'inde yapılır.

Alt menünün ikinci öğesi mevcut favori hareket listesine açılır. Food kaynakları ve navigation graph korunur. Boş listede Gözat çalışır; silme sonrasında adapter ve detay açarken kullanılan liste birlikte güncellenir.

## Çalıştırılan kontroller — 16 Ekim 2026

- Wrapper: `:app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest --console=plain` başarılı; mevcut 1 birim testi geçti.
- `:app:lintDebug --console=plain` başarılı. Projenin mevcut lint uyarıları devam ediyor.
- Pixel_9 / Android API 36 / emulator-5554 üzerinde `PreloadedInterstitialAdTest`: 3 test geçti. Kapsam: hazır olmayan reklam, geç callback, yükleme/gösterim hatası, tekrarlanan giriş ve callback, reklamın süresinin dolması ve görünümün yok edilmesi.
- APK'lar `adb install -r -t` ile mevcut veri temizlenmeden kuruldu. Instrumentation doğrudan `am instrument` ile çalıştırıldı.
- Etkileşimle doğrulandı: ikinci menü/kalp simgesi, boş liste ve Gözat, egzersizde favoriye ekleme, favorilerde gösterme ve detay açma, silme onayı ve tekrar boş liste, İstatistik/Profil sekmelerine geçiş ve favorilere dönüş.
- İnternet kapalı ve yeni süreçte Başlat → hazırlık ekranı reklam beklemeden ilerledi. İnternet tekrar açıldığında gezinme ortasında geç reklam açılmadı. Wi-Fi/mobil veri başlangıçtaki açık durumuna geri getirildi.
- Hazır Google test geçiş reklamı tıklamada gösterildi ve kapandıktan sonra egzersiz akışı devam etti. Test banner'ı dinlenme ekranında görsel olarak doğrulandı. Gerçek reklama tıklanmadı.

## Sınırlar

Kontroller mevcut çalışma ağacındaki API 36 / AGP 8.9.1 / Gradle 8.11.1 / Kotlin 2.0.10 ve Ads SDK 24.4.0 yapılandırmasıyla yapıldı. Önceden var olan build, IDE ve diğer uygulama düzenlemeleri bu değişikliğin commit kapsamına dahil edilmedi. Bu nedenle yalnızca bu commit'in temiz checkout'u aynı doğrulama ortamı olarak sunulmaz.

Başlangıç denemelerinden birinde Splash → Home sırasında ANR gözlendi. Tekrarlanan SDK başlatmaları kaldırıldıktan sonraki denemelerde tekrar gözlenmedi; kesin kök neden ve tüm cihazlarda çözüm kanıtlanmış değildir. UiAutomator aracının ayrı bir hata kaydı da uygulama çökmesi olarak değerlendirilmedi.

Ödüllü reklamın uçtan uca ödül kazanma akışı ve tüm banner yerleşimleri tek tek çalıştırılmadı. Release derlemesi, gelir/doluluk oranı, gerçek ağ gecikmesi ve diğer cihaz boyutları bu doğrulamanın kapsamında değil. Bu değişiklik reklam hazır değilken gösterimi atlar; bu davranışın gösterim sayısına etkisi olabilir.

Kod ve testler için self-review yapıldı; bağımsız reviewer çalıştırılmadı. CI durumu Git checkpoint sonucunda ayrıca belirtilir.
