#!/bin/sh

set -e

./mvnw clean package

jlink \
  --module-path "./cli/target/lib:./cli/target/cli-0.0.1.jar" \
  --add-modules "de.til7701.fluxus.cli" \
  --launcher "fluxus=de.til7701.fluxus.cli/de.til7701.fluxus.cli.Main" \
  --no-header-files \
  --no-man-pages \
  --strip-debug \
  --compress zip-6 \
  --add-options="--enable-preview -Dfluxus.version=0.0.1" \
  --output "./cli/target/jlink-out" \
  --verbose

mkdir -p cli/target/dpkg-buildpackage-working-dir
cp -r data/debian cli/target/dpkg-buildpackage-working-dir

cd cli/target/dpkg-buildpackage-working-dir
dpkg-buildpackage -us -uc
