# Fitbess AI Playbook entegrasyonu

Bu projede [AI Engineering Playbook](https://github.com/OzerBAYRAKTAR/ai-engineering-playbook) kullanılır. Kurallar ve skill talimatları İngilizce, kullanıcıya açıklamalar Türkçedir.

Kaynak sürüm: **0.1.0**. GitHub `main` dalından çekilen sabit commit: `653d57d05071a26fa6451c17b8dc910b7757a150`. Kaynak bilgisi [project-context.md](../project-context.md) içinde de kayıtlıdır.

- [AGENTS.md](../AGENTS.md): Codex'in proje giriş talimatları.
- [Proje bağlamı](../project-context.md): gerçek komutlar, mimari, emülatör ve Git yetkilendirmesi.
- `core/`, `platforms/`, `workflows/`: kaynaktan değiştirilmeden alınan ortak içerik.
- [Skills](../.agents/skills/): Android doğrulama, review ve Git checkpoint süreçleri.
- [Kaynaklar](SOURCES.md): resmî referanslar ve güncelleme yaklaşımı.

Yeni Codex oturumları proje AGENTS.md dosyasını kullanır. Üç skill mevcut oturumun kataloğunda görünmüyorsa talimatları açıkça okunabilir; otomatik keşfi yeni oturumda kontrol edin.

## Güncelleme

1. Merkezi playbook reposunu GitHub'dan çekin ve yeni commit farkını inceleyin.
2. Yalnızca yukarıda belirtilen yönetilen klasörleri ve dosyaları güncelleyin; skill'lerde yerel değişiklik varsa karşılaştırın.
3. Projeye özel AGENTS.md ve project-context.md kararlarını koruyun.
4. Bu dosyada ve proje bağlamında kaynak commit/sürüm bilgisini yenileyin.
5. Bağlantıları, kopyaların kaynakla eşleşmesini ve ilgili davranış senaryolarını doğrulayın; inceleme sonrası çalışma dalına commit/push yapın.

Bu entegrasyon Android uygulama kodunu değiştirmez. Belgelerin doğrulanması, emülatörde uygulama davranışının veya bütün testlerin doğrulandığı anlamına gelmez.
