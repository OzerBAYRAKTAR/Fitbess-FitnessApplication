# İlerleme ve vücut hesaplayıcıları — tasarım kararları

Araştırma tarihi: 17 Kasım 2026. Seçilen dört uygulamanın resmi Play listeleri, destek belgeleri ve gerçek ürün arayüzü içeren resmi görselleri incelendi. İndirme bantları sıralama veya aktif kullanıcı sayısı değildir; özellikle ön yükleme toplamları etkileyebilir. Görsellerin bir kısmı eski veya pazarlama çerçevesi içinde olduğundan güncel kurulu uygulamanın her ayrıntısı hakkında iddia yapılmaz.

| Resmi kaynak | Gözlenen desen | Fitbess uyarlaması |
| --- | --- | --- |
| [Samsung Health](https://play.google.com/store/apps/details?id=com.sec.android.app.shealth), 1B+; [adım geçmişi yardımı](https://www.samsung.com/us/support/answer/ANS10001370/) | Seçili tarih belirgin, sayı ve birimi açık; adım kartında hedef ve ilerleme ayrılıyor. | Tarih aralığı ve seçili grafik sütununun açıklaması. Yalnız mevcut gerçek 21 günlük program hedefleri; adım/sensör hedefi eklenmedi. |
| [Strava](https://play.google.com/store/apps/details?id=com.strava), 100M+; [Training Log](https://support.strava.com/en-us/articles/15402077-training-log) | İlerleme, haftalık aktivite özeti, zaman/toplam ve geçmiş ayrıntısı ayrı katmanlar. Resmi topluluk görseli Şubat 2025 örneğidir. | Kısa dönem özeti → birimli grafik → program günleri → kompakt tarihli kayıtlar. Rota, mesafe veya üyelik özelliği taklit edilmedi. |
| [Google Fit](https://play.google.com/store/apps/details?id=com.google.android.apps.fitness), 100M+; [aktivite takibi](https://support.google.com/fit/answer/6075066?hl=en) | Birimli küçük metrikler, günlük/haftalık özetler ve hedefin metinsel karşılığı birlikte. | Tamamlanan antrenman, aktif dakika, yapılan hareket birbirinden ayrılıyor. Kalori yakımı, sağlık puanı veya kilo trendi üretilmedi. |
| [Garmin Connect](https://play.google.com/store/apps/details?id=com.garmin.android.apps.connectmobile), 50M+; [grafik inceleme](https://support.garmin.com/en-PH/?faq=V0T9hu0HIa8uj2gi0ea9aA) | Büyük değer, açıklayıcı metin, 1d/7d/4w dönem kontrolü ve ayrıntı katmanı. | Son 7 gün / son 4 hafta; günlük sütun veya ardışık yedi günlük dört sütun. Body Battery, uyku veya sensör puanı eklenmedi. |

Bunlar Fitbess için özgün düzenleme kararlarıdır; rakip görseli, marka öğesi veya arayüz kodu ürüne alınmadı. Ham araştırma görselleri teslimata/commite dahil edilmez. Ayrıntılı kaynak ve sınırlama notları yerel doğrulama klasöründe tutulur.

## Bilgi mimarisi ve gerçek veri sınırı

- İlerleme: Room `workout_sessions` kaynağı. Kısmi antrenmanların aktif süresi ve tamamlanan hareketleri sayılır; tam antrenman sayısına katılmaz. Geçerli program günleri seviye/gün çiftinde tekilleştirilir. Tekrar antrenmanlar gerçek seans sayısını artırabilir, program günü sayısını artırmaz.
- Son 7 gün yerel takvim günlerine göre; son 4 hafta ardışık 28 gün ve dört adet yedi günlük dönemdir. Gelecekteki ve tutarsız kayıtlar gösterge toplamlarını şişirmez. Veri yokluğu sıfır kayıttan ayrı açıklanır. Grafik açıklaması renk dışındaki sayısal karşılığı da sunar.
- Vücut hesaplayıcıları aynı sayfada ayrı, belirgin üç karttır; uzun geçmişten önce gelir. Sonuç ancak mevcut ölçülerle yeniden hesaplandığında kayıtlı değerle uyuşuyorsa gösterilir. Zaman damgası, ölçüm geçmişi ve vücut trendi bulunmadığından bunlar uydurulmaz.
- Üç mevcut XML/Fragment hedefi ve gezinme kimlikleri korunur. Yeni bağımlılık, Compose geçişi, Room şeması veya yayımlanan uygulama kimliği değişikliği yoktur.

## Hesaplama ve sunum

- Boy cm, kilo kg, çevreler cm ve yaş tam sayı olarak korunur. Günlük enerji formülü ve 1.2/1.3/1.4/1.5 aktivite katsayıları değiştirilmedi. Yağ oranı formülündeki mevcut yoğunluk katsayıları korunur; erkek formülünde kullanılmayan kalça ölçüsü zorunlu değildir.
- [CDC yetişkin VKİ kategorileri](https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html): ham değer üzerinden <18.5, 18.5–<25, 25–<30, ≥30. Görüntüleme yuvarlaması sınıfı belirlemez. Kategori yalnız 20 yaş ve üzeri yetişkinler için tarama göstergesidir; tanı değildir. Eski İngilizce fazla ağırlık/obezite etiketleri tersliği giderilir.
- Geçerli sonuç ve ilgili ölçüler mevcut DataStore anahtarlarına tek işlemde kaydedilir. Geçersiz hesaplama ölçüleri değiştirmez. Eski TR/EN/DE/RU cinsiyet metinleri aynı anahtardan geri okunur.
- Birimli girişler, en az 48dp seçim/kapatma hedefleri, satır içi hatalar ve büyük sonuç alanı kullanılır. Herhangi bir giriş veya seçim değiştiğinde önceki sonuç gizlenir. Sonuçlar sağlık önerisi, tüketilen kalori veya antrenman ilerlemesi olarak sunulmaz.

Doğrulama: saf toplama/hesaplama testleri, dört dilde gerçek emülatör ekranları, boş ve kayıtlı durum, geri dönüş/yeniden oluşturma ve küçük ekran/büyük yazı senaryoları ilgili doğrulama raporunda kaydedilir. Önceki reklam akışı değişiklikleri bu kapsamın dışındadır.
