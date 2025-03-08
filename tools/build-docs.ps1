$refinedSitesVersion = "0.4.1"

Remove-Item -Recurse -Force output 2>$null

if (-Not (Test-Path "refinedsites-$refinedSitesVersion-all.jar")) {
    Write-Host "Downloading Refined Sites $refinedSitesVersion"
    Invoke-WebRequest -Uri "https://github.com/refinedmods/refinedsites/releases/download/v$refinedSitesVersion/refinedsites-$refinedSitesVersion-all.jar" -OutFile "refinedsites-$refinedSitesVersion-all.jar"
}

java -jar "refinedsites-$refinedSitesVersion-all.jar" . playbook.json

Start-Process "output/refined-storage/index.html"