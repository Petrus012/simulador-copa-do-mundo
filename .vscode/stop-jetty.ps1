$ErrorActionPreference = "SilentlyContinue"

if (Test-Path ".vscode\jetty.pid") {

    $jettyPid = Get-Content ".vscode\jetty.pid"

    taskkill /PID $jettyPid /T /F

    Remove-Item ".vscode\jetty.pid" -Force
}

Get-CimInstance Win32_Process |
    Where-Object {
        $_.CommandLine -and $_.CommandLine -match "jetty:run"
    } |
    ForEach-Object {
        taskkill /PID $_.ProcessId /T /F
    }