#!/usr/bin/env bash

set -eo pipefail

if [ -z $CERTIFICATES_DIRECTORY ]; then
  echo "Missing environment variable 'CERTIFICATES_DIRECTORY'."
  exit 1
fi

CERTS_OPTS="-Dcerts.dir=$CERTIFICATES_DIRECTORY -Dcerts.provider='user.home'"
export JAVA_OPTS="$JAVA_OPTS $CERTS_OPTS"

exec "/opt/app/bin/java-client" "$@"