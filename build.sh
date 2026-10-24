#!/bin/sh
set -eu
rm -rf out minibank.jar
mkdir -p out
javac -d out $(find src -name '*.java')
jar --create --file minibank.jar --manifest manifest.mf -C out .
