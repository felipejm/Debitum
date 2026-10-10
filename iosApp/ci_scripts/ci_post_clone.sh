#!/bin/sh
# Xcode Cloud images do not ship a JDK. The "Compile Kotlin Framework"
# build phase runs Gradle, which fails without Java.
set -eu

if ! command -v brew >/dev/null 2>&1; then
  if [ -x /opt/homebrew/bin/brew ]; then
    eval "$(/opt/homebrew/bin/brew shellenv)"
  fi
fi

brew install openjdk@21

jdk_bundle="$(brew --prefix openjdk@21)/libexec/openjdk.jdk"
sudo ln -sfn "$jdk_bundle" /Library/Java/JavaVirtualMachines/openjdk-21.jdk

export JAVA_HOME="$jdk_bundle/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "JAVA_HOME=$JAVA_HOME"
java -version
