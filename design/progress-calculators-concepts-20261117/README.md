# Progress ve hesaplayıcılar — görsel tasarım örnekleri

17 Kasım 2026. Üç uyumlu, yüksek çözünürlüklü Türkçe UI konsepti; uygulama ekranı veya çalışan prototip olarak sunulmaz. Sayılar açıkça örnek veridir. Bu teslimatta uygulama kodu değiştirilmedi.

1. `01-progress.png`: aktif süreyi öne çıkaran özet, ayrı antrenman/hareket sayıları, 7 gün / 4 hafta kontrolü, birimli günlük sütunlar ve gerçek program hedefi mantığı.
2. `02-calculators-hub.png`: üç hesaplayıcının faydasını, örnek sonucunu ve Hesapla eylemini belirginleştiren kartlar.
3. `03-bmi-result.png`: birimli girişler, büyük sonuç, kategori ve kısa yorumun birlikte göründüğü iç ekran örneği.

Built-in **image_gen** ile ayrı üç üretim yapıldı. İlk görsel özgün tasarım olarak üretildi; ikinci ilk görseli, üçüncü ikinci görseli yalnız stil referansı olarak kullandı. Krem / orman yeşili / sage / lime paleti, tipografi ve kart dili ortaktır. Orijinal üretimler korunarak PNG dosyaları buraya kopyalandı. Üretim briefleri `PROMPTS.md` içindedir.

## Araştırma ve uyarlama

Önceki ayrıntılı [araştırma/tasarım notu](../progress-calculators-20261117/README.md) ve gerçek resmî ürün görselleri yeniden değerlendirildi. Resmî sayfalar bu çalışma sırasında yeniden kontrol edildi. Bunlar yüksek indirmeli seçilmiş örneklerdir; kesin bir en çok indirilenler sıralaması değildir.

- [Strava Training Log](https://support.strava.com/en-us/articles/15402077-training-log): haftalık kayıt, seçilebilir aktivite ve dönem ayrıntısı → tek anlaşılır aktivite grafiği ve kayıtların açık sunumu.
- [Samsung Health geçmişi](https://www.samsung.com/us/support/answer/ANS10001370/): seçili tarih ve birim vurgusu → tarihlendirilmiş, dakika birimli grafik.
- [Google Fit](https://play.google.com/store/apps/details?id=com.google.android.apps.fitness): kısa özet metrikleri ve haftalık hedef sunumu → öncelikli tek büyük değer ve gerçek 21 günlük program ilerlemesi.
- [Garmin Connect](https://play.google.com/store/apps/details?id=com.garmin.android.apps.connectmobile): dönem kontrolü ve ayrıntı hiyerarşisi → 7 gün / 4 hafta ve özetin altında açıklayıcı kartlar.

Rakip marka veya görselleri tasarımlara alınmadı. Mevcut veri modelinde olmayan vücut geçmişi, sensör skoru veya sağlık garantisi oluşturulmadı. VKİ örneği 180 cm / 80 kg → 24,69 kg/m² olarak tutarlı; yetişkin kategorisi [CDC sınırlarına](https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html) dayanır ve tanı olarak sunulmaz.

## Görsel kontrol

Üç çıktı tam boy görsel olarak incelendi. Türkçe karakterler, kartlar, güvenli kenarlar ve metin okunabilirliği uygun. Progress sütunları 0+8+0+12+7+0+15 = 42 dakika; programın 4/21 değeri ayrı, toplam program ilerlemesidir. Hesaplayıcı değerleri örnek olarak işaretlidir. Bu bir görsel öz incelemedir; yeni Android çalışma zamanı testi yapıldığı iddia edilmez.

Üretim kimlikleri: `exec-e907681c-d7fc-4605-837f-aec97ebc8d3b`, `exec-72912054-bb8b-4d4d-b986-2620d0a20da5`, `exec-62d53ef9-d6c7-40dc-9406-b11a6370a488`.
