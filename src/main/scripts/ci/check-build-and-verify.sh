#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail


RUN_ONLY_INTEGRATION_TESTS=no
if [ "${1:-}" = '--only-integration-tests' ]; then
	RUN_ONLY_INTEGRATION_TESTS=yes
fi

. "$(dirname "$0")/common.sh"

CS_STATUS=
PMD_STATUS=
CODENARC_STATUS=
LICENSE_STATUS=
POM_STATUS=
BOOTLINT_STATUS=
RFLINT_STATUS=
JASMINE_STATUS=
HTML_STATUS=
ENFORCER_STATUS=
TEST_STATUS=
FINDBUGS_STATUS=
VERIFY_STATUS=
DANGER_STATUS=

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	
	mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning \
		>cs.log 2>&1 || CS_STATUS=fail
	
	mvn --batch-mode pmd:check \
		>pmd.log 2>&1 || PMD_STATUS=fail
	
	mvn --batch-mode codenarc:codenarc -Dcodenarc.maxPriority1Violations=0 -Dcodenarc.maxPriority2Violations=0 -Dcodenarc.maxPriority3Violations=0 \
		>codenarc.log 2>&1 || CODENARC_STATUS=fail
	
	mvn --batch-mode license:check \
		>license.log 2>&1 || LICENSE_STATUS=fail
	
	mvn --batch-mode sortpom:verify -Dsort.verifyFail=stop \
		>pom.log || POM_STATUS=fail
	
	find src -type f -name '*.html' | xargs bootlint \
		>bootlint.log 2>&1 || BOOTLINT_STATUS=fail
	
	rflint --error=all --ignore TooFewKeywordSteps --ignore TooManyTestSteps --configure LineTooLong:130 src/test/robotframework \
		>rflint.log 2>&1 || RFLINT_STATUS=fail
	
	mvn --batch-mode jasmine:test \
		>jasmine.log 2>&1 || JASMINE_STATUS=fail
	
	# FIXME: add check for src/main/config/nginx/503.*html
	# TODO: remove ignoring of error about alt attribute after resolving #314
	# TODO: remove ignoring of error about document language when it will be resolved in upstream
	html5validator \
		--root src/main/webapp/WEB-INF/views \
		--ignore-re 'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
			'Attribute “(th|sec|togglz):[a-z]+” is not serializable' \
			'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
			'An "img" element must have an "alt" attribute' \
			'The first child "option" element of a "select" element with a "required" attribute' \
			'This document appears to be written in (Danish|Lithuanian)' \
		--show-warnings \
		>validator.log 2>&1 || HTML_STATUS=fail
	
	mvn --batch-mode enforcer:enforce \
		>enforcer.log 2>&1 || ENFORCER_STATUS=fail
	
	mvn --batch-mode test -Denforcer.skip=true -Dmaven.resources.skip=true -DskipMinify=true -DdisableXmlReport=false \
		>test.log 2>&1 || TEST_STATUS=fail
	
	# run after tests for getting compiled sources
	mvn --batch-mode findbugs:check \
		>findbugs.log 2>&1 || FINDBUGS_STATUS=fail
fi

mvn --batch-mode verify -Denforcer.skip=true -DskipUnitTests=true \
	>verify-raw.log 2>&1 || VERIFY_STATUS=fail

if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' -a "${TRAVIS_PULL_REQUEST:-}" != 'false' ]; then
	danger >danger.log 2>&1 || DANGER_STATUS=fail
fi

# Workaround for #538
"$(dirname "$0")/filter-out-htmlunit-messages.pl" <verify-raw.log >verify.log

echo
echo 'Build summary:'
echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_status "$CS_STATUS"       'Run CheckStyle'
	print_status "$PMD_STATUS"      'Run PMD'
	print_status "$CODENARC_STATUS" 'Run CodeNarc'
	print_status "$LICENSE_STATUS"  'Check license headers'
	print_status "$POM_STATUS"      'Check sorting of pom.xml'
	print_status "$BOOTLINT_STATUS" 'Run bootlint'
	print_status "$RFLINT_STATUS"   'Run robot framework lint'
	print_status "$JASMINE_STATUS"  'Run JavaScript unit tests'
	print_status "$HTML_STATUS"     'Run html5validator'
	print_status "$ENFORCER_STATUS" 'Run maven-enforcer-plugin'
	print_status "$TEST_STATUS"     'Run unit tests'
	print_status "$FINDBUGS_STATUS" 'Run FindBugs'
fi

print_status "$VERIFY_STATUS" 'Run integration tests'

if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' -a "${TRAVIS_PULL_REQUEST:-}" != 'false' ]; then
	print_status "$DANGER_STATUS" 'Run danger'
fi

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

if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' -a "${TRAVIS_PULL_REQUEST:-}" != 'false' ]; then
	print_log danger.log 'Run danger'
fi

# In order to be able debug robot framework test flakes we need to have a report.
# Just encode it to a binary form and dump to console.
if fgrep -qs 'status="FAIL"' target/robotframework-reports/output.xml; then
	echo "===== REPORT START ====="
	base64 target/robotframework-reports/log.html
	echo "===== REPORT END ====="
fi

rm -f cs.log pmd.log codenarc.log license.log pom.log bootlint.log rflint.log jasmine.log validator.log enforcer.log test.log findbugs.log verify-raw.log verify.log danger.log

if [ -n "$CS_STATUS$PMD_STATUS$CODENARC_STATUS$LICENSE_STATUS$POM_STATUS$BOOTLINT_STATUS$RFLINT_STATUS$JASMINE_STATUS$HTML_STATUS$ENFORCER_STATUS$TEST_STATUS$FINDBUGS_STATUS$VERIFY_STATUS$DANGER_STATUS" ]; then
	exit 1
fi
