# Localized six-image Google Play set

18 final PNGs in `tr/`, `en/`, `de/`, numbered `01-store.png` through `06-store.png`. Each is 1080 × 1920. Original files remain unchanged. No Play Console upload or application change.

## Source mapping

Paths below are relative to `marketing/google-play/`; substitute `{locale}` with tr, en or de.

| Final order | Source |
|---|---|
| 1 | 2026-11-06/phone-revisions-v2/{locale}/01-store.png |
| 2 | 2026-11-13/home-region-refresh/{locale}/01-home.png |
| 3 | 2026-11-06/phone-revisions-v2/{locale}/02-store.png |
| 4 | 2026-11-13/home-region-refresh/{locale}/04-store.png |
| 5 | 2026-11-05/phone-final-6/{locale}/05-store.png |
| 6 | 2026-11-05/phone-final-6/{locale}/06-store.png |

Images 1, 3, 4 and 6: only upper-right pagination area reconstructed using horizontal per-row boundary-color interpolation. User explicitly authorized code-based number removal. Images 2 had no pagination and were copied byte-for-byte. `remove-pagination.ps1` records exact rectangles; EN image 4 uses its actual shifted badge location. Outside these rectangles every pixel is identical to the source.

Image 5: rebuilt from its original editable `phone-final-6/render-ui.ps1` native template. Preserves headline, subtitle, hero photo, palette, backdrop and footer; removes upper-right pagination. Redesigns summary and two preview rows, using genuine AI raster assets. Summary remains **8 exercises / 7 minutes / 113 estimated kcal**; two displayed entries are previews, not the total exercise count. Original exercise names Fire Hydrants and Bird Dog remain unchanged in all three locales. DE headline explicitly rendered as **Dein Training. / Alles im Blick.** with native text paths to guarantee complete lettering. In-panel lime section numbers are original content and remain.

## Generation and reproduction

Built-in `image_gen` used for `assets/fire-hydrants.png` and `assets/bird-dog.png`; no API or CLI image-generation fallback. Native PowerShell/System.Drawing template composes the new fifth image with original localized typography. Run `render-fifth.ps1` to render fifth creatives, `remove-pagination.ps1` for the other numbered sources, and copy home sources to final 02. No Python image editing.

`validate.cjs` uses the bundled Node.js `sharp` package. Resolve the bundled node_modules via the desktop workspace-dependencies tool and set NODE_PATH accordingly before running `node validate.cjs`. It produces `validation.json` with all 18 source/output SHA-256 values, dimensions and exact pixel comparisons, plus `preview-final-{tr,en,de}.png` contact sheets. No repository dependency added.

## Validation

All 18 PNGs are 1080 × 1920. Each of the three final contact sheets and all full-size fifth creatives were visually inspected. Correct order, complete locale copy, readable workout totals, credible exercise poses and no upper-right pagination confirmed. Pixel comparison: **zero changed pixels outside authorized regions** in all 18 outputs; all three home images have zero changed pixels overall. Fifth image allowed regions cover the summary, two exercise rows, upper-right badge, and the DE second headline line. All other pixels retain the original composition.

Mandatory asset-scope code review is coordinated separately by the orchestrator; final Git evidence is reported in chat. No Android build/runtime verification applies to these marketing-only assets.

## Prompts (verbatim)

### Fire Hydrants

Use case: ads-marketing. Create a single premium photorealistic exercise demonstration photograph for a fitness app thumbnail: one adult athletic woman in sage-green fitted workout clothes on a forest-green yoga mat, warm cream home studio background, soft daylight, tasteful plant softly out of focus. Show FIRE HYDRANT exercise accurately, three-quarter front-side view with complete body visible: both hands planted beneath shoulders, one knee grounded beneath hip, other thigh lifted sideways away from body with that knee bent approximately 90 degrees, spine neutral and pelvis stable. Pose must clearly show the lateral raised bent knee, not a donkey kick. Anatomically credible two arms and two legs, natural joints and fingers. Landscape composition with full subject centered and generous margin for rounded-card crop. No text, no labels, no logos, no arrows, no collage.

Built-in generation output ID: exec-46cc26c1-9666-4c23-92df-86cfc03090ce.png.

### Bird Dog

Use case: ads-marketing. Create a single premium photorealistic exercise demonstration photograph for a fitness app thumbnail: one adult athletic woman with brown hair in a bun, sage-green fitted workout clothes and white sneakers on a forest-green yoga mat, warm cream home studio background, soft daylight and a tasteful plant. Show BIRD DOG exercise accurately in clear side view with complete body visible: right hand and left knee planted beneath shoulder and hip, left arm reaching straight forward at shoulder height and right leg extended straight backward at hip height, spine neutral and hips level. Opposite arm and leg extended, two arms and two legs anatomically natural, hands and feet visible. Landscape 16:9 composition with generous margin around whole body for rounded-card crop. No text, no labels, no logos, no arrows, no collage.

Built-in generation output ID: exec-595c4411-5c80-48f8-b599-fb11226c0875.png.
