#!/usr/bin/env bash

set -e

SCRIPT_DIR=$(cd -- "$(dirname "$0")" >/dev/null 2>&1 && pwd -P)

BUILD_GS=0
CLEAN_GS=0
SKIPTEST=""
JAR_DIR="$SCRIPT_DIR/builddir"
GS_SRC="$SCRIPT_DIR/Emulator"

help() {
    echo "Usage: $0 [-h] [-g] [-c] [-o <dir>] [-q] [-d]"
    echo "  -h    Show this help message"
    echo "  -g    Build Game Server (Emulator)"
    echo "  -c    Clean Game Server"
    echo "  -o    Output directory for built .jar file (default: builddir/)"
    echo "  -q    Quick build (skip tests)"
    echo "  -d    Debug print"
}

error() {
    echo "[ERROR] $1" >&2
    exit 1
}

clean_gs() {
    echo "[SCRIPT] Cleaning Game Server..."
    cd "$GS_SRC" || error "Directory not found: $GS_SRC"
    ./mvnw clean || error "Failed to clean Game Server"
}

build_gs() {
    echo "[SCRIPT] Building Game Server..."
    cd "$GS_SRC" || error "Directory not found: $GS_SRC"
    ./mvnw package $SKIPTEST || error "Failed to build Game Server"
}

move_jar() {
    local src="$1"
    local outname="$2"

    JAR_PATH=$(find "$src/target" -type f -name "*with-dependencies.jar" -o -name "*.jar" | head -n1)
    if [[ -z "$JAR_PATH" ]]; then
        error "No .jar file found in $src/target"
    fi

    mv -v "$JAR_PATH" "$JAR_DIR/$outname"
}

while getopts "hgcqo:d" opt; do
    case "$opt" in
        h) help; exit 0 ;;
        g) BUILD_GS=1 ;;
        c) CLEAN_GS=1 ;;
        o) JAR_DIR="$OPTARG" ;;
        q) SKIPTEST="-DskipTests" ;;
        d)
            echo "[DEBUG] BUILD_GS=$BUILD_GS"
            echo "[DEBUG] CLEAN_GS=$CLEAN_GS"
            echo "[DEBUG] SKIPTEST=$SKIPTEST"
            echo "[DEBUG] JAR_DIR=$JAR_DIR"
            ;;
        *) help; exit 1 ;;
    esac
done

if [[ "$BUILD_GS" -eq 0 && "$CLEAN_GS" -eq 0 ]]; then
    error "Nothing to do. Use -g to build or -c to clean. See -h for help."
fi

if [[ "$CLEAN_GS" -eq 1 ]]; then
    clean_gs
fi

if [[ "$BUILD_GS" -eq 1 ]]; then
    mkdir -p "$JAR_DIR" || error "Failed to create output directory: $JAR_DIR"
    build_gs
    move_jar "$GS_SRC" "server.jar"
fi

echo "[SCRIPT] Done."
