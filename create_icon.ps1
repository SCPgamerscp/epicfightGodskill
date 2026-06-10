$width = 128; $height = 128
Add-Type -AssemblyName System.Drawing
$bmp = New-Object System.Drawing.Bitmap($width, $height)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = 'AntiAlias'
$g.Clear([System.Drawing.Color]::Transparent)

# Draw golden circle background
$brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0,0)),
    (New-Object System.Drawing.Point(128,128)),
    [System.Drawing.Color]::FromArgb(255, 255, 215, 0),
    [System.Drawing.Color]::FromArgb(255, 255, 165, 0)
)
$g.FillEllipse($brush, 8, 8, 112, 112)

# Draw inner glow
$innerBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(200, 255, 255, 200))
$g.FillEllipse($innerBrush, 24, 24, 80, 80)

# Draw center
$centerBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
$g.FillEllipse($centerBrush, 48, 48, 32, 32)

# Draw star
$pen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 255, 255, 200), 3)
for ($i = 0; $i -lt 5; $i++) {
    $angle1 = [math]::PI * 2 * $i / 5 - [math]::PI / 2
    $angle2 = [math]::PI * 2 * (($i + 2) % 5) / 5 - [math]::PI / 2
    $x1 = 64 + [int](45 * [math]::Cos($angle1))
    $y1 = 64 + [int](45 * [math]::Sin($angle1))
    $x2 = 64 + [int](45 * [math]::Cos($angle2))
    $y2 = 64 + [int](45 * [math]::Sin($angle2))
    $g.DrawLine($pen, $x1, $y1, $x2, $y2)
}

$dir = 'c:\Users\ecrea\Downloads\epicfight\epicfightGodskill\src\main\resources\assets\epicfightgodskill\textures\gui\skills\passive'
New-Item -ItemType Directory -Path $dir -Force | Out-Null
$bmp.Save([System.IO.Path]::Combine($dir, 'godskill.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose()
$bmp.Dispose()
Write-Output 'Icon created successfully!'
