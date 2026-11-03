# Nefes ekranı / rutin kısayolu takip raporu

- Complete your routine içindeki nutrition düğmesi ve listener kaldırıldı; diyet verisi ve navigasyon hedefi silinmedi.
- Nefes ekranının mevcut 8 statik tekniği mint/forest başlık ve numaralı açılır listeye taşındı. Timer eski ekranda yoktu; yeni sahte timer eklenmedi.
- Açık teknik recreate sonrası korunuyor, view binding temizleniyor, erişilebilir açık/kapalı durum dört dilde.
- Sitali mevcut dört dildeki doğru seyir_desc kaynağına bağlandı (eski XML yanlış alternatif burun metnini kullanıyordu).
- Katalog / ücretsiz ilk set / bölge birleştirme kararı kullanıcı yanıtı beklediği için değiştirilmedi. JSON, kayıtlı kilitler ve favoriler dokunulmadı.

## Doğrulama
- JBR + proje Gradle wrapper: assembleDebug, lintDebug PASS; son kaynak düzeltmesi + assembleDebugAndroidTest PASS.
- Pixel_9 / emulator-5554 / API36: BreathingGuideTest 1/1 normal font PASS (8.496s), 1/1 font1.3 PASS (6.335s).
- Test: sekiz teknik; açık başlığı kapatma; diğer tekniği açınca öncekini kapatma; Sitali metni; recreate state; geri düğmesinden Home.
- Normal ve büyük font screenshot görsel inceleme PASS. Locale değiştirilmedi (English); font_scale önceki 1.0 değerine finally ile döndü ve okuma 1.0 doğrulandı.
- Read-only code review: blocker yok. git diff --check PASS (yalnız CRLF uyarıları).
- Dört dil yeni string kaynakları mevcut; bu takipte diğer üç dil runtime testi tekrar edilmedi. TalkBack sesli denemesi yapılmadı.

## Artefaktlar
- breathing.png / breathing-large.png / large-font-test.txt bu klasörde.
- APK: app/build/outputs/apk/debug/app-debug.apk
- Test: app/src/androidTest/java/com/bayraktar/healthybackandneck/BreathingGuideTest.kt
- Lint: app/build/reports/lint-results-debug.html

Karışık ve önceki bağımlı çalışma ağacı korunmuştur; commit/push/PR/release yapılmadı.
