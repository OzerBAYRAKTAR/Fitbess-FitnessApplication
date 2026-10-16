# Kaynaklar ve güncellik

Son araştırma: 16 Ekim 2026. Özetler ve kararlar bu playbook'un tercihlerini temsil eder; resmî tavsiyeler proje ihtiyacına göre uyarlanır.

- [Android mimari önerileri](https://developer.android.com/topic/architecture/recommendations)
- [Android domain katmanı](https://developer.android.com/topic/architecture/domain-layer)
- [Modülerleştirme](https://developer.android.com/topic/modularization)
- [Coroutine uygulamaları](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
- [Kotlin kodlama kuralları](https://kotlinlang.org/docs/coding-conventions.html)
- [Android testleri](https://developer.android.com/studio/test/test-in-android-studio)
- [AGP ve araç uyumluluğu](https://developer.android.com/build/releases/about-agp)
- [Flutter mimari önerileri](https://docs.flutter.dev/app-architecture/recommendations)
- [Flutter integration testleri](https://docs.flutter.dev/testing/integration-tests)
- [Codex AGENTS.md](https://developers.openai.com/codex/guides/agents-md)
- [Codex skills](https://developers.openai.com/codex/skills)
- [OpenAI skill kullanım örneği](https://developers.openai.com/blog/skills-agents-sdk)
- [Codex review kuralları](https://developers.openai.com/blog/custom-code-review-rules-for-codex)
- [Google code review](https://google.github.io/eng-practices/review/reviewer/looking-for.html)
- [AI kodunun incelenmesi](https://docs.github.com/en/copilot/tutorials/review-ai-generated-code)

## Güncelleme yaklaşımı

Sürüm numaralarını kalıcı kurallara sabitlemeyin. Projenin gerçek araç sürümlerini proje bağlamında tutun; yükseltirken güncel resmî uyumluluk tablolarını doğrulayın.

Önemli platform/ajan değişikliklerinde veya pilotta ortaya çıkan somut sorunlarda ilgili kaynağı yeniden kontrol edin. Değişikliğin gerekçesini commit'te belirtin. Yönetilen kopyaları kendiliğinden güncellemeyin.

0.x sürümleri pilot niteliğindedir. Davranış değişiklikleri incelenerek alınır; biçim kontrolü ajan davranışını veya uygulama doğruluğunu garanti etmez.
