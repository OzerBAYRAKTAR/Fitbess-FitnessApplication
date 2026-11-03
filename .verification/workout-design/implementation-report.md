# Antrenman akışı tasarımı — uygulama ve doğrulama

2026-11-03, `codex/exercise-runtime-ads-stability` çalışma ağacı.

## Uygulanan ekranlar

- Başlangıç, orta ve ileri seviye: ortak fotoğraflı başlık, üç hafta kaydırma düğmesi, 21 günlük gerçek veri listesi; devam/tamamlandı/kilitli durumları. RecyclerView üzerindeki gereksiz dış NestedScrollView kaldırıldı. Tamamlandı etiketi erişim bayrağı yerine tamamlanmış WorkoutSession kayıtlarından alınır.
- Gün detayı: geri ve başlık aynı üst bölümde; yerel dekoratif fotoğraf; gerçek hareket/süre/tahmini kalori; numaralı GIF önizlemeleri; mevcut erişim kurallarını kullanan sabit başlatma düğmesi. Tekrarlanan ücretsiz satır metinleri kaldırıldı.
- Dinlenme: büyük halka sayaç, sıradaki hareket GIF'i, sıra numarası ve dinlenmeyi geç. Kalan dinlenme süresi ekran yeniden yaratılmasında korunur. Çıkış ve hızlı çift dokunma navigasyon koruması ile çalışır.
- Hareket: büyük mevcut GIF, oturum başlığı/sırası, halka sayaç, önceki–duraklat/devam–sonraki; bilgi ve favori kontrolleri. Mevcut banner alanı kontrol bölümünün dışında tutulur.

Tasarım, gerçek içerik ve mevcut kontrollerle uyarlanmıştır; konseptteki örnek hareket çizimleri uygulamaya eğitim görseli olarak aktarılmamıştır. Program yüzdelik ilerlemesi eklenmedi. Dört dil kaynakları sağlandı. Veritabanı şeması, ürün/kilit kimlikleri ve önceki turda geri getirilen beş katalog grubu değiştirilmedi.

## Kanıt

- Gradle wrapper + Android Studio JBR: assembleDebug ve assembleDebugAndroidTest başarılı.
- 27 birim testi, 0 hata/başarısızlık.
- lintDebug başarılı, hata yok; proje genelinde uyarılar devam ediyor.
- Pixel_9 / emulator-5554 / API36: `install -r`, kullanıcı verisi temizlenmedi.
- WorkoutFlowDesignTest, İngilizce normal font: 2/2 geçti.
- Aynı iki test Almanca font_scale=1.3 ile geçti. Önceki font ölçeği finally bloğunda geri yüklendi; test dili tercihi ve varsayılan Locale de finally/@After ile geri yüklenir.
- Türkçe son tur da 2/2 geçti (26.858s). Test sonunda font_scale=1.0 doğrulandı. İngilizce normal ve Almanca büyük font örnekleri görsel olarak incelendi.
- Test kapsamı: üç seviye ve 21 gün; üçüncü haftaya/ilk haftaya kaydırma; gün açma ve geri dönüş; oynatıcı duraklatma ve recreate; bilgi penceresi sonrası duraklatmanın korunması; dinlenme recreate; çift tıklamayla tek geçiş; önceki hareket.
- Gerçek ekran görüntüleri `workout-design-verification/` klasöründe. `de-` önekli dosyalar büyük font kontrolüdür. `tr-` dosyaları Türkçe son doğrulama turudur.

## İnceleme ve sınırlar

Yardımcı ajan kullanım sınırına ulaştığında ana ajan işi devraldı. Son inceleme ana ajan tarafından yapıldı; bağımsız son inceleme olarak sunulmaz. Yeni testin parser parametre tipi hatası giderildi. Kaynak/binding, timer yaşam döngüsü, erişim/tamamlanma ayrımı ve hızlı navigasyon incelendi.

Son `git diff --check` başarılı. Üçüncü seviye Fragment dosyasındaki satır sonu/boşluk sorunu yalnız biçimsel olarak temizlendi.

Bu tur gerçek reklam tamamlama veya satın alma testi içermez. Emülatör kontrolü tüm cihazlar için performans veya sıfır çökme garantisi değildir. Üretim AAB/imza/mağaza yayını yapılmadı. APK `app/build/outputs/apk/debug/app-debug.apk`.

Çalışma ağacı önceki yetkili değişikliklerle birlikte olduğundan parçalı commit/push yapılmadı; mevcut çalışma korundu. Görsellerin üretim yönergeleri `asset-provenance.md` dosyasında.
