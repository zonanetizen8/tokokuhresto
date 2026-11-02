#!/usr/bin/env sh
# Lightweight Gradle bootstrap for this project. Downloads the pinned Gradle distribution
# on first run, then executes it. Requires Java, curl (or wget), and unzip.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="9.3.1"
GRADLE_BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/tokokuh-wrapper"
GRADLE_HOME="$GRADLE_BASE/gradle-$GRADLE_VERSION"
if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  mkdir -p "$GRADLE_BASE"
  ZIP="$GRADLE_BASE/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  echo "Downloading Gradle $GRADLE_VERSION..."
  if command -v curl >/dev/null 2>&1; then curl -fL "$URL" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then wget -O "$ZIP" "$URL"
  else echo "Install curl or wget first." >&2; exit 1; fi
  if ! command -v unzip >/dev/null 2>&1; then echo "Install unzip first." >&2; exit 1; fi
  unzip -q -o "$ZIP" -d "$GRADLE_BASE"
fi
exec "$GRADLE_HOME/bin/gradle" -p "$APP_HOME" "$@"
