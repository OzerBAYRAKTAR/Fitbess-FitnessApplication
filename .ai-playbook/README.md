# Fitbess AI Playbook entegrasyonu

Bu projede [AI Engineering Playbook](https://github.com/OzerBAYRAKTAR/ai-engineering-playbook) kullanılır. Kurallar ve skill talimatları İngilizce, kullanıcıya açıklamalar Türkçedir.

Kaynak sürüm: **0.3.0**. GitHub `codex/po-manager-workflow` dalından çekilen sabit commit: `55622c9717b7e4292d72dac18fac57065dd19e6b`. Kaynak bilgisi [project-context.md](../project-context.md) içinde de kayıtlıdır.

- [AGENTS.md](../AGENTS.md): Codex'in proje giriş talimatları.
- [Proje bağlamı](../project-context.md): gerçek komutlar, mimari, emülatör ve Git yetkilendirmesi.
- `core/`, `platforms/`, `workflows/`: sabit kaynak commitinden alınan ortak içerik; aşağıda belirtilen yerel politika uyarlaması hariç.
- [Skills](../.agents/skills/): Android doğrulama, review ve Git checkpoint süreçleri.
- [Kaynaklar](SOURCES.md): resmî referanslar ve güncelleme yaklaşımı.
- [Ajan becerileri araştırması](research/agent-skills-review-2026-10.md): 20 Ekim 2026'da incelenen on açık kaynak depodan seçilen, projeye uyarlanmış ilkeler ve kesin kaynak commitleri.

Yeni Codex oturumları proje AGENTS.md dosyasını kullanır. Beş skill mevcut oturumun kataloğunda görünmüyorsa talimatları açıkça okunabilir; otomatik keşfi yeni oturumda kontrol edin.

## Otomatik kullanım ve taşınabilirlik

Her zaman uygulanması gereken kısa ilkeler `AGENTS.md` ve görev girişinde okunan playbook belgelerinde bulunur; bunlardan yararlanmak için bir skill adı yazmak gerekmez. Beceri klasörleri ise her ajan/istemci tarafından aynı şekilde keşfedilmez: desteklenen istemcide doğru konuma kurulmalı veya proje talimatlarında ilgili beceri dosyasının ne zaman okunacağı belirtilmelidir. Dosyaları başka bir projeye kopyalamak tek başına evrensel otomatik keşif ya da çalıştırma garantisi vermez. Yeni projeye entegrasyonda `AGENTS.md`, proje bağlamı, beceri dizin yolu ve gerçek tetikleme/davranış kontrolü o ajan ortamına göre doğrulanmalıdır.

Ortak ilkeleri göreve göre seçin; her istekte bu playbook'un tümünü modele yüklemeyin. İstemciye özgü plugin, hook, komut ve izin ayarlarını uyarlamadan projeler arasında kopyalamayın.

## Güncelleme

1. Merkezi playbook reposunu GitHub'dan çekin ve yeni commit farkını inceleyin.
2. Yalnızca yukarıda belirtilen yönetilen klasörleri ve dosyaları güncelleyin; skill'lerde yerel değişiklik varsa karşılaştırın.
3. Projeye özel AGENTS.md ve project-context.md kararlarını koruyun.
4. Bu dosyada ve proje bağlamında kaynak commit/sürüm bilgisini yenileyin.
5. Bağlantıları, kopyaların kaynakla eşleşmesini ve ilgili davranış senaryolarını doğrulayın; inceleme sonrası çalışma dalına commit/push yapın.

Bu entegrasyon Android uygulama kodunu değiştirmez. Belgelerin doğrulanması, emülatörde uygulama davranışının veya bütün testlerin doğrulandığı anlamına gelmez.


PO–teknik yönetici kurulumu için [kullanım rehberini](../docs/po-manager-workflow.md) ve [araştırmayı](integrations/codex/po-manager-research.md) okuyun. Yeni workflow, iki skill ve araştırma/config şablonları aynı kaynak commitinden alındı. .codex içindeki aktif ayarlar ve root AGENTS.md/proje bağlamı proje tarafından yönetilir.

## Yerel Git politikası uyarlaması

17 Ekim 2026 kullanıcı tercihi: otomatik commit/push yalnız büyük, anlamlı ve doğrulanmış iş bütünlerinde yapılır; her düzenlemede yapılmaz. PR kendiliğinden açılmaz. `workflows/development.md` ve Git checkpoint skill'i bu tercihe göre yerel olarak uyarlanmıştır. Merkezi kaynak şimdi 0.3.0 sürümünde `55622c9717b7e4292d72dac18fac57065dd19e6b` commit'indedir; sonraki merkezi güncellemelerde bu yerel Git politikası korunmalıdır.
