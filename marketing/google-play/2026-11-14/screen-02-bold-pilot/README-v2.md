# Screen 02 pilot refinement, v2

`02-store-tr-v2.png`: Turkish portrait PNG, **1080 × 1920**. `comparison-v1-v2.png` compares approved v1 (left) to refinement v2 (right). `thumbnail-v2.png`: 270 × 480 store-listing-size preview. V1 and its documentation remain unchanged.

## Requested refinement and result

The foreground 21-day card and lunge athlete are smaller together while retaining the floating outside-phone treatment, forest-green/lime styling and depth. The newly exposed phone area now visibly contains both the cream **Bölgesel Çalışma** card with **Bölgeni seç**, and blue **Isınma ve Nefes Egzersizi** card. Both remain secondary to the program card. The headline, subtitle and footer retain their exact correct Turkish copy and overall placement; the phone remains large and diagonal. No whole-image downscaling was used as the design edit.

The prompt specified approximately 13% foreground reduction as an artistic starting point. This was a generative raster edit, not editable layers or a measured geometric transform; the result is visually assessed rather than claiming an exact percentage or pixel-identical background. Minor phone-perspective redraw accompanies reconstruction of the exposed UI. No additional features or claims were introduced.

## Provenance

- Built-in `image_gen` EDIT target: `02-store-tr.png` (approved v1).
- Supporting reference only: `../../2026-11-13/store-final-6/tr/02-store.png` for accurate underlying cards.
- Verbatim edit prompt: `PROMPT-v2.md`.
- Generated output ID: `exec-e8ac0b20-5b4c-49d0-b162-0663fcb8cdaf.png`; preserved as `source-generation-v2.png`, 941 × 1672.
- `build-v2.cjs` fits proportionally to 1080 × 1920, makes comparison/thumbnail and `validation-v2.json`. It uses the existing bundled `sharp` module; set NODE_PATH to the bundled dependency directory before `node build-v2.cjs`. No API/CLI image-generation fallback, text repaint or deterministic raster object edit.

## Validation

Full final PNG, comparison and thumbnail visually inspected: smaller coherent foreground group; regional and warmup cards substantially more visible; program title/button still readable; athlete anatomy and edge breakout intact; exact main Turkish text/diacritics and footer pass; no pagination or watermark. Final dimensions checked; approved v1 SHA-256 remains **2b368f8aaedb50370d989c497d4920c1443f2eec6f4c4ec58aeb893e39d35115**. Full hash evidence is in `validation-v2.json`.

No files outside this pilot folder were written. No Android runtime/build work applies. Independent asset review and Git checkpoint are coordinated by the orchestrator.
