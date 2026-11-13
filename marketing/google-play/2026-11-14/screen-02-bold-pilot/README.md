# Turkish screen 02: bold program-card pilot

One new Turkish-only promotional creative. Final `02-store-tr.png` is **1080 × 1920**. `before-after-tr.png` shows unchanged source on the left and pilot on the right; `thumbnail-tr.png` is the 270 × 480 readability preview. This is a marketing composition, not an application UI implementation. Screens 3–5 and existing source assets are untouched.

## Design

Huge forest-green **21 Günlük Program** card floats separately in front of the narrower diagonal phone and extends beyond both phone edges. The athlete breaks above and below the card. Cream backdrop, lime calendar/button and dimensional shadows carry the Fitbess campaign language. Supporting UI stays recognizable but secondary. Copy presents only the existing 21-day program and choice of level; no new functionality, ratings, transformation or weight-loss claims.

Exact main copy: **21 günlük / planın hazır**; **Seviyeni seç, günlük planını takip et.** Card: **21 Günlük Program**, **Seviyeni seç**. Footer: **Başlangıç • Orta • İleri**.

## Provenance and reproduction

- Reference: `../../2026-11-13/store-final-6/tr/02-store.png` (unaltered). SHA-256 baseline and post-production hashes match in `validation.json`.
- Built-in `image_gen` generated the redesigned raster with that local image as reference. Full exact prompt is in `PROMPT.md`. No API/CLI generation fallback.
- Generated output ID: `exec-2b0996fa-1235-401a-8c9e-1da7f53e545a.png`, saved here as `source-generation.png` (916 × 1717).
- `build.cjs` uses the existing bundled Node `sharp` library to fit proportionally into 1080 × 1920 with sampled cream side padding; no image content cropped, text repainted or anatomy distorted. It also makes comparison/thumbnail and validation metadata.
- To reproduce: resolve bundled Node dependencies with desktop workspace-dependencies tool, set NODE_PATH to that node_modules directory, then run `node build.cjs`. No repository dependency or machine path added.

## Observed visual research

Orchestrator visually inspected current Google Play listing carousels on **2026-11-14**. These are selected fitness examples, not a global ranking. No competitor images were copied into the asset.

- [Home Workout – No Equipment](https://play.google.com/store/apps/details?hl=en_US&id=homeworkout.homeworkouts.noequipment): short oversized headlines, high contrast orange/red accents, large athletes overlapping calendar/program cards; benefit dominates literal phone.
- [Workout for Women](https://play.google.com/store/apps/details?hl=en-US&id=loseweightapp.loseweightappforwomen.womenworkoutathome): dark/pink typography hierarchy, program calendars, floating metric overlay in Track Progress creative.
- [Fitify](https://play.google.com/store/apps/details?id=com.fitifyworkouts.bodyweight.workoutapp): diagonal phones crossing screenshot edges, dark/lime palette, floating white exercise tile. Promotional audience numbers were not adopted.

Fitbess adaptation uses the layered hierarchy, floating card and athletic cutout principle in its own cream/forest/lime language; no competitor branding or unsupported claims.

## Validation

Final PNG, 270 × 480 thumbnail and before/after were visually inspected. Main Turkish spelling and diacritics, card hierarchy, clean anatomy, full frame safe margins, no upper-right pagination, and source hash preservation pass. `validation.json` records source/final hashes and actual dimensions. Android build/runtime checks do not apply to this asset-only pilot. Independent asset review and working-branch checkpoint are coordinated by the orchestrator.
