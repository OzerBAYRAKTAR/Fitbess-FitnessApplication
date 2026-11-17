# Favoriler tasarım araştırması — 17 Kasım 2026

## Kapsam
Görünür uygulama adı **Get Fit at Home** olarak güncellenir. Favoriler bu teslimatta araştırma ve görsel konsepttir; uygulamadaki Favoriler kodu değiştirilmez. Tasarım örneği sayılar ve çizimler gerçek kullanıcı verisi değildir.

## Resmi kaynaklar ve sınırları
Google Play indirme bantları kesin toplam veya dünya sıralaması değildir. Aşağıdaki yüksek indirmeli uygulamalar benzer ürün akışları için seçildi; tüm platformların “en çok indirilen ilk dört” sıralaması iddia edilmez. Resmi metinler ve mağaza/kütüphane sunumları araştırıldı; oturum gerektiren güncel Favoriler ekranları çalıştırılıp piksel düzeyinde incelenmedi.

| Uygulama | Google Play bandı | Doğrulanmış yaklaşım | Bizim tasarıma alınan karar |
|---|---|---|---|
| [FitOn](https://play.google.com/store/apps/details?id=com.fiton.android) | 10M+ | Ayrıntı ekranındaki kalple kayıt; For You → Favorites ile tekrar erişim. [Resmi yardım](https://help.fitonapp.com/en/articles/7835263-how-can-i-favorite-trainers-workouts) | Tanıdık, görünür kalp ve açıklayıcı Favoriler başlığı. |
| [Freeletics](https://play.google.com/store/apps/details?hl=en_US&id=com.freeletics.lite) | 10M+ | Favori antrenmanları saklama, hızlı tekrar erişim ve paylaşım. [Resmi ürün özeti](https://www.freeletics.com/en/blog/posts/freeletics-2025-wrapped/) | Saklanan öğeyi hızlı tanıma ve inceleme. Paylaşım bizim projede olmadığı için tasarıma alınmadı. |
| [Nike Training Club](https://play.google.com/store/apps/details?id=com.nike.ntc) | 10M+ | Çok türde antrenman ve programları açık içerik hiyerarşisiyle sunan kütüphane. [Resmi ürün sayfası](https://www.nike.com/ntc-app) | Büyük hareket görseli, kısa başlık ve açık eylem. Güncel resmi Favorites akışı doğrulanmadığı için Favorites yerleşimi Nike'a atfedilmedi. |
| [Peloton](https://play.google.com/store/apps/details?id=com.onepeloton.callisto) | 1M+ | Sevilen dersi bookmark ile tekrar bulma; Collections ayrı bir kürasyon yapısı. [Resmi açıklama](https://www.onepeloton.com/blog/welcome-to-peloton) | Kaydetmenin amacını “kolay geri dönme” olarak anlatma. Koleksiyon veya ders kuyruğu eklenmedi. |

## Mevcut uygulamaya uyum
- Favoriler kaydedilmiş **tekil hareketleri** exerciseId üzerinden tekilleştirir. Model isim, açıklama, görsel/GIF ve erişim durumu sunar.
- Açık hareketi inceleme açıklama/GIF penceresine; kilitli hareket mevcut erişim seçeneklerine gider. Favoriye eklemek hareketi açmaz.
- Favoriden çıkarma onaylıdır. Konseptte kalp kontrolü onay akışını koruyarak çalışmalıdır.
- Boş durumdaki keşfet eylemi mevcut Home keşfine döner.
- Süre, kalori, kaydetme tarihi, son eklenen sırası, koleksiyon, arama/filtre ve “hepsini başlat” bu tasarımlarda gösterilmez.
- Kartlardaki kategori adı mevcut titleName ile bağlanabilir; erişim etiketi mevcut loading/locked/unlocked sözleşmesini korumalıdır.
- Kaynaklar: ui/Favourite/FavouriteMoveList/MoveListFragment.kt ve MoveListAdapter.kt; data/Models/ExerciseDetailModel/ExerciseDayExercise.kt. FavouriteMain ekranı mevcut kategori kataloğudur; kaydedilmiş hareket listesiyle karıştırılmamalıdır.

## Görsel karar
Onaylı ana sayfa ve Progress'in krem zemin, forest gradient/dalga, lime vurgu ve yuvarlatılmış kart dili kullanıldı. Yeni marka Get Fit at Home.

1. **01-favorites-populated.png:** örnek üç hareket; kısa kayıt özeti, büyük görsel, ad/bölge, erişim durumu, İncele veya Kilidi aç, ayrı kalp kontrolü.
2. **02-favorites-empty.png:** ilk kalbi ekleme davranışını anlatan sakin boş durum; tek baskın Hareketleri keşfet eylemi.

Çizimler AI ile üretilmiş konsept örnekleridir; mevcut GIFlerin birebir kopyası veya doğrulanmış egzersiz talimatı değildir. İleride entegrasyonda mevcut stabil ExerciseMedia kullanılmalıdır. Görseller tasarım referansıdır; bitmap ekran olarak uygulamaya gömülmemelidir. Native uyarlamada küçük ekran ve büyük yazıda kartlar yeniden akmalı; en az48dp hedefler, erişilebilir kalp/erişim etiketleri ve dört dil korunmalıdır.

## Üretim
Yerleşik image_gen aracı kullanıldı; CLI/API fallback kullanılmadı. Tam promptlar PROMPTS.md dosyasında bulunur. Marka değişikliği için build/emülatör kanıtı ayrı doğrulama raporundadır.

## Sonraki kullanıcı yönlendirmesi — 18 Kasım 2026
İki örnek kullanıcı tarafından beğenildi ve uygulamaya entegrasyonu istendi. Yeni kabul koşulu: kaydedilen hareket kartlarında kilit/erişim etiketi veya “Kilidi aç” düğmesi bulunmayacak; tüm kartlar “İncele” ile mevcut salt okunur görsel/açıklamayı açacak. İlk PNG, önceki değerlendirmeyi saklayan tarihsel referanstır; içindeki erişim etiketleri son entegrasyon için geçerli değildir. Favoriye ekleme herhangi bir kullanım hakkı yazmayacak; katalog ve antrenman yürütme kuralları korunacaktır.

Google Play için istenen yeni Favoriler görselleri her üç dilde de örnek hareket görselleriyle dolu listeyi gösterecek. Boş durum mağaza görseli olarak kullanılmayacak.
