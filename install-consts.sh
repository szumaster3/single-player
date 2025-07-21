#!/bin/bash

set -e

echo "[SCRIPT] Installing 530-Consts JAR to local Maven repository..."

mvn install:install-file \
  -Dfile=530-Consts/target/530-Consts-1.0.0.jar \
  -DgroupId=org.rsps \
  -DartifactId=530-Consts \
  -Dversion=1.0.0 \
  -Dpackaging=jar \
  -DgeneratePom=true

echo "[SCRIPT] Done."
