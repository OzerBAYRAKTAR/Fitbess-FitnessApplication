# Codex entegrasyonu

Kurulum uygulama reposunda yapılır. Bu playbook reposunun kendi AGENTS.md dosyası uygulama projesine kopyalanmaz.

## Otomatik kurulum ve GitHub güncellemesi

Python 3.11+ ve Git yeterlidir; ek Python paketi gerekmez. Codex için proje skill yolu `.agents/skills/` olarak resmî [kurulum belgesinde](https://developers.openai.com/api/reference/cli) de gösterilir. Standart skill kurucuları yalnız skill paketlerini kurmak için uygundur; burada ortak core/workflow belgeleri, kök talimatları ve yerel uyarlamalar da yönetildiği için depoya ait küçük bir Python script'i seçildi. Symlink/junction yönetici yetkisi ve platform farkları doğurabilir; script normal dosyalar kullanır.

Playbook'u GitHub'dan klonladıktan sonra, klonun kökünde:

```powershell
python scripts/sync_playbook.py install --project "C:/path/to/application" --check
python scripts/sync_playbook.py install --project "C:/path/to/application"
```

Kurucu bütün skill klasörlerini destek dosyalarıyla `.agents/skills/` içine, kaynak rehberleri `.ai-playbook/` içine yerleştirir. `AGENTS.md` dosyasında yalnız işaretli entegrasyon bloğunu yönetir; mevcut proje talimatları ve `project-context.md` korunur. Skill kurulumu kendi başına Git, ödeme veya tool izni vermez. Mevcut `.codex` config'ine veya hook'lara dokunulmaz.

Sonraki güncellemede uygulama projesinin kökünden, incelenmiş tam commit, tag veya dalı açıkça seçin:

```powershell
python .ai-playbook/scripts/sync_playbook.py update --project . --ref <reviewed-commit-or-tag> --check
python .ai-playbook/scripts/sync_playbook.py update --project . --ref <reviewed-commit-or-tag>
```

`update`, sabit playbook GitHub deposunu geçici dizine çeker, seçilen ref'i kesin commit'e çözer ve kurulumu aynı komutta eşitler. Uygulama reposunda `git pull`, checkout, commit veya push çalıştırmaz. Dal ref'i değişebileceğinden tekrarlanabilir kurulum için tam commit tercih edilir. İlk clone/pull tek başına kod çalıştırmaz; otomatik entegrasyonun açık giriş noktası bu komuttur. Klonlanan kaynağın script'leri, hook'ları veya build'i çalıştırılmaz. Kurucu `.ai-playbook-manifest.json` dosyasında kaynak commit, sürüm ve yönetilen dosya hash'lerini tutar; çalışma ağacından kurulum kirliyse bunu ayrıca işaretler.

Var olan dosya kaynakla aynıysa benimsenir. Değişmiş dosya varsa tüm dosya yazımlarından önce çakışma raporlanır; üzerine yazma seçeneği yoktur. İncelenmiş yerel varyant için örneğin `--preserve .agents/skills/git-checkpoint/SKILL.md` kullanılır; bu dosya sonraki güncellemelerde de yerel kalır ve güncelliği projede takip edilir. Diğer özel skill'ler silinmez. Kaynaktan kaldırılan yalnızca daha önce yönetilen ve değişmemiş dosyalar kaldırılır.

Eski elle yapılmış kurulumlarda kaynak Git klonundan `install --baseline-ref <previous-source-commit>` kullanılabilir. Önceki commit ile eşleşen kopyalar, metin dosyalarındaki CRLF/LF farkı tolere edilerek benimsenir. Farklı yerel uyarlamalar yine çakışır ve tek tek incelenir. Baseline yalnız manifest henüz yokken geçerlidir. Özel dosyaları topluca preserve ederek hataları gizlemeyin.

Kurucu sembolik link/junction ve yönetilen alan dışına çıkan yolları reddeder; eşzamanlı iki yazımı kilitle engeller. Dosya bazında değiştirme atomiktir, bütün dosya grubunun işlemi transaction değildir: disk/izin hatasında kısmi sonuç kalabilir. Manifest en son yazılır; kesintide diff'i inceleyin, çakışmayı çözün ve tekrar deneyin. `--check` hedef proje dosyalarını değiştirmez. Ağsız kurulum için daha önce incelenmiş yerel klonda `install` kullanılabilir.

Kurulumdan sonra manifest, kopya içerikleri ve yeni Codex oturumundaki skill kataloğu kontrol edilir. Dosyanın doğru yerde bulunması gerçek owner–orchestrator mesajlaşmasının doğrulandığı anlamına gelmez.

## İlk kurulum

1. Playbook'u belirli bir commit veya tag ile yerel bir klasöre alın.
2. Uygulama reposunda `.ai-playbook/` oluşturun; bu repodan `core/`, `platforms/`, `workflows/`, `VERSION` ve `SOURCES.md` dosyalarını buraya kopyalayın; `research/` altında bağlantı verilen raporları da alın ki yerel kaynak linkleri çalışmaya devam etsin.
3. Skill klasörlerini uygulamanın `.agents/skills/` dizinine kopyalayın. Mevcut aynı adlı skill varsa üzerine yazmadan karşılaştırın. `owner` ve `orchestrator` ayrı sohbet topolojisi içindir; kurulmaları tek başına bu topolojiyi etkinleştirmez.
4. `AGENTS.template.md` içeriğini proje kökündeki `AGENTS.md` ile birleştirin. Mevcut talimatları koruyun.
5. `project-context.template.md` dosyasını proje köküne `project-context.md` adıyla alın ve gerçek bilgilerle doldurun.
6. Kullanıcı bu proje için otomatik Git yetkisi verdiyse alanı etkinleştirin ve gerçek remote URL'sini kaydedin. Şablon varsayılanı yeni projelerde izin varsayılmasını önlemek için kapalıdır.
7. Kaynak repo, tam commit ve sürümü proje bağlamına yazın; tüm entegrasyon dosyalarını projenin sürüm kontrolüne alın.
8. Yeni Codex oturumunda skill keşfini ve dosya yollarını kontrol edin. Küçük bir görevle test edin.

Bu kişisel kullanım için kararlaştırılan tercihler: Türkçe açıklama, İngilizce kurallar, Android emülatörü ve doğrulanmış iş aşamalarında çalışma dalına otomatik commit/push. Bunları her projeye bilinçli şekilde uygulayın.

## Güncelleme

Yeni sürümün farklarını inceleyin; yalnızca yönetilen playbook içeriğini ve skill'leri güncelleyin. Proje bağlamını ve projeye özgü AGENTS.md talimatlarını koruyun. Kaynak commit bilgisini yenileyin ve etkilenen davranış senaryolarını deneyin.

## Taşınabilirlik

Ortak kurallar araç bağımsızdır. Cursor/Claude giriş dosyaları ve kurulum mekanizmaları bu sürümde sağlanmaz; ilgili aracın resmî dokümanına göre ayrıca uyarlanmalıdır. GitHub'da repo bulunması tek başına talimatları etkinleştirmez.

## PO–teknik yönetici düzeni (isteğe bağlı)

Önce [araştırma ve izin ayrımını](po-manager-research.md) ve [akışı](../../workflows/po-manager.md) okuyun. Bu dosyaları kopyalamak tek başına aktif sohbetin izinlerini değiştirmez.

1. Proje bağlamında bu akışın etkin olduğunu ve native alt ajan delegasyonunun yetkilendirildiğini kaydedin.
2. Proje AGENTS.md dosyasına ürün isteklerinde product-owner skill'ini kullanmasını, oradan tek technical-manager alt ajanına iş aktarmasını yazın. Yönetici olarak devredilmiş görevlerin tekrar PO oluşturmasını engelleyin.
3. [Teknik yönetici tanımını](technical-manager.toml) `.codex/agents/technical-manager.toml` yoluna alın. Model ayarı içermez; mevcut model tercihi korunur.
4. Otomatik izin incelemesi isteniyorsa [opt-in config](po-manager.config.toml) ayarlarını mevcut `.codex/config.toml` ile birleştirin. Bu çalışma ayrıca kullanıcı tarafından yetkilendirilmelidir. Full Access, özel allow-all hook veya global config değişikliği gerekmez.
5. Güvenilen proje katmanı ve yeni oturumdaki ajan/skill keşfini kontrol edin. Mevcut sohbet için izin seçicisindeki **Approve for me** durumunu ayrıca kontrol edin. Yönetilen politika yerel ayarların önündedir.
6. Sınırlı bir görevde gerçek aktarımı, analysis-only sınırını, review/test kanıtını ve Git kapsamını gözleyin. Parse kontrolünü uçtan uca davranış testi olarak sunmayın.

Native araç yoksa roller aynı sohbette sıralı yürütülür ve bu sınırlama raporlanır. İki ayrı sidebar sohbeti veya sürekli çalışan servis bu entegrasyon tarafından oluşturulmaz.

## Ayrı owner–orchestrator sohbetleri (isteğe bağlı)

Bu seçenek yukarıdaki native PO-manager topolojisinin alternatifidir. Önce [değerlendirmeyi](../../research/owner-orchestrator-review-2026-11.md) ve [ortak akışı](../../workflows/owner-orchestrator.md) okuyun. Playbook içindeki kaynak dosyaları hazırlamak aktif skill kurulumu veya çalışan sohbet çifti oluşturmak değildir.

1. `owner`, `orchestrator`, `product-owner`, `technical-manager` ve gereken doğrulama/review/Git skill'lerini `.agents/skills/` içine alın. Ortak workflow ve core belgeleri `.ai-playbook/` altında bulunmalıdır.
2. Proje bağlamında ayrı sohbet topolojisini seçin; PO-manager topolojisini aynı görev için kapatın. Owner ve orchestrator rol yönergeleri için [AGENTS şablonunu](AGENTS.template.md) kullanın.
3. Owner sohbetinde “Sen ownersın; bu proje için mevcut orchestrator'a bağlan, yoksa yeni bir sohbet oluştur ve görev boyunca onunla mesajlaş” deyin. Bu istek sohbet oluşturma/mesajlaşmayı yetkilendirir; uygulama, Git ve dış eylem kapsamı kendi gerçek yetkisine dayanır.
4. Owner'ın araçlarla doğru proje/peer kimliğini ve asenkron hazır yanıtını gözlediğini kontrol edin. Mesaj kaynağı doğrulanamıyorsa manuel aktarım sınırlaması raporlanmalıdır.
5. Önce küçük bir görevle gerçek karar sorusu, executor teslimatı, gerekli review ve kanıtla kabulü deneyin. Çift mesaj, iptal ve devam senaryolarını ayrıca gözleyin. İzin ayarları veya global config bu kurulumun parçası olarak değiştirilmez.

Skill keşfi desteklenen istemci ve doğru yerel konuma bağlıdır. “Sen ownersın” ifadesinin dosyalar kurulmamışken her sohbette otomatik tanınacağı veya kapalı uygulamada çalışmanın süreceği vaat edilmez.
