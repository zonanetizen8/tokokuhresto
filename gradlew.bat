@echo off
setlocal
set GRADLE_VERSION=9.3.1
set GRADLE_BASE=%USERPROFILE%\.gradle\tokokuh-wrapper
set GRADLE_HOME=%GRADLE_BASE%\gradle-%GRADLE_VERSION%
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  if not exist "%GRADLE_BASE%" mkdir "%GRADLE_BASE%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_BASE%\gradle-%GRADLE_VERSION%-bin.zip'; Expand-Archive -Force '%GRADLE_BASE%\gradle-%GRADLE_VERSION%-bin.zip' '%GRADLE_BASE%'"
)
call "%GRADLE_HOME%\bin\gradle.bat" -p "%~dp0" %*
endlocal
