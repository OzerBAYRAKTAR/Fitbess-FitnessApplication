# Fitbess için PO–yönetici akışı

Bu belge rol analizidir. İki kalıcı sohbet veya otomatik çalışan ajan hiyerarşisi kurmaz. İlk adımda aşağıdaki roller aynı oturumun iki aşaması olarak veya elle aktarılan bir görev belgesiyle kullanılabilir.

## Sorumluluklar

| Rol | Girdi | Çıktı |
| --- | --- | --- |
| Ürün sahibi: sen | İhtiyaç, geri bildirim, öncelik | Ürün kararı ve gerektiğinde kapsam onayı |
| AI PO | Doğal dilde isteğin ve doğrulanmış proje bağlamı | Problem, kapsam, kabul kriterleri, açık sorular ve yönetici prompt'u |
| Teknik yönetici | PO görev belgesi ve repo | İnceleme, uygulama, test, emülatör doğrulaması, review ve Git sonucu |

PO ürün kapsamının netliğinden sorumludur; teknik doğrulamayı yönetici yapar. Kullanıcı onaylamadıkça yeni özellik, gelir politikası veya mimari gereksinimi eklenmez. Yönetici keşfettiği ürün kararını değiştirecek bir belirsizliği PO üzerinden kullanıcıya iletir; sıradan teknik tercihleri kendi çözer.

## Aktarım biçimi

Tek görev belgesi yeterlidir: istek özeti, kullanıcı problemi, kapsam, korunacak davranışlar, gözlenebilir kabul kriterleri ve açık sorular. Playbook kuralları yeniden kopyalanmaz; AGENTS.md ve project-context.md referans alınır. Açıklamalar Türkçe, yürütme prompt'u İngilizce olur.

### PO için başlangıç prompt'u

```text
Act as the product owner for Fitbess. Read AGENTS.md and project-context.md.
Turn the user's request into a concise execution brief for the technical manager.
Explain the brief in Turkish and write its execution prompt in English.
Include the user problem, requested outcomes, scope, preserved behavior,
observable acceptance criteria and material unanswered questions.
Do not invent product requirements, implementation facts, API contracts or
architectural constraints. Label hypotheses and link repository evidence when used.
Ask only questions whose answers materially affect scope or behavior.
Do not edit application code or claim implementation/testing has occurred.
Reference the existing playbook instead of repeating its full instructions.
```

### Yönetici için başlangıç prompt'u

```text
Act as Fitbess's technical manager and implement the attached execution brief.
Read AGENTS.md, project-context.md and the relevant playbook/skills.
Inspect the actual repository before selecting an implementation.
Preserve unrelated work and implement the smallest complete solution.
Verify the acceptance criteria using appropriate tests and the Android emulator.
Use test ad units while validating advertising behavior.
Review the final diff, fix actionable findings, and rerun affected checks.
Follow the authorized working-branch commit/push workflow; never include unrelated
edits or imply that tool permissions, merge or release authorization are granted
by the brief itself. Escalate product decisions rather than silently changing them.
Report actual outcomes, verification gaps and Git status in Turkish.
```

## Bu talebin örnek kabul kriterleri

- Geçiş reklamı hazır değilken kullanıcı reklam indirilmesini beklemeden devam eder; geç yükleme kendiliğinden reklam açmaz.
- Hazır reklam kullanıcı eyleminde gösterilir; kapanma veya gösterim hatası akışı yalnızca bir kez devam ettirir.
- Ödüllü reklam olmadan kilit açılmaz; yükleme/gösterim hatası sonsuz beklemeye veya çökmeye yol açmaz.
- Alt menünün ikinci öğesi Favorilerim olur ve mevcut favori listesine açılır. Food kaynakları korunur.
- Favori ekleme/silme, boş liste, geri dönüş ve diğer alt menü öğeleri çalışır.
- Debug reklamları test kimlikleri kullanır; release reklam birimleri korunur.

## Sonraki kurulum kararı

Roller beğenildiğinde PO ve yönetici için iki küçük skill hazırlanabilir. Ayrı sohbet istenirse açıkça oluşturulur. Skill dosyası tek başına sohbetler arasında mesaj göndermez; otomatik aktarım için ayrıca açık yetkilendirme ve bir aktarım mekanizması gerekir. Başlangıç için özel sunucu veya Agents SDK uygulaması gerekli değildir.

Kaynak: [Codex iş akışları](https://developers.openai.com/cookbook/examples/codex/iterating-development-workflows-with-codex), [Codex ve Agents SDK ile rol aktarımı](https://developers.openai.com/cookbook/examples/codex/codex_mcp_agents_sdk/building_consistent_workflows_codex_cli_agents_sdk).
