# Evde Fit tasarım ve gerçek antrenman kaydı doğrulaması

16 Ekim 2026. Uygulama görünen adı **Evde Fit: 21 Günlük Plan**; yayımlanmış `com.bayraktar.healthybackandneck` kimliği korunur. Google Play mağaza adı/listesi bu kod değişikliğiyle yayımlanmaz.

## Uygulanan kapsam

- XML/Fragment yapısında açık nötr yüzeyler, koyu metin, kontrollü yeşil vurgu, ortak sans-serif tipografi ve kart/buton dili. Alt menüde sürekli Ana Sayfa, Favoriler, İlerleme, Profil etiketleri.
- Tek ekranlı ilk açılış; kullanılmayan zorunlu vücut soruları ve sahte kişiselleştirme beklemesi kaldırıldı. Vücut bilgileri isteğe bağlı hesaplayıcılarda istenir. Boş/sıfır/taşan sayı doğrulaması, açık cinsiyet seçimi ve geçerli aktivite seçimi eklendi.
- Seviye ve bölge kataloğu, mevcut hareket GIF'leri, hazırlık → hareket → dinlenme → bitiş akışı korundu. Aktif antrenman/hazırlık/dinlenme banner'ları kaldırıldı; önceden yüklenen bitiş geçiş reklamı davranışı korundu.
- Favoriler gerçekten kaydedilen hareketleri gösterir; oluşturulmamış özel program iddiası yoktur. Profilin dil, değerlendirme ve gizlilik bağlantıları çalışır; bildirim gözlemcisinin açılışta tekrar iş planlaması önlenir.
- Room 11→12 açık, veriyi koruyan göç: UUID, başlangıç/bitiş zamanı, aktif süre, tamamlanan/planlanan hareket, seviye/gün/başlık saklanır. Tekrarlanan aynı UUID eklemesi yok sayılır. Tamamlanan günün sonraki günü açması kayıtla aynı transaction içindedir.
- Pause, dinlenme ve arkaplan süresi aktif süreye eklenmez. Geri çıkışlar kısmi oturumdur; hepsini atlamak program günü tamamlamaz. End view yeniden oluşturulması çift kayıt üretmez.
- İlerleme: gerçek haftalık oturum/hareket/aktif dakika kartları, yedi günlük aktivite şeridi, üç programın 21 günlük çubukları, tarihli tam/kısmi geçmiş, daha fazla gösterme ve boş durum. Aynı gün iki antrenman iki oturum, tek program günüdür. Kalori/yağ/kas kazanımı uydurulmaz.

## Eski verilerin anlamı

Mevcut `ExerciseDay.isCompleted` alanı uygulamada günün **kilidinin açık** olduğunu temsil eder. JSON'daki ilk günün true olması geçmişte antrenman yapıldığının kanıtı değildir. Bu alanlar ve favoriler korunur; eski verilere sahte tarih/süre eklenmez. İlerleme yalnız yeni gerçek oturum kayıtlarından hesaplanır. GIF dosyaları değiştirilmediği için bazılarının içinde eski Fitbess markası görünmeye devam eder.

## Tamamlanan kontroller

- Son çalışma ağacı Gradle wrapper: `:app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest`, ardından `:app:lintDebug` başarılı.
- 4 yerel birim testi geçti (1 mevcut + 3 yeni oturum tam/kısmi politikası testi).
- Lint: 0 hata, 1171 uyarı. Uyarısız proje olduğu iddia edilmez; geniş eski lint borcu bu işin kapsamı değildir.
- API36'da `WorkoutPersistenceTest`: 2/2 geçti. Göçte favori koruma, aynı ID'nin tekilleştirilmesi, DB yeniden açılışında kayıt, pause/saved-state yeniden oluşturma sırasında geçen süreyi dışlama sınandı.
- İlk `RedesignFlowTest` turu 5 test: Türkçe dilin geçici uygulanıp geri yüklenmesi ve tek ekran onboarding geçti. Diğer 3 senaryo bitişe ulaştı ancak testteki Hilt EntryPoint'in uygulama component'inde bulunmaması nedeniyle DB okuma/temizlik adımında hata verdi. Test bağlantısı ayrı Room bağlantısına düzeltildi; bu ilk tur tam geçiş kanıtı değildir.
- Home, Türkçe/İngilizce onboarding, gerçek GIF içeren hareket, dinlenme, tam ve kısmi bitiş ekranları emülatörde render edildi ve görsel incelendi. Tamamlanma testinde timer bitişi deterministik tetiklendi; 0 saniyelik örnek ekran gerçek 30 saniyelik çalışma iddiası değildir.
- Bağımsız salt okunur review: workout back-stack, kısmi çıkış kaydı, favori ikonu, rest-back yaşam döngüsü ve hesaplayıcı varsayılan cinsiyet bulguları düzeltildi. Son incelemede açık somut bulgu yok; reviewer testleri çalıştırmış gibi raporlanmaz.

## Son doğrulama ve checkpoint

- Son APK, mevcut Pixel_9 API36 AVD üzerine veri silmeden `install -r` ile kuruldu. Tam instrumented suite **19/19 geçti, 129.413 saniye**: 6 yeni UI akışı, 2 veri/süre testi ve 11 mevcut reklam/gezinme/inset/context testi. Türkçe onboarding, boş/0/taşan hesaplayıcı girdisi, pause+recreate, dinlenme geri çıkış dialogu+arkaplan, tam/kısmi kayıt, tekrarlanan aynı günde iki oturum/tek program günü doğrulandı. Son İlerleme görseli incelendi: iki tamamlanan test oturumu, dört hareket ve tek 21-günlük program günü doğru gösterildi. Kullanıcıya ait üç favori korundu. Eski gezinme testinin kalan tek sentetik kaydı kesin UUID ile temizlendi; temizlikle birlikte ilk açılış testi tekrar geçti (1 test, 9.059 saniye). Son DB kontrolünde 0 sentetik oturum ve 3 korunmuş favori görüldü. Launcher üzerinden açılış başarılı.
- Seçili Git içeriği HEAD üzerine ayrı kaynak ağacına çıkarıldı; açıklanmayan yerel minSdk/sürüm/AdMob ID ve IDE değişiklikleri dışarıda bırakıldı. Sandbox `.zip.lck` erişim engelinden sonra yetkili root çağrısıyla bu ağaçta `:app:assembleDebug --offline --console=plain` **başarılı: 6 dakika 3 saniye, 46 görev**. Bağımsız derlenebilirlik yalnız kirli çalışma ağacına dayandırılmadı.
- Önkoşul ve tasarım değişiklikleri iki çalışma dalı commit'i için hazır; push sonucu ayrıca bildirilecek. Kullanıcı IDE/release/yerel yapılandırma değişiklikleri dışarıda kalır.

## Sınırlar ve gözlenen sorunlar

Yalnız API36 Pixel_9 AVD mevcut. Eski Android API, büyük yazı tipi/küçük ekran, yatay/cutout ve gerçek süreç öldürülmesiyle UI geri yükleme henüz çalıştırılmadı. Saved-state süre testi model seviyesinde, ekran yeniden oluşturma akış testi Activity seviyesindedir. Gerçek Play reklam sunumu/gelir, mağaza metadatası, satın alma/diyet/backend bu değişiklik kapsamında değildir.

İlk ekran kontrollerinde eşzamanlı ağır Gradle/emülatör yükü sırasında iki Home açılış ANR'si gözlendi. Sonrasında derleme bittikten sonra seri çalıştırılan idle Home→Progress geçişinde yeni ANR görülmedi. Bu gözlem bütün cihazlarda performans sorunu olmadığını kanıtlamaz. Son 19 testlik seri runtime turu başarıyla tamamlandı.

## Git sahipliği

Başlangıç staged/unstaged diffi ve app/src kaynakları çalışma öncesinde ayrı snapshot'a alındı. Önceden staged `.idea/.name`, `AndroidProjectSystem.xml`, `other.xml` silinmesi ve `runConfigurations.xml` korunur. IDE/release/signing/local SDK yolları, açıklanmayan AdMob application ID, minSdk/sürüm değişimleri, NotificationWorker/utils ve project-context yerel değişimleri seçili commit dışında tutulur. Gerekli önceki API36/Gradle/derleme düzeltmeleri ayrı önkoşul, tasarım ve egzersiz güvenliği birlikte anlamlı sonraki değişiklik olarak hazırlanır.
