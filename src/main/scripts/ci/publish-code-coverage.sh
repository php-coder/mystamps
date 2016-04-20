#!/bin/sh

. "$(dirname "$0")/common.sh"

JACOCO_FAIL=

mvn --batch-mode jacoco:prepare-agent test jacoco:report coveralls:jacoco >jacoco.log 2>&1 || JACOCO_FAIL=yes

print_status "$JACOCO_FAIL" 'Publish code coverage'

print_log jacoco.log 'Publish code coverage'

rm -f jacoco.log
