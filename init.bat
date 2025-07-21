@echo off
setlocal enabledelayedexpansion
echo [SCRIPT] Initializing git submodules...
git submodule update --init --recursive

echo [SCRIPT] Building 530-Consts...
cd 530-Consts
call mvn clean package -DskipTests

echo [SCRIPT] Installing 530-Consts JAR to local Maven repository...
call mvn install:install-file ^
  -Dfile=target\530-Consts-1.0.0.jar ^
  -DgroupId=org.rsps ^
  -DartifactId=530-Consts ^
  -Dversion=1.0.0 ^
  -Dpackaging=jar ^
  -DgeneratePom=true

cd ..

echo [SCRIPT] Building Emulator...
cd Emulator
call mvn clean install -DskipTests

cd ..
echo [SCRIPT] Initialization complete.
pause
