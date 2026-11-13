const fs=require('fs'),path=require('path'),crypto=require('crypto'),sharp=require('sharp');
const root=__dirname;const hash=p=>crypto.createHash('sha256').update(fs.readFileSync(p)).digest('hex');
(async()=>{
const v1=path.join(root,'02-store-tr.png'),generated=path.join(root,'source-generation-v2.png'),final=path.join(root,'02-store-tr-v2.png');
const expected='2b368f8aaedb50370d989c497d4920c1443f2eec6f4c4ec58aeb893e39d35115';if(hash(v1)!==expected)throw Error('Approved v1 changed');
const meta=await sharp(generated).metadata(),rgb=await sharp(generated).extract({left:0,top:0,width:1,height:1}).removeAlpha().raw().toBuffer();
await sharp(generated).resize(1080,1920,{fit:'contain',background:{r:rgb[0],g:rgb[1],b:rgb[2]}}).png().toFile(final);
await sharp(final).resize(270,480).png().toFile(path.join(root,'thumbnail-v2.png'));
const before=await sharp(v1).resize(540,960).toBuffer(),after=await sharp(final).resize(540,960).toBuffer();
await sharp({create:{width:1080,height:960,channels:3,background:'#faf4e9'}}).composite([{input:before,left:0,top:0},{input:after,left:540,top:0}]).png().toFile(path.join(root,'comparison-v1-v2.png'));
const validation={v1:'02-store-tr.png',v1SHA256:hash(v1),v1Unchanged:hash(v1)===expected,generatedDimensions:{width:meta.width,height:meta.height},finalDimensions:{width:1080,height:1920},finalSHA256:hash(final),resize:'proportional contain with sampled cream padding if needed; no crop or text repaint',locale:'tr',editTool:'built-in image_gen',prompt:'PROMPT-v2.md',note:'AI raster edit; preservation assessed visually, no pixel-identity claim for regenerated areas'};
fs.writeFileSync(path.join(root,'validation-v2.json'),JSON.stringify(validation,null,2)+'\n');console.log(JSON.stringify(validation,null,2));
})();
