# Fitbess PO–teknik yönetici kullanımı

PO–teknik yönetici akışı bu proje için etkinleştirildi. Sen isteğini normal Türkçe yazarsın; ana sohbet PO rolüyle kapsamı ve kabul kriterlerini netleştirir, kısa İngilizce brief'i tek teknik yönetici alt ajanına aktarır. Yönetici uygular, ilgili testleri ve emülatör akışını çalıştırır, gerektiğinde bağımsız review alır, düzeltir ve yetkili çalışma dalına commit/push yapar. PO kanıtları kontrol edip sonucu Türkçe bildirir.

Örnek: “Favoriler ekranındaki silme onayını daha anlaşılır yap, test edip çalışma dalına gönder.” Ayrı bir prompt kopyalaman gerekmez. Gerekirse `$product-owner` veya `$technical-manager` ile rolü açıkça seçebilirsin. “Önce yalnızca analiz et” dediğinde uygulama ve Git adımları başlamaz.

## Kurulan dosyalar

- [PO skill](../.agents/skills/product-owner/SKILL.md): kullanıcı niyeti, brief ve sonuç kontrolü.
- [Teknik yönetici skill](../.agents/skills/technical-manager/SKILL.md): uygulama, doğrulama, review/düzeltme ve yetkili Git.
- [Native ajan](../.codex/agents/technical-manager.toml): technical_manager rolü; model tercihi üst oturumdan alınır.
- [Ortak akış](../.ai-playbook/workflows/po-manager.md): rol sahipliği ve aktarım sözleşmesi.
- [Araştırma](../.ai-playbook/integrations/codex/po-manager-research.md): seçenekler, resmî kaynaklar ve izin otomasyonu.
- [Proje ayarı](../.codex/config.toml): on-request + otomatik izin incelemesi talebi; iki eşzamanlı alt ajan sınırı.

## İzinler ve ilk kullanım

Bu kurulumda build → test → review → düzeltme → yetkili commit/push arasına gereksiz insan onayı konmaz. Araçların gerektirdiği onayları Codex'in ayrı otomatik izin inceleyicisi değerlendirebilir. PO veya teknik yönetici, sandbox izni veren makam değildir.

Proje ayarlarının ve yeni skill/ajan dosyalarının keşfi için yeni bir Codex sohbeti açmak gerekebilir. Proje config katmanı güvenilir olmalı. Mevcut sohbetin izin seçicisinde **Approve for me** seçili olduğunu kontrol et; managed politika veya hesap erişimi bu seçimi sınırlandırabilir. Dosyanın yazılmış olması mevcut sohbetin izin modunun değiştiğinin kanıtı değildir.

Full Access, global config değişikliği veya her isteği onaylayan hook kurulmadı. Gerçek bir ürün kararı, reddedilen işlem veya ilerlemeyen doğrulama hatası yine sana dönebilir. Ayrı sidebar sohbetleri ve arka plan servisi oluşturulmadı. Native delegasyon yoksa aynı roller sıralı yürütülür ve bu sınırlama açıkça raporlanır.

## Gözlenen doğrulama

Merkezi playbook'ta yapısal/link/TOML kontrolü, 8 validator testi ve iki skill'in resmî biçim kontrolü geçti. İzole kopyada gerçek teknik yönetici alt ajanı bozuk bir linki düzeltti; validator önce başarısız, sonra başarılı oldu. İkinci analysis-only görevinde 25 dosyanın hashleri değişmedi. Bağımsız salt okunur review somut bulgu bildirmedi.

Bu gözlemler native görev verme/sonuç alma ve analiz sınırını doğrular; yeni sohbette custom-agent keşfini, kalıcı izin davranışını veya tüm gelecekteki ürün görevlerini doğruladığı iddia edilmez. Kurulum Android uygulama kodunu değiştirmedi; bu görev için yeni Android build/emülatör testi gerekmedi. Önceki uygulama kontrolleri ayrı görevin kanıtıdır.
