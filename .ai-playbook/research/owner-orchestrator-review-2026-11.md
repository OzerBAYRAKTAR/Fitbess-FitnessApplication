# Arkadaştan alınan skill paketi ve owner–orchestrator kararı

İnceleme: 13 Kasım 2026. Girdi: `skills.zip`, SHA-256 `c64781050d1a9fcb8be44861a34682e8ad8414980fff2b3815fc16598629ec6e`. Arşivde 90 kayıt ve 19 skill var; Markdown içerikleri statik incelendi, paket kurulmadı veya talimatları çalıştırılmadı. Bu değerlendirme davranış veya güvenlik sertifikası değildir.

## Ne işe yararlar?

| Skill | İşlev | Bizim için karar |
| --- | --- | --- |
| product-owner | Ürün kararları, MVP, rapor yorumlama | Mevcut PO korunur; yeni owner rolüyle kapsamlı karar ortaklığı eklenir |
| project-orchestrator | Executor/reviewer ve teslimat kapılarını yönetme | Kısa orchestrator skill'iyle uyarlanır |
| role-coordination | Sohbet eşleştirme, mesaj ve devam kararları | Kimlik, korelasyon ve tekrar engeli alınır; ağır protokol kopyalanmaz |
| product-owner-handoff | Owner sohbetini halefe taşıma | İhtiyaç halinde; ilk kurulum için ayrı skill gerekmiyor |
| orchestrator-handoff | Orchestrator devamlılığı | Durum yeniden kurma ilkesi ortak akışa alınır |
| project-discovery | Fikir, kapsam, mimari ve yol haritası | Owner brief'ine uyarlanır; ayrı skill gerçek tekrar ihtiyacında |
| scope-proportionality | MVP ile gereksiz genişlemeyi ayırma | Mevcut sadelik ve kapsam kurallarıyla birleştirilir |
| playbook-integrator | Projeye kurulum ve güncelleme | Mevcut entegrasyon rehberi korunur |
| project-map-builder | Kod ve doğrulama giriş noktalarının haritası | Büyük depolarda yararlı; her projeye zorunlu dosya eklenmez |
| feature-implementation | İnceleme, uygulama, test akışı | Mevcut development ve technical-manager ile örtüşüyor |
| bugfix-workflow | Hatanın kanıtla teşhisi ve regresyon | Mevcut engineering akışıyla örtüşüyor |
| test-only | Özellik geliştirmeden test iyileştirme | Test odaklı görevlerde yararlı; şimdilik mod ayrımı yeterli |
| code-review | Bağımsız hata/regresyon incelemesi | Mevcut skill korunur; bulgu kotası yok |
| ci-quality-pipeline | Değişikliğe göre CI ve tam kabul kapıları | Gerçek CI ihtiyacında ayrıca tasarlanır |
| flutter-architecture | Flutter yaşam döngüsü ve mimari | Android/Kotlin profiline doğrudan kopyalanmaz |
| ui-ux-quality | Durumlar, erişilebilirlik, responsive arayüz | UI görevlerinde yararlı; gerçek proje senaryolarıyla uyarlanmalı |
| mobile-release-readiness | İmzalama, mağaza, rollout ve gizlilik | Yayın hazırlığında yararlı; yayın yetkisi sağlamaz |
| prompt-architect | Başka ajana yürütülebilir prompt hazırlama | Doğrudan araç aktarımı yoksa yararlı |
| project-audit-prompt | Harici kapsamlı audit prompt'u | İkinci görüş istendiğinde; olağan teslimata zorunlu audit eklenmez |

## Güçlü yanlar ve sorunlar

Kapsamı küçük tutma, mevcut kodu inceleme, bağımsız review, gerçek test/CI kanıtı, ilgisiz değişiklikleri koruma ve izinle rol kararını ayırma yaklaşımı değerli. Bunların önemli kısmı depomuzda zaten var. “Her şeyi en iyi yönetir” iddiasını destekleyen karşılaştırmalı görev sonucu veya ölçüm pakette yok.

Owner 480, orchestrator 490, role-coordination 323 satır. Bu üç giriş dosyası bile 1.293 satır; ek protokol ve 597 satırlık e-posta köprüsü bağlam ve bakım maliyetini artırıyor. Uzunluk tek başına kusur değildir; ancak aynı izin/yayın/HOLD kurallarının tekrarı tutarsız güncelleme riskini büyütüyor.

Paket, `prompt_templates.md`, `.agents/ai-playbook-task-reports.md`, proje haritası ve playbook yollarına metin içi bağımlılıklar içeriyor. Bu harici dosyalar ZIP'te yok. Markdown bağlantı kontrolünün bunları yakalamaması, paketin kendine yeterli olduğunu göstermiyor. E-posta profili ve gerçek mesaj kaynağı doğrulaması gibi çalışma zamanı şartları da yalnızca skill kopyalamakla oluşmuyor.

İstenen deneyimle farkları: paket owner'ı varsayılan salt okunur tutuyor; mevcut orchestrator yoksa otomatik chat oluşturmak yerine başlangıç prompt'u veriyor; otomatik ilerleme ayrıca Auto Mode'a bağlı. Kullanıcının yalnızca ödeme/girişte soru beklentisine rağmen paket daha fazla korunan işlem sınıfı tanımlıyor. Bu sınırların bir kısmı gerekli; ancak skill'in “sınırsız yetki” sunduğu söylenemez.

## Güncel kaynaklardan alınan kararlar

- Skill, tekrarlanabilir çalışma yöntemidir; canlı veri, kimlik doğrulama ve eylem yetkisi araç katmanındadır. Bu nedenle sınırsız yetki yerine açık kapsam ve gerçek araç kontrolü kullanıyoruz. [OpenAI Skills](https://developers.openai.com/plugins/concepts/skills)
- Alt ajanları bağımsız araştırma veya ayrışan görevlerde kullanmak anlamlıdır; kısa ve birbirine bağımlı adımları gereksiz bölmüyoruz. Ortak dosyalarda yazarlık koordine edilir. API belgesi bu ilkeyi destekler, masaüstünde aynı API'nin bulunduğunu kanıtlamaz. [Multi-agent](https://developers.openai.com/api/docs/guides/agents-api/multi-agent)
- Otomatik inceleme, gerçek onay taleplerini ayrı değerlendirir ve ağ sınırını kaldırmaz. Owner kararıyla eş tutulmaz. API ayarlarını masaüstü config'ine doğrudan kopyalamıyoruz. [Automatic review](https://developers.openai.com/api/docs/guides/agents-api/sessions/approvals)
- App-server sohbet/turn/olay ve onay entegrasyonuna olanak verir; devamlı çalışan kontrol düzlemi gerekiyorsa ayrıca uygulama gerekir. İlk aşamada mevcut masaüstü araçlarının yeteneğini kontrol ediyoruz. [Codex platform yaklaşımı](https://developers.openai.com/blog/codex-as-a-platform)

Seçilen tasarım resmî belgelerin zorunlu tuttuğu tek yapı değildir. Bu oturumdaki chat araçlarının açıklamalarında açık kullanıcı isteğiyle ayrı chat oluşturma, yetkilendirilmiş chat'e mesaj ve asenkron bekleme bulunuyor. Kaynak mesajın doğrulanabilirliği ve yeni chat'in keşfettiği skill'ler pilotta ayrıca görülmeli; belge üzerinden çalışıyor kabul edilmiyor.

## Bizde oluşturulan yapı ve kullanım

[Owner](../skills/owner/SKILL.md) ve [orchestrator](../skills/orchestrator/SKILL.md), mevcut PO/technical-manager kurallarına dayanır. Eşleştirme, mesaj, durma ve karar sınırları tek [ortak akışta](../workflows/owner-orchestrator.md) bulunur. İki topoloji aynı görev için birlikte açılmaz. E-posta köprüsü, özel izin motoru ve yeni servis eklenmedi.

Kurulumdan sonra kullanıcı owner sohbetinde “Sen ownersın; bu projede mevcut orchestrator'a bağlan, yoksa yeni bir sohbet oluştur ve onunla görev boyunca mesajlaş” diyebilir. Owner kapsam içindeki plan ve mimari kararları alır; orchestrator uygulamayı alt ajanlara devreder, kanıtı kontrol eder ve gerekli soruları owner'a yollar. Yalnızca rol vermek kendiliğinden yeni özellik geliştirmez.

Ödeme ve giriş dışında gerçek insan onayı gerektiren bağlayıcı/dış/geri alınamaz işlemler de olabilir. Mevcut açık yetki tekrar sorulmaz; role yeni isim vermek yetki üretmez. Görev bitene kadar etkin oturumda çalışmak hedeflenir; bilgisayar kapalıyken veya kaynak limiti dolunca sınırsız devam garantisi verilmez.

## Değerlendirme durumu

Yeni davranış senaryoları [değerlendirme listesine](../examples/evaluation-scenarios.md) eklendi. Tasarım üzerinde yapılan masa başı kontrolünde rol-only başlangıcın iş uydurmaması, aynı peer'ın yeniden kullanılması, çifte mesajın tekrar iş başlatmaması, rutin kararın owner'da çözülmesi ve gerçek iznin role devredilememesi incelendi. Bunlar gerçek iki sohbetli çalıştırma değildir. Gerçek eşleştirme, alt ajan teslimatı ve yeniden bağlanma pilotu henüz çalıştırılmadı; kurulum başarılı diye raporlanamaz.
