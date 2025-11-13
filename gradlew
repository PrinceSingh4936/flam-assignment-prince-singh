#!/usr/bin/env sh
# Minimal gradlew wrapper bootstrap (works if you have gradle installed locally).
# If you don't run locally this is optional.

DIRNAME=$(dirname "$0")
"$DIRNAME/gradle/wrapper/gradle-wrapper.jar" "$@"
