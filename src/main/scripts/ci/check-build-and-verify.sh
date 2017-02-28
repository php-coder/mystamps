#!/bin/sh

RUN_ONLY_INTEGRATION_TESTS=no
if [ "$1" = '--only-integration-tests' ]; then
	RUN_ONLY_INTEGRATION_TESTS=yes
fi

. "$(dirname "$0")/common.sh"

CS_FAIL=
PMD_FAIL=
CODENARC_FAIL=
LICENSE_FAIL=
POM_FAIL=
BOOTLINT_FAIL=
RFLINT_FAIL=
JASMINE_FAIL=
HTML_FAIL=
ENFORCER_FAIL=
TEST_FAIL=
FINDBUGS_FAIL=
VERIFY_FAIL=

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning >cs.log 2>&1 || CS_FAIL=yes
	mvn --batch-mode pmd:check >pmd.log 2>&1 || PMD_FAIL=yes
	mvn --batch-mode codenarc:codenarc \
		-Dcodenarc.maxPriority1Violations=0 \
		-Dcodenarc.maxPriority2Violations=0 \
		-Dcodenarc.maxPriority3Violations=0 \
		>codenarc.log 2>&1 || CODENARC_FAIL=yes
	mvn --batch-mode license:check >license.log 2>&1 || LICENSE_FAIL=yes
	mvn --batch-mode sortpom:verify -Dsort.verifyFail=stop >pom.log || POM_FAIL=yes
	find src -type f -name '*.html' | xargs bootlint >bootlint.log 2>&1 || BOOTLINT_FAIL=yes
	rflint --error=all \
		--ignore TooFewKeywordSteps \
		--configure LineTooLong:110 \
		src/test/robotframework \
		>rflint.log 2>&1 || RFLINT_FAIL=yes
	mvn --batch-mode jasmine:test >jasmine.log 2>&1 || JASMINE_FAIL=yes
	# FIXME: add check for src/main/config/nginx/503.*html
	# TODO: remove ignoring of error about alt attribute after resolving #314
	html5validator \
		--root src/main/webapp/WEB-INF/views \
		--ignore-re 'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
			'Attribute “(th|sec|togglz):[a-z]+” is not serializable' \
			'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
			'An "img" element must have an "alt" attribute' \
			'The first child "option" element of a "select" element with a "required" attribute' \
		--show-warnings \
		>validator.log 2>&1 || HTML_FAIL=yes
	mvn --batch-mode enforcer:enforce >enforcer.log 2>&1 || ENFORCER_FAIL=yes
	mvn --batch-mode test \
		-Denforcer.skip=true \
		-Dmaven.resources.skip=true \
		-DskipMinify=true \
		>test.log 2>&1 || TEST_FAIL=yes
	# run after tests for getting compiled sources
	mvn --batch-mode findbugs:check >findbugs.log 2>&1 || FINDBUGS_FAIL=yes
fi

mvn --batch-mode verify -Denforcer.skip=true -DskipUnitTests=true >verify-raw.log 2>&1 || VERIFY_FAIL=yes

# Workaround for #538
"$(dirname "$0")/filter-out-htmlunit-messages.pl" <verify-raw.log >verify.log

echo
echo 'Build summary:'
echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_status "$CS_FAIL"       'Run CheckStyle'
	print_status "$PMD_FAIL"      'Run PMD'
	print_status "$CODENARC_FAIL" 'Run CodeNarc'
	print_status "$LICENSE_FAIL"  'Check license headers'
	print_status "$POM_FAIL"      'Check sorting of pom.xml'
	print_status "$BOOTLINT_FAIL" 'Run bootlint'
	print_status "$RFLINT_FAIL"   'Run robot framework lint'
	print_status "$JASMINE_FAIL"  'Run JavaScript unit tests'
	print_status "$HTML_FAIL"     'Run html5validator'
	print_status "$ENFORCER_FAIL" 'Run maven-enforcer-plugin'
	print_status "$TEST_FAIL"     'Run unit tests'
	print_status "$FINDBUGS_FAIL" 'Run FindBugs'
fi

print_status "$VERIFY_FAIL" 'Run integration tests'

echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_log cs.log        'Run CheckStyle'
	print_log pmd.log       'Run PMD'
	print_log codenarc.log  'Run CodeNarc'
	print_log license.log   'Check license headers'
	print_log pom.log       'Check sorting of pom.xml'
	print_log bootlint.log  'Run bootlint'
	print_log rflint.log    'Run robot framework lint'
	print_log jasmine.log   'Run JavaScript unit tests'
	print_log validator.log 'Run html5validator'
	print_log enforcer.log  'Run maven-enforcer-plugin'
	print_log test.log      'Run unit tests'
	print_log findbugs.log  'Run FindBugs'
fi

print_log verify.log   'Run integration tests'

rm -f cs.log pmd.log codenarc.log license.log pom.log bootlint.log rflint.log jasmine.log validator.log enforcer.log test.log findbugs.log verify-raw.log verify.log

if [ -n "$CS_FAIL$PMD_FAIL$CODENARC_FAIL$LICENSE_FAIL$POM_FAIL$BOOTLINT_FAIL$RFLINT_FAIL$JASMINE_FAIL$HTML_FAIL$ENFORCER_FAIL$TEST_FAIL$FINDBUGS_FAIL$VERIFY_FAIL" ]; then
	exit 1
fi
