Add-Type -AssemblyName System.Drawing
$root=$PSScriptRoot
$inputs=@{
 '01-store.png'='../../2026-11-06/phone-revisions-v2/{0}/01-store.png'
 '03-store.png'='../../2026-11-06/phone-revisions-v2/{0}/02-store.png'
 '04-store.png'='../home-region-refresh/{0}/04-store.png'
 '06-store.png'='../../2026-11-05/phone-final-6/{0}/06-store.png'
}
# Minimal rectangle enclosing pagination and anti-aliased edge. No other pixels are painted.
foreach($lang in @('tr','en','de')){foreach($entry in $inputs.GetEnumerator()){
 $src=Join-Path $root ($entry.Value -f $lang);$im=[System.Drawing.Bitmap]::new($src)
 try{
  $x0=888;$x1=1024;$y0=46;$y1=116
  if($lang -eq 'en' -and $entry.Key -eq '04-store.png'){$x0=850;$x1=976;$y0=43;$y1=102}
  for($y=$y0;$y -le $y1;$y++){
   $a=$im.GetPixel(($x0-1),$y);$b=$im.GetPixel(($x1+1),$y)
   for($x=$x0;$x -le $x1;$x++){
    $t=($x-$x0+1)/[double]($x1-$x0+2)
    $r=[int][Math]::Round($a.R*(1-$t)+$b.R*$t);$g=[int][Math]::Round($a.G*(1-$t)+$b.G*$t);$bl=[int][Math]::Round($a.B*(1-$t)+$b.B*$t)
    $im.SetPixel($x,$y,[System.Drawing.Color]::FromArgb($r,$g,$bl))
   }
  }
  $im.Save((Join-Path $root "$lang/$($entry.Key)"),[System.Drawing.Imaging.ImageFormat]::Png)
 }finally{$im.Dispose()}
}}
