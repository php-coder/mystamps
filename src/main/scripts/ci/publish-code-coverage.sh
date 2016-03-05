#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -e errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


. "$(dirname "$0")/common.sh"

mvn --batch-mode jacoco:prepare-agent test jacoco:report coveralls:jacoco >jacoco.log 2>&1 || touch JACOCO_FAIL

print_status JACOCO_FAIL 'Publish code coverage'

print_log jacoco.log 'Publish code coverage'

rm -f jacoco.log

if [ -n "$(ls *_FAIL 2>/dev/null)" ]; then
	rm -f JACOCO_FAIL
	exit 1
fi
