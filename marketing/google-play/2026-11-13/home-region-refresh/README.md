# Home and regional workout store images

Six 1080×1920 PNG files: `tr`, `en`, `de`, each with `01-home.png` and `02-regions.png`. No page numbers; existing campaign images were preserved. These are promotional compositions anchored to the actual application UI, not additional application screens. Not uploaded to Play Console.

Created with built-in imagegen, then proportionally resized and padded to 1080×1920 (no UI/text repainting). Original generated images remain under `C:/Users/BAYRAKTAR/.codex/generated_images/01a1d504-02b4-72f0-8372-550818f70f6a/`.

## Prompt set

Common production prompt: Create one finished portrait Google Play promotional screenshot, 1080×1920. Use the supplied actual localized app screenshot as a faithful UI anchor. Premium cream ivory background, forest-green bold clean typography, subtle sage ribbon, lime underline, rounded dark-green smartphone frame and soft shadow. Headline and smaller subtitle above a large phone. Preserve wording, controls, woman imagery and arrangement; do not invent features or replace the UI with a generic poster. No page numbers, ratings, badges, weight-loss promises or watermark. Readable exact language accents.

Home: show motivational home header, dominant green 21-day card, regional card and blue wellness card in original hierarchy; bottom may be cropped as the real scrollable screen, preserve bottom navigation.

Regions: show standing woman left, four photo region cards right, selected Back card and existing workout list below.

| Language/type | Headline | Subtitle | Footer | Source output |
|---|---|---|---|---|
| TR/home | 21 günlük planın hazır | Seviyeni seç, günlük planını takip et. | Başlangıç • Orta • İleri | exec-f76c0a86-d963-4f34-82fd-75ec3dd70466.png |
| TR/regions | Bölgeni seç. Harekete geç. | Çalışmak istediğin bölgeye odaklan. | Kollar • Sırt • Karın • Bacaklar ve Popo | exec-6569ed01-8b36-4cbc-add7-cb145ebd7528.png |
| EN/home | Your 21-day plan starts here | Choose your level. Follow your daily plan. | Beginner • Intermediate • Advanced | exec-629b393b-b5c3-4208-88cf-a107e77c1512.png |
| EN/regions | Choose your focus | Train the area you want to work on. | Arms • Back • Abs • Legs & Glutes | exec-9027168f-1686-4348-8af2-523cfcbce472.png |
| DE/home | Dein 21-Tage-Plan | Wähle dein Level. Folge deinem Tagesplan. | Anfänger • Mittelstufe • Fortgeschritten | exec-0488417e-d407-46e9-a183-e1f95d2befe2.png |
| DE/regions | Wähle deinen Fokus | Trainiere die Körperregion deiner Wahl. | Arme • Rücken • Bauch • Beine & Gesäß | exec-1f5a9a21-e7a7-4d6f-95ba-46f9680a9086.png |

UI references: `.verification/home-refresh/final-normal/home-{tr,en,de}.png`, `region-{tr,en,de}.png`. TR region was generated first; remaining five used it as the campaign style reference. All six outputs visually inspected for spelling, actual UI fidelity and composition.
