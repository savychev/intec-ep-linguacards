Write-Host "=== Maven / Java Diagnostic ==="

Write-Host "\n[1/3] mvn -v"
mvn -v

Write-Host "\n[2/3] JAVA_HOME (cmd + PowerShell forms)"
cmd /c "echo %JAVA_HOME%"
Write-Host "$env:JAVA_HOME"

Write-Host "\n[3/3] Resolve dependencies with force update"
mvn -U -e -DskipTests dependency:resolve
