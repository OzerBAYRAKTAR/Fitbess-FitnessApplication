# Profil, hareket erişimi ve dört sekme düzeni

17 Ekim 2026. Başlangıç: `codex/evde-fit-redesign`, `3af8ae3`.

## Kapsam ve veri kuralları

- Ana sayfa, Favoriler, İlerleme ve Profil ortak nötr arka plan, beyaz kart, orman yeşili vurgu, sans-serif başlık/metin ölçeği kullanır. Profil eylemleri grupludur; dil simgesi küre, değerlendirme yıldız ve paylaşım kendi simgesiyle gösterilir.
- İlerleme yedi günlük aktif süre grafiği, etkin günler, mevcut 21 günlük program çubukları ve tarihli geçmişi birlikte gösterir. Kısmi oturumun gerçekten çalışılan süresi hesaba katılır. Pozitif fakat bir dakikadan kısa süre ` <1 ` olarak gösterilir; erişilebilir açıklama dakika/saniye verir. Yağ/kas/kalori veya geçmiş veri uydurulmaz.
- Başlık İngilizce `Back & Neck Exercises`, Türkçe `Sırt ve Boyun Egzersizleri`; Almanca ve mevcut doğru Rusça karşılık korunur.
- Beş bölgenin ilk alt listesi ücretsizdir. Bütün listeler açılabilir; sonraki listelerde her `ExerciseDayExercise.id` ayrı ödülle açılır. `exerciseId` paylaşıldığı için izin anahtarı olarak kullanılmaz. Yalnız kazanılan ödül callback'i kalıcı DataStore kaydı oluşturur; iptal, kapanış ve reklam hatası açmaz. Debug sürüm Google test reklam kimliğini kullanır.
- Kilitli hareketler simge ve açıklamayla belirtilir; devre dışı başlat düğmesi ayrı, okunabilir renk durumuna sahiptir. Kilitli hareketlerin önizlemesi oynatılmaz; başlat düğmesi yalnız açık hareketleri ve gerçek adetlerini kullanır. Hazırlık girişinde erişim tekrar okunur. Favoriler silinmez; aynı hareketin zaten erişilebilir program/ücretsiz temsili varsa tercih edilir. 21 günlük program kilitleri değişmez.
- Kazanılmış hareket ödülü, reklam sırasında ekran yeniden oluşturulsa da uygulama kapsamlı kısa IO yazmasıyla kaydedilir. Program reklamlarının önceki lifecycle davranışı korunur. İzinler cihazdaki DataStore'dadır; uygulama verisi silinirse korunacağı veya cihazlar arası senkronize olacağı iddia edilmez.
- Su yaklaşık 4 saat, motivasyon yaklaşık 12 saat aralıkla benzersiz WorkManager işi kullanır. İşler ilk açılışta önceki tercihlerden uzlaştırılır, kendi eski etiketleri ve eski ID1 bildirimi emekliye ayrılır. KEEP tekrar açılışta süreyi sıfırlamaz. Tercih kapatma ve bildirim gönderme aynı kilitle sıralanır; çalışan Worker son tercihi/izin ve kanal durumunu denetler. Kanallar korunur, yeni bildirim kimlikleri ayrıdır.
- İzin ancak kullanıcı hatırlatıcı açtığında istenir; engelli uygulama/kanal ayarı açıkça gösterilir. Değerlendirme market bağlantısını, yoksa web bağlantısını dener; paylaşım Android seçicisi üzerinden yayımlanmış paket bağlantısını içerir.

## Tasarım ve platform kaynakları

- [Hevy özellikleri](https://www.hevyapp.com/features/) ve [istatistik açıklaması](https://help.hevyapp.com/hc/en-us/articles/35702030346903-Hevy-Statistics-Explained-Track-Your-Training-Progress-and-Muscle-Growth): okunabilir metrik, sakin grafik ve geçmiş hiyerarşisi.
- [Seven](https://seven.app/): ölçülü ilerleme/başarı sunumu; mevcut ilk antrenman kartı gerçek tamamlanmaya bağlıdır.
- [Freeletics başlangıç](https://www.freeletics.com/en/blog/posts/getting-started-with-freeletics/): belirgin antrenman başlangıcı ve tutarlı sekmeler.
- [Android bildirim izni](https://developer.android.com/develop/ui/views/notifications/notification-permission), [AdMob ödüllü reklam](https://developers.google.com/admob/android/rewarded). WorkManager 2.7.1 `Operation.await` imzası kurulu kaynak JAR'ından doğrulandı; yeni bağımlılık eklenmedi.

## Doğrulama durumu

- İlk derleme eski RecyclerView API adı, nullable SafeArgs ve doğrudan ListenableFuture erişimi hatalarını yakaladı. Uyumlu çağrılar ve kurulu KTX `await` ile düzeltildi.
- İkinci `:app:assembleDebug :app:testDebugUnitTest` başarılı; 6 birim testi, hata yok. İki yeni test beş kategori erişim sınırları ve yerel gece yarısı/kısmi/gelecek oturum haftalık hesaplarını kapsar.
- Son kaynak `:app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug` başarılı. Test APK uyumluluk düzeltmeleri ve son görsel kabul değişikliklerinden sonra dört Gradle görevi 2 dakika 56 saniyede geçti; 6/6 birim testi XML raporundan doğrulandı.
- API36 Pixel_9 emülatörüne iki APK kuruldu. İlk 28 testlik instrumentation turunda 27 geçti, pozitif bildirim snapshot testi yarış nedeniyle başarısız oldu. Son düzeltmelerden sonra etkilenen ProfileAndCatalogTest 5/5 ve PolishedTabsTest 2/2 geçti (7 test, 32.838 saniye). Diğer 21 test önceki turda geçti. Bildirim teslim testi için başlangıçta kapalı izin geçici açıldı; tur sonunda önceki `granted=false` durumu ve izin bayrakları geri yüklendi.
- İlk instrumentation turunda iki `deliver` çağrısı başarılı döndü, fakat sistem bildirim kuyruğu tamamlanmadan alınan snapshot yalnız ID101 gösterdi. Pozitif teslim testine 5 saniye sınırlı bekleme eklendi; iki ayrı kimlik ve intent assertion'ları değiştirilmedi. Sınırlı beklemeyle tekrar test geçti; gerçek 4/12 saatlik sistem zamanlaması beklenmedi, doğrudan Worker teslim yolu doğrulandı.
- Lint raporu: 0 hata, 1173 uyarı; eski geniş lint borcu temizlenmiş sayılmaz.
- Favori ekranı veri sorgusu tamamlanmadan sıfır kayıt göstermez. Türkçe dört sekme testine negatif kimlikli tek sentetik favori eklenir; mevcut favori ID kümesinin dil/sekme değişiminden sonra aynen korunduğu doğrulanır, yalnız testin kendi satırı temizlenir. Görseldeki `Verification favorite` kullanıcı verisi değildir.
- Yeni instrumentation kapsamı: kazanılmış/iptal/başarısız reklam callback'i; view kapanışı sonrası tek ödül; DataStore idempotent kayıt/yeniden okuma; benzersiz iş ve iptal; izinsiz/kapalı bildirim no-op; dış intent içeriği; Profil üzerinden gerçek dil değişimi; dört Türkçe sekme; kilitli listeye giriş/başlatma engeli ve ücretsiz giriş.
- `PolishedTabsTest` hazırlık duraklatma/devamını kontrol eder; geciken DataStore okumasının tam arkaplana geçiş yarışını deterministik olarak üretmez. Bu sınır kaynak incelemesiyle değerlendirildi, ayrıca runtime geçmiş gibi sunulmaz.
- Bağımsız salt okunur nihai kaynak incelemesinde açık somut bulgu yok. Dil locale'i, eski bildirim temizliği, erişim okuması sonrası arkaplanda hazırlık sayacı ve gerçek katalog kimliği kullanan test izolasyonu bulguları düzeltildi. İncelemeci runtime başarısı varsaymadı.

## Önceki favori snapshot farkı

Önceki tur snapshot'ında üç favori satır aynı `exerciseId=62` hareketinin 83/140/505 kopyalarıydı. Bu tur alınan DB snapshot'ında 287 hareket satırından yalnız bu üç `isFavourite` alanı 1→0 farklıydı; diğer alanlar aynıydı. Yeni testlerde bu harekete favoriden çıkarma işlemi yapılmıyor; veri kaybının kaynağı kesinleşmedi. Arada gerçek kullanıcı antrenmanı da bulunduğundan eski snapshot otomatik geri yüklenmedi, mevcut kullanıcı verisi ezilmedi. Yeni izolasyonlu favori koruma testi geçti: sentetik kayıt dil değişimi ve sekme dolaşımında korundu; test mevcut favori ID kümesini de karşılaştırdı.

- Eski regresyon testinin bıraktığı yalnız kesin sentetik Regression UUID temizlendi; temizlik/onboarding tek testi 1/1 geçti. Son salt okunur DB snapshot: sentetik negatif hareket kimlikleri yok, Regression oturumu yok; gerçek `legbutt` oturumu aynen korunuyor. Bu tur başındaki boş favori durumu aynı kaldı.

## Görsel kapsam ve sınırlamalar

- Root son Türkçe Ana sayfa, Favoriler, İlerleme, Profil ve kilitli hareket ekranlarını görsel olarak inceledi: ortak kart/tipografi, görünür kilit simgesi ve devre dışı düğme renk durumu kabul edildi. Favori ekranındaki `Verification favorite` yalnız test verisidir.
- Gerçek üretim reklamına tıklanmadı; ödül başarı/iptal/hata ve owner kapanışı sahte SDK reklamı ile test edildi. Gerçek reklam sunucusu bağlantısı ve reklam oynarken fiziksel cihaz döndürme doğrulanmadı.
- Yalnız mevcut API36 emülatörü kullanıldı. Eski API ve büyük yazı ölçeği kapsamı henüz çalıştırılmadı; geçmiş tur sonuçları bu değişikliğin yeni runtime sonucu sayılmaz.

## Korunan yerel değişiklikler ve Git

Başlangıç staged/unstaged diffleri ve ilgili dosyalar dış doğrulama dizininde `profile-category-baseline` altında kaydedildi. IDE ayarları, release AAB, build minSdk/sürüm, manifest reklam uygulama kimliği, utils.kt ve proje bağlamındaki önceki Kotlin hunkı bu checkpoint'e dahil edilmeyecek. Proje bağlamına yalnız kullanıcının yeni Git sıklığı/PR politikası HEAD üzerine uygulanarak aday oluşturulur. NotificationWorker mevcut yerel biçimi korunarak işlevi güncellendi. Veritabanı şeması, uygulama kimliği, GIF'ler ve paket yapısı değişmedi.

Kullanıcının 17 Ekim tercihi yerel playbook ve Git skill'ine işlendi: yalnız büyük, anlamlı ve doğrulanmış iş bütününde otomatik commit/push; her düzenlemede değil. Otomatik PR açılmaz. Upstream commit değiştirilmedi, yerel uyarlama README'de belirtilir.

Runtime doğrulaması ve bağımsız inceleme tamamlandı. 58 kapsam dosyası için seçili aday ağaç yerel IDE/sürüm/reklam kimliği değişikliklerinden bağımsız derlendi: `:app:assembleDebug --offline --console=plain` 1 dakika 53 saniyede başarılı. HEAD arşivindeki 701 takipli kaynak/Gradle girdisi karşılaştırıldı; seçili dosyalar aday hashlerine, diğerleri HEAD içeriğine uyuyor. Eksik, farklı veya fazladan eski kaynak dosyası yok. Bu büyük ve tamamlanmış iş bütünü için yetkili `codex/evde-fit-redesign` dalına tek commit/push yapılacak; PR açılmayacak. Commit/push sonucu ayrıca bildirilecek. GitHub Actions CI workflow bulunmadı; CI sonucu varmış gibi sunulmaz.
