#!/bin/sh
# Xcode Cloud images do not ship a JDK. The "Compile Kotlin Framework"
# build phase runs Gradle, which fails without Java.
set -eu

export HOMEBREW_NO_AUTO_UPDATE=1
export HOMEBREW_NO_INSTALL_CLEANUP=1
export HOMEBREW_NO_ENV_HINTS=1

if ! command -v brew >/dev/null 2>&1; then
  if [ -x /opt/homebrew/bin/brew ]; then
    eval "$(/opt/homebrew/bin/brew shellenv)"
  fi
fi

brew install openjdk@21

# Homebrew's openjdk is keg-only. Xcode Cloud cannot sudo, so the
# Gradle build phase points JAVA_HOME at this path directly.
JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "JDK 21 was installed but java is missing at $JAVA_HOME" >&2
  exit 1
fi

echo "JAVA_HOME=$JAVA_HOME"
"$JAVA_HOME/bin/java" -version
