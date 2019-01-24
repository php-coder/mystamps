#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


# shellcheck source=web/src/main/scripts/ci/common.sh
. "$(dirname "$0")/common.sh"

JACOCO_FAIL=

mvn --batch-mode jacoco:prepare-agent test jacoco:report -Denforcer.skip=true -DskipMinify=true >jacoco.log 2>&1 || JACOCO_FAIL=yes

# -Z Exit with 1 if not successful. Default will Exit with 0
bash <(curl -s https://codecov.io/bash) -Z >>jacoco.log 2>&1 || JACOCO_FAIL=yes

print_status "$JACOCO_FAIL" 'Publish code coverage'

print_log jacoco.log 'Publish code coverage'

rm -f jacoco.log

if [ -n "$JACOCO_FAIL" ]; then
	exit 1
fi
