# Google Play ek görseller — 18 Kasım 2026

Üç tema, TR/EN/DE olmak üzere **9 final PNG** eklendi. Önceki 01–06 ve bu klasör dışındaki eski doküman/önizlemeler değiştirilmedi. Play Console yayını ve Git işlemi yapılmadı.

## Final dosyalar

| Tema | Türkçe | English | Deutsch |
|---|---|---|---|
| Favoriler | ../tr/07-store.png | ../en/07-store.png | ../de/07-store.png |
| İlerleme | ../tr/08-store.png | ../en/08-store.png | ../de/08-store.png |
| Vücut hesaplayıcıları | ../tr/09-store.png | ../en/09-store.png | ../de/09-store.png |

Tümü 1080×1920, RGB PNG, alpha yok. validation.json her çıktının ve seçilmiş ham kaynağının SHA256 değerini, boyutunu ve kaynak ID eşleşmesini içerir. Başlangıçta kaydedilen **87 eski dosyanın tamamı byte olarak aynı** doğrulandı.

## İçerik ve görsel kontrol

- 07: Her dilde üç dolu tekil hareket kartı, kalpler ve İncele / Explore / Ansehen. Kilit, kilit açma, erişim etiketi, boş durum, süre, kalori, koleksiyon ve antrenman başlatma yok.
- 08: 42 aktif dakika, 3 tamamlanan antrenman, 24 hareket; yedi sütun 0/8/0/12/7/0/15 (toplam 42), günler 11–17, 4/21 halka ve 4 farklı gün. Örnek veriler / Sample data / Beispieldaten etiketi mevcut. Antrenman sayısı ile farklı program günleri ayrı kavramlardır.
- 09: BMI 24,69; günlük tahmini enerji 2.236 kcal; tahmini yağ oranı %18,4. EN biçimi 24.69 / 2,236 / 18.4; TR/DE biçimi 24,69 / 2.236 / 18,4. Her dilde örnek sonuç ve tahmin/tanı değil notu mevcut.
- Aynı Get Fit at Home yaprak markası üç temada korundu. Uydurulmuş Google Play PREMIUM rozeti ilk 09 taslağından image_gen ile kaldırıldı; taslak final değildir.
- Tüm dokuz final tam boy JPEG decode ile, üç dil contact sheet ve tema karşılaştırmalarıyla incelendi. Başlıklar, eylemler, sayı/birimler ve DE `Deine 21-Tage-Reise` doğru; somut kırpılma veya okunaksızlık saptanmadı.

## Kaynak / yöntem

02, 04 ve 05 Türkçe kaynakları tam boy incelendi; üretimde 04 ve 05 stil referansı olarak kullanıldı. Yeni raster görseller built-in image_gen ile oluşturuldu. Son EN/DE sürümler Türkçe kaynakların lokalizasyon düzenlemeleridir. Yerel hedefler düzenleme öncesinde view_image ile incelendi. Tüm kesin promptlar ve çağrı sırası PROMPTS.md / generation-log.json içinde. Athleteler gerçek kişiye atıf yapılmadan AI ile üretilmiştir; uygulama içi ekran görüntüsü yerine kavramsal mağaza sunumudur. Yeni özellik veya sağlık sonucu vaadi eklenmedi.

Sharp yalnız orantılı contain standardizasyonu, alpha kaldırma, JPEG inceleme kopyaları ve contact sheet kompozisyonu için kullanıldı; yazı çizimi/raster içerik yaması yapılmadı. `node additions-20261118/build.cjs` seçilmiş ham dosyalarla final piksellerini yeniden üretir. Image generation nondeterministiktir.

## İnceleme dosyaları ve checkpoint sınırı

contact-final-tr.jpg, contact-final-en.jpg, contact-final-de.jpg son üçlü dil önizlemeleridir. comparison-final-07/08/09.jpg tema bazında TR/EN/DE karşılaştırmasıdır. inspect-final-*.jpg tam boy kontrol kopyalarıdır.

Önerilen dar checkpoint: dokuz final PNG + bu README + PROMPTS.md + generation-log.json + validation.json + üç contact-final-*.jpg. build.cjs isteğe bağlı üretim kaydıdır. Ham selected dosyaları, eski/reddedilmiş taslaklar, baseline.json, write-provenance.cjs, inspect/comparison kopyaları yerel üretim/QA kanıtı olarak tutulabilir; final PNG yerine kullanılmamalı ve otomatik toplu stage edilmemelidir.
