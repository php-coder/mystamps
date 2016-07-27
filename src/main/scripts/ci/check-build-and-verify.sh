#!/bin/sh

RUN_ONLY_INTEGRATION_TESTS=no
if [ "$1" = '--only-integration-tests' ]; then
	RUN_ONLY_INTEGRATION_TESTS=yes
fi

. "$(dirname "$0")/common.sh"

CS_FAIL=
PMD_FAIL=
LICENSE_FAIL=
POM_FAIL=
BOOTLINT_FAIL=
JASMINE_FAIL=
HTML_FAIL=
TEST_FAIL=
VERIFY_FAIL=

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning >cs.log 2>&1 || CS_FAIL=yes
	mvn --batch-mode pmd:check >pmd.log 2>&1 || PMD_FAIL=yes
	mvn --batch-mode license:check >license.log 2>&1 || LICENSE_FAIL=yes
	mvn --batch-mode sortpom:verify -Dsort.verifyFail=stop >pom.log || POM_FAIL=yes
	find src -type f -name '*.html' | xargs bootlint >bootlint.log 2>&1 || BOOTLINT_FAIL=yes
	mvn --batch-mode jasmine:test >jasmine.log 2>&1 || JASMINE_FAIL=yes
	# FIXME: add check for src/main/config/nginx/503.*html
	# TODO: remove ignoring of error about alt attribute after resolving #314
	html5validator \
		--root src/main/webapp/WEB-INF/views \
		--ignore-re 'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
			'Attribute “(th|sec|togglz):[a-z]+” is not serializable' \
			'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
			'An "img" element must have an "alt" attribute' \
		--show-warnings \
		>validator.log 2>&1 || HTML_FAIL=yes
	mvn --batch-mode test -Denforcer.skip=true -DskipMinify=true >test.log 2>&1 || TEST_FAIL=yes
fi

mvn --batch-mode verify -Denforcer.skip=true -DskipUnitTests=true >verify.log 2>&1 || VERIFY_FAIL=yes

echo
echo 'Build summary:'
echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_status "$CS_FAIL"       'Run CheckStyle'
	print_status "$PMD_FAIL"      'Run PMD'
	print_status "$LICENSE_FAIL"  'Check license headers'
	print_status "$POM_FAIL"      'Check sorting of pom.xml'
	print_status "$BOOTLINT_FAIL" 'Run bootlint'
	print_status "$JASMINE_FAIL"  'Run JavaScript unit tests'
	print_status "$HTML_FAIL"     'Run html5validator'
	print_status "$TEST_FAIL"     'Run unit tests'
fi

print_status "$VERIFY_FAIL" 'Run integration tests'

echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_log cs.log        'Run CheckStyle'
	print_log pmd.log       'Run PMD'
	print_log license.log   'Check license headers'
	print_log pom.log       'Check sorting of pom.xml'
	print_log bootlint.log  'Run bootlint'
	print_log jasmine.log   'Run JavaScript unit tests'
	print_log validator.log 'Run html5validator'
	print_log test.log      'Run unit tests'
fi

print_log verify.log   'Run integration tests'

rm -f cs.log pmd.log license.log bootlint.log jasmine.log validator.log test.log verify.log

if [ -n "$CS_FAIL$PMD_FAIL$LICENSE_FAIL$POM_FAIL$BOOTLINT_FAIL$JASMINE_FAIL$HTML_FAIL$TEST_FAIL$VERIFY_FAIL" ]; then
	exit 1
fi
