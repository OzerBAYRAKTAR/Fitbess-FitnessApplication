const fs=require('fs');const path=require('path');const crypto=require('crypto');
const sharp=require('sharp');
const root=__dirname;const dirs=['../../2026-11-06/phone-revisions-v2','../home-region-refresh','../../2026-11-06/phone-revisions-v2','../home-region-refresh','../../2026-11-05/phone-final-6','../../2026-11-05/phone-final-6'];const names=['01-store.png','01-home.png','02-store.png','04-store.png','05-store.png','06-store.png'];
const hash=p=>crypto.createHash('sha256').update(fs.readFileSync(p)).digest('hex');
(async()=>{let records=[];for(const locale of ['tr','en','de']){let thumbnails=[];for(let i=0;i<6;i++){
const source=path.resolve(root,dirs[i],locale,names[i]);const output=path.join(root,locale,`${String(i+1).padStart(2,'0')}-store.png`);
const a=await sharp(source).ensureAlpha().raw().toBuffer({resolveWithObject:true});const b=await sharp(output).ensureAlpha().raw().toBuffer({resolveWithObject:true});if(a.info.width!==b.info.width||a.info.height!==b.info.height)throw Error('Dimensions differ');
let changed=0,outside=0;let rect=(locale==='en'&&i===3)?[850,43,976,102]:[888,46,1024,116];
for(let y=0;y<b.info.height;y++)for(let x=0;x<b.info.width;x++){let p=(y*b.info.width+x)*4;if(a.data[p]!==b.data[p]||a.data[p+1]!==b.data[p+1]||a.data[p+2]!==b.data[p+2]||a.data[p+3]!==b.data[p+3]){changed++;let ok=x>=rect[0]&&x<=rect[2]&&y>=rect[1]&&y<=rect[3];if(i===4)ok=ok||(x>=35&&x<=1045&&y>=720&&y<=1075)||(x>=35&&x<=1045&&y>=1160&&y<=1770)||(locale==='de'&&x>=50&&x<=760&&y>=250&&y<=365);if(!ok)outside++;}}
records.push({locale,order:i+1,source:path.relative(root,source).replaceAll('\\','/'),output:path.relative(root,output).replaceAll('\\','/'),width:b.info.width,height:b.info.height,sourceSHA256:hash(source),outputSHA256:hash(output),changedPixels:changed,pixelsOutsideAllowedRegions:outside,badgeRegion:rect});
thumbnails.push({input:await sharp(output).resize(270,480).png().toBuffer(),left:i*270,top:0});
}await sharp({create:{width:1620,height:480,channels:3,background:'#ffffff'}}).composite(thumbnails).png().toFile(path.join(root,`preview-final-${locale}.png`));}
fs.writeFileSync(path.join(root,'validation.json'),JSON.stringify(records,null,2)+'\n');console.table(records.map(({locale,order,width,height,changedPixels,pixelsOutsideAllowedRegions})=>({locale,order,width,height,changedPixels,pixelsOutsideAllowedRegions})));if(records.some(x=>x.pixelsOutsideAllowedRegions))process.exitCode=1;
})();
