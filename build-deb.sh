#!/bin/sh

set -e

./mvnw clean package
./mvnw -pl cli jpackage:jpackage

mkdir -p cli/target/dpkg-buildpackage-working-dir
cp -r debian cli/target/dpkg-buildpackage-working-dir

cd cli/target/dpkg-buildpackage-working-dir
dpkg-buildpackage -us -uc
