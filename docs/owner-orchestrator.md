# Fitbess owner–orchestrator kullanımı

Yedi skill `.agents/skills/` altında proje becerisi olarak kuruldu. Yeni Codex oturumunda keşfi kontrol edin. Mevcut oturum kataloğu yenilenmiyorsa ilgili SKILL.md açıkça okunabilir.

Owner sohbetinde: “Sen ownersın; Fitbess projesinde mevcut orchestrator ile bağlantı kur, yoksa yeni bir sohbet oluştur ve görevlerimi onunla mesajlaşarak tamamla.” Owner planı ve mimari kararları hazırlar; orchestrator alt ajanlarla uygular, doğrular ve gerektiğinde bağımsız review yaptırır. Rutin kararlar owner'a döner. Tek görev için eski PO-manager topolojisi aynı anda başlatılmaz.

- [Owner skill](../.agents/skills/owner/SKILL.md)
- [Orchestrator skill](../.agents/skills/orchestrator/SKILL.md)
- [Ortak koordinasyon akışı](../.ai-playbook/workflows/owner-orchestrator.md)
- [Kurulum ve güncelleme komutları](../.ai-playbook/integrations/codex/README.md)
- [Kaynak sürüm ve yerel uyarlama manifesti](../.ai-playbook-manifest.json)

Owner'ın kapsam içi kararı gerçek platform onayının yerine geçmez. Ödeme, giriş/MFA ve yeni yetki gerektiren bağlayıcı/geri alınamaz işlemler kullanıcıya döner. Mevcut Git yetkisi ve yalnız anlamlı doğrulanmış aşamada checkpoint tercihi korunur.

Bu entegrasyonda dosya kurulumu doğrulanır; gerçek owner-orchestrator sohbet eşleştirmesi ve Android çalışma zamanı testi yapılmış sayılmaz. Uygulama kodu değişmediği için emülatör başlatılmaz.
