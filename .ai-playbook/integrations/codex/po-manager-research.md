# PO–teknik yönetici araştırması ve kurulum kararı

Araştırma tarihi: 16 Ekim 2026. Bu rehberdeki kurulum tercihi, resmî belgelerin zorunlu tuttuğu tek mimari değildir. Codex'in yerel yeteneklerini mevcut playbook ile birleştiren, bu ihtiyaç için seçilmiş başlangıç düzenidir.

## Öneri: PO ana sohbet, teknik yönetici alt ajan

Sen doğal dilde isteğini PO'ya söylersin. PO kısa bir görev özeti ve kabul kriterleri çıkarır; İngilizce brief'i teknik yöneticiye aktarır. Yönetici gerçek kodu inceler, uygular, test eder, gerektiğinde bağımsız review ister, bulguları düzeltir ve yetkili çalışma dalına commit/push yapar. PO sonucu kullanıcı isteğiyle karşılaştırıp Türkçe raporlar. Her iç adımda yeniden “devam edeyim mi?” sorulmaz.

Bu tercih **manager-style orchestration** yaklaşımına benzer: kullanıcıyla konuşan ajan sonuçtan sorumlu kalırken uzmanları sınırlı görevler için çağırır. SDK'daki handoff ise kontrolü uzmana devretme modelidir. Bu kavramsal ayrım, uygulama içindeki native alt ajan aracının SDK `as_tool` API'siyle aynı olduğu anlamına gelmez. [Orchestration and handoffs](https://developers.openai.com/api/docs/guides/agents/orchestration)

## Hangi kurulum ne sağlar?

| Seçenek | İş aktarımı | Ek işletim yükü | Bu ihtiyaç için değerlendirme |
| --- | --- | --- | --- |
| Aynı ajan, iki rol prompt'u | Aynı bağlamda sıralı aşamalar | Düşük | Araç yoksa açıkça belirtilen fallback; ayrı ajan testi değildir |
| Codex native alt ajan | Ana sohbet görev verir, sonucu bekler ve düzeltme yollar | Düşük | Seçilen başlangıç; harici bir servis gerektirmez |
| İki ayrı sidebar sohbeti | Ayrı sohbetleri yöneten açık mesajlaşma akışı gerekir | Orta | Skill yüklemek kendi başına sohbetler arası otomasyon kurmaz |
| Codex CLI / app-server | Script veya özel arayüz görevleri yönetir | Orta | Harici görev kuyruğu, kayıt veya özel panel gerektiğinde |
| Agents SDK + Codex | Kodla tanımlı routing, handoff ve gözlenebilirlik | Daha yüksek | Şimdilik ek sunucu/API entegrasyonu gereksiz |

Codex alt ajanları ayrı bağlamla çalışabilir. Özellikle araştırma ve inceleme çıktısını ana sohbetten ayırmak yararlıdır; eşzamanlı yazarlarda çatışma riski vardır. Bu yüzden bu kurulumda bir uygulama yazarı bulunur, ikinci ajan gerektiğinde salt okunur review yapar. Fazladan ajanlar ek model/araç çalışması ve token tüketir; her işi daha hızlı veya daha doğru yapacağı varsayılmaz. [Subagents](https://learn.chatgpt.com/docs/agent-configuration/subagents)

Resmî Codex + Agents SDK örneği yönetici, tasarımcı, geliştirici ve tester arasında aktarımın mümkün olduğunu gösterir. Ancak kendi API anahtarı/bağımlılıkları olan bir örnektir; kurulmuş masaüstü oturumunun hazır bir özelliği gibi sunulmamalıdır. Şimdiki kurulum API tabanlı ayrı servis çalıştırmaz. [Çalışan örnek](https://developers.openai.com/cookbook/examples/codex/codex_mcp_agents_sdk/building_consistent_workflows_codex_cli_agents_sdk)

## İzin taleplerinin asıl kaynağı

Üç farklı karar vardır: kullanıcı bu işi istiyor mu, işlem mevcut dosya/ağ sınırları içinde mi, sınır dışındaki işlem nasıl onaylanacak? PO brief'i birinciyi taşır; diğer ikisini vermez. Alt ajan da üst oturumun izinlerini devralır. Yöneticiye “her şeye onay ver” yazmak güvenilir bir izin sistemi oluşturmaz.

| Kontrol | Etkisi |
| --- | --- |
| AGENTS.md ve skills | Roller, görev kapsamı, test/review/Git süreci |
| Sandbox / permission profile | Yerel komutların erişebildiği dosyalar ve ağ |
| Approval policy | Komutun ne zaman onay akışına gireceği |
| Approvals reviewer | İsteği kullanıcı mı otomatik inceleyici mi değerlendirecek? |
| Command rules | Belirli komut biçimleri için kalıcı ve dar istisnalar |

**Seçilen çözüm: Approve for me.** Uygun onay istekleri ayrı bir Codex inceleyicisine gider. Onaylanırsa iş sürer; reddedilirse güvenli bir alternatif veya kullanıcı kararı gerekir. Bu özellik sandbox'ı kaldırmaz ve sıfır soru garantisi vermez. Masaüstünde mevcut sohbet için yazı alanının altındaki izin seçicisinden kullanılabilir; erişim ve yönetilen politikalar belirleyicidir. [Auto-review](https://learn.chatgpt.com/docs/sandboxing/auto-review)

Proje için önerilen opt-in ayarlar [config şablonunda](po-manager.config.toml) bulunur: `approval_policy = "on-request"` ve `approvals_reviewer = "auto_review"`. Bunlar eski ayarların üzerine körlemesine yazılmaz. Mevcut sohbeti dosya düzenleyerek anında değiştirdiğimiz varsayılmaz; güvenilen proje ayarları ve yeni oturumda etkin değerler kontrol edilir. [Config reference](https://learn.chatgpt.com/docs/config-file/config-reference)

`approval_policy = "never"`, engellenen işlemlere izin verildiği anlamına gelmez; yeni onay alınamayan işler başarısız olabilir. `danger-full-access` farklı ve çok daha geniş bir erişim kararıdır; bu kurulum onu etkinleştirmez. `.git`, `.agents` ve `.codex` gibi korunan yollar nedeniyle proje içindeki bazı işlemler dahi özel izin gerektirebilir. [Agent approvals & security](https://learn.chatgpt.com/docs/agent-approvals-security)

Kalıcı komut kuralları tekrar eden, incelenmiş işlemlerde yardımcı olabilir. Geniş `python`, shell veya sınırsız Git kuralları yerine gerçek komutun dar prefix'i kullanılır. Komut yapısı değişirse eski kuralın eşleşmemesi normaldir; bunu aşmak için komut gizlenmez. Yeni kurallar güvenilen config katmanında yüklenir ve yeniden başlatma gerekebilir. [Rules](https://learn.chatgpt.com/docs/agent-configuration/rules)

## Hooks, sürekli çalışma ve dış otomasyon

Hooks belirli olaylarda script çalıştırabilir; örneğin araç sonrası kayıt veya duruşta kontrol. Güncel belgelerde `PermissionRequest` için karar döndürme de bulunur. Fakat hook tanımı güvenilmeli, değiştiğinde yeniden incelenmeli ve karar mantığı test edilmelidir. Her isteğe allow döndüren bir hook eklemiyoruz; hazır otomatik inceleyici varken özel bir izin motoru yazmak bu ihtiyaç için ek yük getirir. [Hooks](https://learn.chatgpt.com/docs/hooks)

CLI `codex exec` dışarıdan verilen sınırlı bir görevi çalıştırabilir ve yapılandırılmış çıktı üretebilir. İşletim sistemi servisi, zamanlayıcı veya görev kuyruğu bunun dışında ayrıca kurulur. Bilgisayar kapalıyken yerel emülatörde doğrulama süremez; kalıcı sohbet veya skill dosyası tek başına sürekli çalışan servis değildir. [Non-interactive mode](https://learn.chatgpt.com/docs/non-interactive-mode)

Özel uygulama gerektiğinde app-server thread/turn, olay ve onay akışını entegrasyona açar. Bu, ilk kurulum için gerekli değildir; gelecekte kendi kontrol paneli veya görev kuyruğu istenirse değerlendirilir. [Codex platform yaklaşımı](https://developers.openai.com/blog/codex-as-a-platform)

## Test, review ve durma koşulları

Özerklik, hatayı görmezden gelerek devam etmek anlamına gelmez. Yönetici gerçek komut çıktıları ve emülatör etkileşimiyle kabul kriterlerini değerlendirir. Başarısız kontrolde ilgili düzeltmeyi yapıp etkilenen kontrolü tekrarlar. Aynı hata yeni bilgi üretmiyorsa, bütçe dolduysa veya ürün/izin kararı gerekiyorsa durur ve tamamlanmayan işi açıkça raporlar. Böylece sonsuz review–düzeltme döngüsü ve “build geçti, her şey çalışıyor” sonucu önlenir. [Repair loops](https://developers.openai.com/cookbook/examples/codex/build_iterative_repair_loops_with_codex)

Gerçek kullanımda gözlenecek ölçütler: gereksiz insan sorusu sayısı, zorunlu izin kararları, başarısız kabul kriterleri, review'de bulunan gerçek hatalar, tekrar döngüleri ve Git'e ilgisiz değişiklik karışması. Başlangıçta ayrıca ölçüm servisi yazmak gerekmez; görev raporları yeterlidir.

## Taşınabilirlik ve sınır

PO brief'i ve teknik yönetici süreci teknoloji bağımsızdır. Android doğrulamasını proje bağlamı ve android-verification belirler; ileride Flutter için gerçek komutlar ve runtime senaryoları eklenir. Codex TOML dosyaları Cursor veya Claude ayarı olarak kopyalanmaz; bu araçların native ajan keşfi ve izinleri ayrıca araştırılır. Ortak İngilizce kurallar yeniden kullanılabilir.

Kurulumun bittiği; dosyaların doğrulanması, native görev aktarımının gerçekten denenmesi ve hangi izin ayarının etkin olduğunun ayrıştırılmasıyla raporlanır. Yalnızca TOML/YAML parse edilmesi otomasyonun uçtan uca çalıştığını kanıtlamaz.
