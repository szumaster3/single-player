#!/usr/bin/env bash

SRC_DIR="cache/"
DEST_DIR="../../Server/data/cache/"

rm -rf "${DEST_DIR:?}"*
mkdir -p "$DEST_DIR"
cp -r "$SRC_DIR"* "$DEST_DIR"
