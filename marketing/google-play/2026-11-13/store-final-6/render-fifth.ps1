Add-Type -AssemblyName System.Drawing
$ErrorActionPreference='Stop'
$root=$PSScriptRoot
$raw=Join-Path $root '../../2026-11-05/phone-screenshots/raw'
$copy=@{
 tr=@(
 @('EVDE EGZERSİZ','21 günlük plan.','Kendi hızında.','Seviyeni seç, günlük programını takip et.','Üç seviye, tek uygulama','Başlangıç  •  Orta  •  İleri'),
 @('BÖLGESEL ANTRENMAN','Bölgeni seç.','Harekete geç.','Çalışmak istediğin bölgeye odaklan.','Sana uygun antrenmanı keşfet','Kol  •  Karın  •  Bel-boyun  •  Bacak'),
 @('GÜNLÜK PROGRAM','Günün planı','elinin altında.','Hareketleri ve süreyi başlamadan gör.','Hareketlerini önceden incele','Egzersiz listesi  •  Süre  •  Tahmini kalori'),
 @('HAREKETLİ REHBER','İzle. Uygula.','Devam et.','Animasyonlu gösterimlerle hareketi takip et.','Süreni takip et','Duraklat  •  Devam et  •  Hareket değiştir')
 )
 en=@(
 @('HOME WORKOUT','21-day plan.','Your own pace.','Choose your level and follow your daily plan.','Three levels. One app.','Beginner  •  Intermediate  •  Advanced'),
 @('TARGETED ROUTINES','Choose your focus.','Get moving.','Focus on the body area you want to train.','Discover your next workout','Arms  •  Abs  •  Back & neck  •  Legs'),
 @('DAILY WORKOUT','Your daily plan.','All in one place.','Preview your exercises and session duration.','Explore the movements','Exercise list  •  Duration  •  Estimated calories'),
 @('ANIMATED GUIDANCE','Watch. Move.','Keep going.','Follow each movement with animated guidance.','Keep track of time','Pause  •  Resume  •  Switch exercises')
 )
 de=@(
 @('TRAINING ZU HAUSE','21-Tage-Plan.','Dein Tempo.','Wähle dein Level und folge deinem Tagesplan.','Drei Stufen. Eine App.','Leicht  •  Mittel  •  Schwer'),
 @('GEZIELTE ÜBUNGEN','Wähle den Bereich.','Leg los.','Trainiere die Körperregion deiner Wahl.','Entdecke dein nächstes Training','Arme  •  Bauch  •  Rücken & Nacken  •  Beine'),
 @('DEIN TAGESPLAN','Dein Training.','Alles im Blick.','Sieh dir Übungen und Dauer vor dem Start an.','Entdecke die Bewegungen','Übungen  •  Dauer  •  Geschätzte Kalorien'),
 @('ANIMIERTE ANLEITUNG','Ansehen. Bewegen.','Dranbleiben.','Folge den animierten Bewegungsabläufen.','Behalte die Zeit im Blick','Pausieren  •  Fortsetzen  •  Übung wechseln')
 )
}
function Col($v){[System.Drawing.ColorTranslator]::FromHtml($v)}
function Round([single]$x,[single]$y,[single]$w,[single]$h,[single]$r){
 $p=[System.Drawing.Drawing2D.GraphicsPath]::new();$d=2*$r
 $p.AddArc($x,$y,$d,$d,180,90);$p.AddArc($x+$w-$d,$y,$d,$d,270,90);$p.AddArc($x+$w-$d,$y+$h-$d,$d,$d,0,90);$p.AddArc($x,$y+$h-$d,$d,$d,90,90);$p.CloseFigure();return $p
}
function FillRound($g,$color,$x,$y,$w,$h,$r){$b=[System.Drawing.SolidBrush]::new((Col $color));$p=Round $x $y $w $h $r;try{$g.FillPath($b,$p)}finally{$p.Dispose();$b.Dispose()}}
function Text($g,[string]$text,$x,$y,$size,$color,[bool]$bold=$false,$max=950){
 do{$f=[System.Drawing.Font]::new('Segoe UI',$size,$(if($bold){[System.Drawing.FontStyle]::Bold}else{[System.Drawing.FontStyle]::Regular}),[System.Drawing.GraphicsUnit]::Pixel);$width=$g.MeasureString($text,$f).Width;if($width -gt $max){$f.Dispose();$size-=1}}while($width -gt $max -and $size -gt 18)
 if($width -gt $max){throw "Text overflow $text"};$b=[System.Drawing.SolidBrush]::new((Col $color));try{$g.DrawString($text,$f,$b,[single]$x,[single]$y)}finally{$b.Dispose();$f.Dispose()}
}
function CropCard($g,$im,$sx,$sy,$sw,$sh,$x,$y,$w,$radius=34,$angle=0){
 $h=[single]($sh*$w/$sw)
 if($sx -lt 0 -or $sy -lt 0 -or ($sx+$sw) -gt $im.Width -or ($sy+$sh) -gt $im.Height){throw 'Invalid crop'}
 $state=$g.Save();$g.TranslateTransform([single]($x+$w/2),[single]($y+$h/2));$g.RotateTransform([single]$angle);$g.TranslateTransform([single](-$w/2),[single](-$h/2))
 try{
  foreach($step in @(18,12,6)){$shadow=[System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(11,0,26,16));$p=Round (-$step) (14-$step) ($w+2*$step) ($h+2*$step) ($radius+$step);$g.FillPath($shadow,$p);$p.Dispose();$shadow.Dispose()}
  FillRound $g '#FFFFFF' -8 -8 ($w+16) ($h+16) ($radius+8)
  $p=Round 0 0 $w $h $radius;$g.SetClip($p)
  $g.DrawImage($im,[System.Drawing.RectangleF]::new(0,0,$w,$h),[System.Drawing.RectangleF]::new($sx,$sy,$sw,$sh),[System.Drawing.GraphicsUnit]::Pixel)
  $p.Dispose()
 }finally{$g.Restore($state)}
}
function DotLabel($g,$number,$label,$x,$y,$color,$light=$false){
 FillRound $g '#B9EA69' $x $y 50 50 25
 Text $g $number ($x+14) ($y+5) 27 '#143F32' $true 40
 Text $g $label ($x+67) ($y+3) 31 $color $true 850
}

# Native template derived from phone-final-6/render-ui.ps1. Only page 5 is rendered.
foreach($lang in @('tr','en','de')){
 $dest=Join-Path $root $lang;New-Item -ItemType Directory -Force $dest|Out-Null
 $day=[System.Drawing.Image]::FromFile((Join-Path $raw "discovery-verification/store-day-$lang.png"))
 $bmp=[System.Drawing.Bitmap]::new(1080,1920,[System.Drawing.Imaging.PixelFormat]::Format24bppRgb);$g=[System.Drawing.Graphics]::FromImage($bmp)
 try{
  $g.SmoothingMode=[System.Drawing.Drawing2D.SmoothingMode]::AntiAlias;$g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic;$g.TextRenderingHint=[System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
  $i=2;$fg='#113D30';$sub='#526A5E'
  $grad=[System.Drawing.Drawing2D.LinearGradientBrush]::new([System.Drawing.Rectangle]::new(0,0,1080,1920),(Col '#FBF8EE'),(Col '#D7E9D8'),65);$g.FillRectangle($grad,0,0,1080,1920);$grad.Dispose()
  $ring=[System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(22,180,229,120),80);$g.DrawEllipse($ring,640,740,700,1100);$ring.Dispose()
  $sweep=[System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(24,200,238,170),12);$g.DrawArc($sweep,-450,1120,1700,1500,210,170);$sweep.Dispose()
  Text $g $copy[$lang][$i][0] 64 64 26 $sub $true 830
  Text $g $copy[$lang][$i][1] 56 145 94 $fg $true 960
    if($lang -eq 'de'){
   $path=[System.Drawing.Drawing2D.GraphicsPath]::new();$family=[System.Drawing.FontFamily]::new('Segoe UI');$brush=[System.Drawing.SolidBrush]::new((Col $fg))
   $path.AddString('Alles im Blick.',$family,[int][System.Drawing.FontStyle]::Bold,94,[System.Drawing.PointF]::new(56,252),[System.Drawing.StringFormat]::GenericDefault);$g.FillPath($brush,$path);$path.Dispose();$family.Dispose();$brush.Dispose()
  }else{Text $g $copy[$lang][$i][2] 56 252 94 $fg $true 960}
  FillRound $g '#B9EA69' 66 375 178 9 4
  Text $g $copy[$lang][$i][3] 64 421 34 $sub $false 950
  CropCard $g $day 53 341 974 263 62 560 956 40 -2
  # Three distinct metrics retain the complete workout totals.
  FillRound $g '#D1DDCC' 91 803 906 253 40
  FillRound $g '#FFFFFF' 83 790 914 253 40
  $labels=@{tr=@('hareket','dakika','tahmini kcal');en=@('exercises','minutes','estimated kcal');de=@('Übungen','Minuten','geschätzte kcal')}[$lang]
  $values=@('8','7','113')
  for($m=0;$m -lt 3;$m++){
   $x=103+$m*294
   FillRound $g '#F0F5E9' $x 810 276 213 26
   Text $g $values[$m] ($x+25) 830 68 $fg $true 230
   Text $g $labels[$m] ($x+25) 934 27 $sub $false 234
  }
  DotLabel $g '5' $copy[$lang][$i][4] 70 1100 $fg
  $names=@('Fire Hydrants','Bird Dog');$files=@('fire-hydrants.png','bird-dog.png')
  for($n=0;$n -lt 2;$n++){
   $x=70+$n*25;$y=1200+$n*285
   FillRound $g '#D0DCCF' ($x+4) ($y+13) 930 242 40
   FillRound $g '#FFFFFF' $x $y 930 242 40
   $photo=[System.Drawing.Image]::FromFile((Join-Path $root "assets/$($files[$n])"))
   try{
    $state=$g.Save();$clip=Round ($x+18) ($y+20) 350 202 25;$g.SetClip($clip)
    $g.DrawImage($photo,[System.Drawing.Rectangle]::new(($x+18),($y+20),350,202));$g.Restore($state);$clip.Dispose()
   }finally{$photo.Dispose()}
   Text $g ('0'+($n+1)) ($x+399) ($y+39) 28 '#62815E' $false 100
   Text $g $names[$n] ($x+397) ($y+95) 43 $fg $true 465
   Text $g '›' ($x+861) ($y+78) 63 '#198359' $false 60
  }
  FillRound $g '#EAF1E4' 54 1840 972 54 27
  Text $g $copy[$lang][$i][5] 80 1849 25 $fg $true 920
  $bmp.Save((Join-Path $dest '05-store.png'),[System.Drawing.Imaging.ImageFormat]::Png)
 }finally{$g.Dispose();$bmp.Dispose();$day.Dispose()}
}
