@echo off
setlocal

set "GS_SRC=Emulator"
set "JAR_DIR=builddir"
set "SKIPTEST="

:parse_args
if "%~1"=="" goto done_parse
if "%~1"=="-g" (
    set "BUILD_GS=1"
)
if "%~1"=="-c" (
    shift
    if /i "%~1"=="g" (
        set "CLEAN_GS=1"
    )
)
if "%~1"=="-o" (
    shift
    set "JAR_DIR=%~1"
)
if "%~1"=="-q" (
    set "SKIPTEST=-DskipTests"
)
shift
goto parse_args

:done_parse

if not defined BUILD_GS if not defined CLEAN_GS (
    echo [ERROR] Nothing to do. Use -g to build or -c g to clean.
    exit /b 1
)

if defined CLEAN_GS (
    echo [SCRIPT] Cleaning Game Server...
    pushd "%GS_SRC%"
    call mvnw clean || (
        echo [ERROR] Failed to clean Game Server
        exit /b 1
    )
    popd
)

if not exist "%JAR_DIR%" (
    mkdir "%JAR_DIR%"
)

if defined BUILD_GS (
    echo [SCRIPT] Building Game Server...
    pushd "%GS_SRC%"
    call mvnw package %SKIPTEST% || (
        echo [ERROR] Build failed
        exit /b 1
    )
    for %%F in (target\*-with-dependencies.jar) do (
        copy "%%F" "..\%JAR_DIR%\server.jar" >nul
        goto built
    )
    for %%F in (target\*.jar) do (
        copy "%%F" "..\%JAR_DIR%\server.jar" >nul
        goto built
    )
    echo [ERROR] No JAR file found
    exit /b 1
    :built
    popd
)

echo [SCRIPT] Done.
pause
