#!/bin/sh
set -e

echo "Starting Nexus Backend..."
echo "JAVA_OPTS: ${JAVA_OPTS}"

exec java ${JAVA_OPTS} -jar app.jar "$@"
