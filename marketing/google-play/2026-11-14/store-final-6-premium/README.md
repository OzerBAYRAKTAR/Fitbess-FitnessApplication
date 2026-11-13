# Premium six-image localized Google Play set

Final deliverables: `tr/`, `en/`, `de/`, each `01-store.png` through `06-store.png` at **1080 × 1920**. Original source sets and approved screen-02 pilot are preserved.

## Source mapping and methods

| Order | Source / treatment |
|---|---|
| 1 | Exact byte copy of `../../2026-11-13/store-final-6/{locale}/01-store.png` |
| 2 TR | Exact byte copy of `../screen-02-bold-pilot/02-store-tr-v2.png` |
| 2 EN/DE | Built-in imagegen language-only edits of approved TR v2 composition |
| 3 | Existing localized `../../2026-11-13/store-final-6/{locale}/03-store.png` content; new TR dimensional three-level cascade, EN/DE localized from new TR |
| 4 | Existing localized `../../2026-11-13/store-final-6/{locale}/04-store.png` content; new TR premium regional dashboard with breakout athlete, EN/DE localized from new TR |
| 5 | Existing `8 exercises / 7 min / 113 estimated kcal` facts and genuine AI Fire Hydrants/Bird Dog pose references; new dimensional floating dashboard and exercise cards, EN/DE localized from new TR |
| 6 | Exact byte copy of `../../2026-11-13/store-final-6/{locale}/06-store.png` |

## Art direction and truthfulness

Cream/ivory, forest green and lime remain consistent. Screen 2 retains the approved smaller foreground group and visible regional/warmup cards. Screen 3 stages three large diagonal overlapping program cards with athletes breaking their edges. Screen 4 uses thick dimensional selector framing, four region tiles and a projecting selected Back card. Screen 5 uses sculpted elevated metric modules, distinct activity/time/flame symbols, premium lighting and two substantial exercise previews. A clock decoration is not a progress indicator; no completion percentage or new performance metric is shown. Two previews do not change the total workout count of eight. Calorie figure remains explicitly estimated.

No new application features, ratings or fitness promises were introduced. Level chips 1–3 and calendar21 are product content, not upper-right pagination. Existing first/sixth originals are copied as requested.

## Reproduction and evidence

- `prompts/*.txt`: exact built-in imagegen production/localization prompts.
- `generation-log.json`: generation output IDs, prompt references and source roles.
- `generated/{locale}-{order}.png`: original selected built-in raster outputs before standardization.
- `assets/fire-hydrants.png`, `assets/bird-dog.png`: preserved genuine AI exercise references from the previous finalized set. Original built-in outputs were exec-46cc26c1-9666-4c23-92df-86cfc03090ce.png and exec-595c4411-5c80-48f8-b599-fb11226c0875.png; their original prompts remain in the prior set README.
- `build.cjs`: uses existing bundled Node `sharp` to proportionally contain generated raster into1080×1920, sampled background padding if necessary, no crop/text repaint or arbitrary object raster edit. Resolve bundled node_modules with desktop workspace-dependencies tool, set NODE_PATH and run `node build.cjs --final`.
- `validation.json`: exact per-image source mapping, source/output SHA-256, dimensions, method and byte identity checks.
- `preview-final-{tr,en,de}.{png,jpg}`: ordered six-image contact sheets.
- `comparison-final-{locale}-{02,03,04,05}.png`: source left, new creative right. TR02 comparison is deliberately identical because approved v2 is the source.

Built-in imagegen was used; no CLI/API generation fallback or Python image editing. Generative localized/new compositions are assessed visually, with no false claim of pixel-identical invariant backgrounds. Hash equality applies only to the seven exact-copy deliverables. This folder contains marketing artifacts only; no Android code, emulator interaction, publication or Play Console upload.
