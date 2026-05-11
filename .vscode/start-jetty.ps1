$proc = Start-Process `
    -FilePath ".\mvnw.cmd" `
    -ArgumentList "jetty:run" `
    -PassThru

Set-Content -Path ".vscode\jetty.pid" -Value $proc.Id

$ready = $false

for ($i = 0; $i -lt 240; $i++) {

    if ($proc.HasExited) {
        break
    }

    try {
        Invoke-WebRequest `
            -Uri "http://localhost:8080" `
            -UseBasicParsing `
            -TimeoutSec 1 | Out-Null

        Write-Output "JETTY_READY"

        $ready = $true
        break
    }
    catch {
    }

    Start-Sleep -Milliseconds 250
}

if (-not $ready) {

    if (-not $proc.HasExited) {
        taskkill /PID $proc.Id /T /F
    }

    throw "Jetty nao iniciou corretamente."
}

Wait-Process -Id $proc.Id