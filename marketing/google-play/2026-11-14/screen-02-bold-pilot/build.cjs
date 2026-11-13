const fs=require('fs'),path=require('path'),crypto=require('crypto'),sharp=require('sharp');
const root=__dirname,source=path.resolve(root,'../../2026-11-13/store-final-6/tr/02-store.png');
const hash=p=>crypto.createHash('sha256').update(fs.readFileSync(p)).digest('hex');
(async()=>{
const generated=path.join(root,'source-generation.png'),final=path.join(root,'02-store-tr.png');
const meta=await sharp(generated).metadata();const rgb=await sharp(generated).extract({left:0,top:0,width:1,height:1}).removeAlpha().raw().toBuffer();
await sharp(generated).resize(1080,1920,{fit:'contain',background:{r:rgb[0],g:rgb[1],b:rgb[2]}}).png().toFile(final);
await sharp(final).resize(270,480).png().toFile(path.join(root,'thumbnail-tr.png'));
const before=await sharp(source).resize(540,960).toBuffer(),after=await sharp(final).resize(540,960).toBuffer();
await sharp({create:{width:1080,height:960,channels:3,background:'#faf4e9'}}).composite([{input:before,left:0,top:0},{input:after,left:540,top:0}]).png().toFile(path.join(root,'before-after-tr.png'));
fs.writeFileSync(path.join(root,'validation.json'),JSON.stringify({source:'../../2026-11-13/store-final-6/tr/02-store.png',sourceSHA256:hash(source),expectedSourceSHA256:'949819476d8d3fb0e069cdbb837d85962ab08d8f4c1e423b271ca7b432ae4d1f',sourceUnchanged:hash(source)==='949819476d8d3fb0e069cdbb837d85962ab08d8f4c1e423b271ca7b432ae4d1f',generatedDimensions:{width:meta.width,height:meta.height},finalDimensions:{width:1080,height:1920},finalSHA256:hash(final),resize:'proportional contain; sampled cream corner padding; no crop, text repaint or perspective adjustment',locale:'tr',imagegenOutputId:'exec-2b0996fa-1235-401a-8c9e-1da7f53e545a.png'},null,2)+'\n');console.log(fs.readFileSync(path.join(root,'validation.json'),'utf8'));
})();
