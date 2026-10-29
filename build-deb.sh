#!/bin/sh

set -e

./mvnw clean package

mkdir -p cli/target/dpkg-buildpackage-working-dir
cp -r data/debian cli/target/dpkg-buildpackage-working-dir

cd cli/target/dpkg-buildpackage-working-dir
dpkg-buildpackage -us -uc
