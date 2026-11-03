# Orijinal alt antrenman listelerini geri getirme

Kullanıcı kararı: Eski listeleri aynen geri getir.

- Kollar: arm seviyeler1-3.
- Bel ve Boyun: bellyneck seviyeler1-4, önce iki bel sonra iki boyun seti.
- Karın: abs seviyeler1-3.
- Bacak ve Kalça: legbutt seviyeler1-4, önce iki bacak sonra iki kalça seti.
- Isınma/Esneme: stretch seviyeler1-3.

Home ve bölge keşfinde dört birleşik kutu (2 sütun). Isınma kısayolu doğrudan eski üç setin listesini açar; body selector gizlenir. Mevcut modern kart tasarımı korunur. Tüm grup etiketleri dört dilde.

JSON/MoveAccess/DataStore değişmedi. İlk set ücretsiz, level2+ mevcut ödül/adsiz erişim politikasında. Hareket IDleri aynı; mevcut kalıcı earned-ID kayıtları geçerlidir. Beslenme kaldırma ve nefes iyileştirmeleri korunur. Program/gün/dinlenme/oynatıcı mockup uygulanmadı.

Doğrulama: wrapper assembleDebug + assembleDebugAndroidTest + lintDebug PASS. Pixel9 API36 testleri ve ekran kanıtları bu klasörde. Katalog testi izole in-memory Room üzerinde gerçek JSON, her grup/set, ilk ücretsiz/sonrakiler kilitli, synthetic earned-ID ile yeniden seed sonrası erişim, favori/tamamlanma koruma test eder. Gerçek kullanıcı unlock kaydı değiştirilmedi.

Git: önceki bağımlı karışık çalışma ağacı korunur; commit/push yapılmadı.

Son emulator sonucu: 4/4 PASS (54.729s). TR/EN/DE/RU Home/bölge/gün görüntüleri; ısınma özgün3 set ve gidiş/geri testi PASS. Locale test finally ile önceki tercihe döner; font ayarı değiştirilmedi.

Son ısınma yinelenen başlık düzeltmesi assembleDebug PASS, hedefli emulator tekrar 1/1 PASS (9.54s). warmup-original.png güncellendi. Read-only son recheck temiz; seçim-koruma test açığı giderildi.
