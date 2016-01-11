#!/bin/sh

. "$(dirname "$0")/common.sh"

mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning >cs.log 2>&1 || touch CS_FAIL
mvn --batch-mode pmd:check >pmd.log 2>&1 || touch PMD_FAIL
mvn --batch-mode license:check >license.log 2>&1 || touch LICENSE_FAIL
find src -type f -name '*.html' | xargs bootlint >bootlint.log 2>&1 || touch BOOTLINT_FAIL
mvn --batch-mode verify >verify.log 2>&1 || touch VERIFY_FAIL
mvn --batch-mode jasmine:test >jasmine.log 2>&1 || touch JASMINE_FAIL

echo
echo 'Build summary:'
echo

print_status CS_FAIL       'Run CheckStyle'
print_status PMD_FAIL      'Run PMD'
print_status LICENSE_FAIL  'Check license headers'
print_status BOOTLINT_FAIL 'Run bootlint'
print_status VERIFY_FAIL   'Compile and run unit/integration tests'
print_status JASMINE_FAIL  'Run JavaScript unit tests'

echo

print_log cs.log       'Run CheckStyle'
print_log pmd.log      'Run PMD'
print_log license.log  'Check license headers'
print_log bootlint.log 'Run bootlint'
print_log verify.log   'Compile and run unit/integration tests'
print_log jasmine.log  'Run JavaScript unit tests'

rm -f cs.log pmd.log license.log bootlint.log verify.log jasmine.log

if [ -n "$(ls *_FAIL 2>/dev/null)" ]; then
	rm -f CS_FAIL PMD_FAIL LICENSE_FAIL BOOTLINT_FAIL VERIFY_FAIL JASMINE_FAIL
	exit 1
fi
