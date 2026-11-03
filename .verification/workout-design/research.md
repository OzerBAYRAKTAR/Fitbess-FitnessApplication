# Evde Fit — antrenman akışı tasarım önerisi

Tarih: 2026-11-03

## Kapsam

Bu çalışmada içerik listesi, beslenme kısayolu ve nefes ekranı uygulama değişikliği kapsamındadır. Ana program gün listesi, gün detayı, dinlenme ve hareket oynatıcısı için üretilen görseller tasarım önerisidir; bu dört ekranın önerilen sürümü henüz uygulanmış değildir.

Sonraki kullanıcı onayıyla dört ekranın tasarım dili uygulamaya entegre edildi. Yukarıdaki cümle ilk araştırma aşamasını anlatır. Gerçek uygulama ve test kapsamı `implementation-report.md` dosyasındadır; konsept görseli ile gerçek emülatör görüntüleri ayrı tutulur.

## İnceleme yöntemi ve kaynaklar

Dört uygulamanın Google Play mağaza ekran görüntüleri tarayıcıda görsel olarak incelendi. Uygulamalar cihaza kurulmadı; mağaza tanıtımları uygulama içi davranış veya performans doğrulaması sayılmaz.

- [Kadınlar için Evde Egzersiz — SWIFTFLYERS](https://play.google.com/store/apps/details?id=swiftflyers.weightloss.workout.athome&hl=tr): Bölge seçimi, 21 günlük planın haftalara ayrılması ve hareket gösterimini öne çıkaran ekranlar. Evde Fit için alınan fikir: günleri taramayı kolaylaştırmak ve devam edilecek günü belirgin göstermek.
- [Tembel Kadın Egzersizleri — SWIFTFLYERS](https://play.google.com/store/apps/details?id=homeworkout.lazy.weight.loss.women.athome&hl=tr): Büyük yaşam tarzı görselleri ve altında kompakt antrenman satırları. Evde Fit için alınan fikir: görsel başlık ile veri listesinin farklı öneme sahip olması. Pembe renk ve reklam vaatleri kopyalanmıyor.
- [Kadın Fitness — Captain Show](https://play.google.com/store/apps/details?id=net.female.fitness.women.workout&hl=tr): Hareket önizlemelerini küçük resimlerle listeleyen, görsel program seçimi kullanan tanıtımlar. Evde Fit için alınan fikir: okunur hareket sırası ve kolay ulaşılır hareket bilgisi. Beslenme alanı bu talep doğrultusunda alınmıyor.
- [Isınma Hareketleri — Nexoft](https://play.google.com/store/apps/details?id=warmup.stretching.morningworkouts&hl=tr): Üç seviye, program kartları ve illüstrasyonla egzersiz anlatımı. Evde Fit için alınan fikir: mevcut GIF'leri koruyarak çevresindeki arayüzü iyileştirmek; tüm hareketleri yeni videolarla değiştirmek gerekmiyor.

## Önerilen dört ekran

1. **Seviye / günler:** Yeşil, kompakt fotoğraflı başlık; haftalara hızlı erişim; tamamlanan, devam edilen ve kilitli günün ayrı görünmesi. Yüzdelik ilerleme geri getirilmiyor. Kilitli günlerde normal tamamlamayla açılma yolu açıklanıyor.
2. **Gün detayı:** Geri butonu ile seviye/gün aynı üst hizada; hareket, süre ve tahmini kcal tek özet alanında. Tekrarlanan ücretsiz etiketleri yerine temiz hareket listesi. Sabit başlatma butonu.
3. **Dinlenme:** Geri sayım ekranın ana unsuru. Sonraki hareket daha küçük önizlemeyle gösteriliyor. Dinlenmeyi geç kontrolü net. Yeni bir süre uzatma özelliği varmış gibi gösterilmiyor.
4. **Egzersiz:** Mevcut GIF için büyük alan, hareket sırası, güçlü geri sayım, ortada büyük duraklatma ve iki yanda önceki/sonraki kontrolleri. Favori ve bilgi erişimi korunuyor. Reklam gösterilecekse kontrollerden ayrılmış alan gerektiriyor.

## Görsel üretim

Yerleşik imagegen ile dört telefonlu yüksek çözünürlüklü bir `ui-mockup` panosu üretildi. Tasarım yönü: orman yeşili, açık mint, kırık beyaz, koyu okunaklı tipografi; mevcut 2D hareket GIF'lerini temsil eden illüstrasyonlar. Görseldeki süre, hareket sayısı, gün ve kalori değerleri örnek içeriktir; gerçek uygulamada katalogdan alınmalıdır. Görsel, çalışan uygulama ekran görüntüsü değildir.

Dosya: `workout-flow-concept.png` (bu raporla aynı klasör). Üretim yönergesi: Dört Android telefonu yan yana göster; Türkçe gün listesi, antrenman detayı, dinlenme ve hareket ekranlarını tek bir forest-green/mint tasarım sisteminde oluştur. Gün listesinde 21 günlük program, hafta seçimleri, tamamlanan/devam edilen/kilitli durumlar; detayda fotoğraf şeridi ve üçlü özet; dinlenmede büyük sayaç ve sıradaki hareket; oynatıcıda büyük 2D gösterim, sayaç, önceki/duraklat/sonraki kontrolleri. Beslenme, abonelik baskısı, yüzdelik program ilerlemesi ve sağlık vaadi ekleme. Bu çizimler hareket doğruluğu referansı değildir; üretimde gerçek GIF kullanılır.

## Uygulama aşamasında korunacaklar

- XML/Fragment, yerel JSON/Room ve mevcut paket kimliği.
- Gün kilitleri, kalıcı reklam ödülleri, favoriler ve gerçek oturum kaydı.
- Dört dil, büyük font ve en az 48dp dokunma alanları.
- Reklam veya ağ yüklemesinin egzersiz arayüzünü bloke etmemesi.
- Dekoratif fotoğraf ile hareket öğretimi ayrımı: hareketleri mevcut GIF'ler anlatır.

## İçerik kataloğunda bulunan fark

Eski katalogda kol, karın ve esneme üçer; bel-boyun ve bacak-kalça birleşik kategorileri dörder set içeriyor. Yeni tasarım birleşik kategorileri iki ayrı bölgeye ayırdığı için boyun ve kalça ilk görünen seti eski level 3 oluyor. Üçüncü set uydurmak veya kalıcı kimlikleri değiştirmek yerine kullanıcıdan eski birleşik liste ile ayrı bölgede ücretsiz ilk set seçenekleri arasında tercih istenmiştir.

Kullanıcı kararı: **Eski listeleri aynen geri getir.** Katalog eski birleşik gruplara döndürülür; ilk set ücretsiz kuralı ve mevcut içerik/kalıcı kilit kimlikleri korunur. Bu karar dört ekranlık tasarım önizlemesinin uygulanması anlamına gelmez.
