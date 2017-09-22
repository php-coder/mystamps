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

DANGER_STATUS=skip
if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' -a "${TRAVIS_PULL_REQUEST:-false}" != 'false' ]; then
	DANGER_STATUS=
fi

echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	
	# TRAVIS_COMMIT_RANGE: The range of commits that were included in the push or
	# pull request. (Note that this is empty for builds triggered by the initial
	# commit of a new branch.)
	if [ -n "${TRAVIS_COMMIT_RANGE:-}" ]; then
		echo "INFO: Range of the commits to be checked: $TRAVIS_COMMIT_RANGE"
		echo 'INFO: List of the files modified by this commits range:'
		git --no-pager diff --name-only $TRAVIS_COMMIT_RANGE -- | sed 's|^|      |' || :
		
		MODIFIED_FILES="$(git --no-pager diff --name-only $TRAVIS_COMMIT_RANGE -- 2>/dev/null || :)"
		
		if [ -n "$MODIFIED_FILES" ]; then
			AFFECTS_POM_XML="$(echo "$MODIFIED_FILES"      | fgrep -xq 'pom.xml' || echo 'no')"
			AFFECTS_TRAVIS_CFG="$(echo "$MODIFIED_FILES"   | fgrep -xq '.travis.yml' || echo 'no')"
			AFFECTS_CS_CFG="$(echo "$MODIFIED_FILES"        | egrep -q '(checkstyle\.xml|checkstyle-suppressions\.xml)$' || echo 'no')"
			AFFECTS_FB_CFG="$(echo "$MODIFIED_FILES"        |  grep -q 'findbugs-filter\.xml$' || echo 'no')"
			AFFECTS_PMD_XML="$(echo "$MODIFIED_FILES"       |  grep -q 'pmd\.xml$' || echo 'no')"
			AFFECTS_JS_FILES="$(echo "$MODIFIED_FILES"      |  grep -q '\.js$' || echo 'no')"
			AFFECTS_HTML_FILES="$(echo "$MODIFIED_FILES"    |  grep -q '\.html$' || echo 'no')"
			AFFECTS_JAVA_FILES="$(echo "$MODIFIED_FILES"    |  grep -q '\.java$' || echo 'no')"
			AFFECTS_ROBOT_FILES="$(echo "$MODIFIED_FILES"   |  grep -q '\.robot$' || echo 'no')"
			AFFECTS_GROOVY_FILES="$(echo "$MODIFIED_FILES"  |  grep -q '\.groovy$' || echo 'no')"
			AFFECTS_PROPERTIES="$(echo "$MODIFIED_FILES"    |  grep -q '\.properties$' || echo 'no')"
			AFFECTS_LICENSE_HEADER="$(echo "$MODIFIED_FILES" | grep -q 'license_header\.txt$' || echo 'no')"
			
			if [ "$AFFECTS_POM_XML" = 'no' ]; then
				POM_STATUS=skip
				ENFORCER_STATUS=skip
				
				if [ "$AFFECTS_JAVA_FILES" = 'no' ]; then
					[ "$AFFECTS_FB_CFG" != 'no' ] || FINDBUGS_STATUS=skip
					[ "$AFFECTS_CS_CFG" != 'no' -o "$AFFECTS_PROPERTIES" != 'no' ] || CS_STATUS=skip
					[ "$AFFECTS_PMD_XML" != 'no' ] || PMD_STATUS=skip
					
					if [ "$AFFECTS_GROOVY_FILES" = 'no' ]; then
						TEST_STATUS=skip
						
						[ "$AFFECTS_LICENSE_HEADER" != 'no' ] || LICENSE_STATUS=skip
					fi
				fi
				
				[ "$AFFECTS_GROOVY_FILES" != 'no' ] || CODENARC_STATUS=skip
				[ "$AFFECTS_JS_FILES" != 'no' ] || JASMINE_STATUS=skip
			fi
			
			if [ "$AFFECTS_TRAVIS_CFG" = 'no' ]; then
				if [ "$AFFECTS_HTML_FILES" = 'no' ]; then
					BOOTLINT_STATUS=skip
					HTML_STATUS=skip
				fi
				[ "$AFFECTS_ROBOT_FILES" != 'no' ] || RFLINT_STATUS=skip
			fi
			echo 'INFO: Some checks could be skipped'
		else
			echo "INFO: Couldn't determine list of modified files."
			echo 'INFO: All checks will be performed'
		fi
	else
		echo "INFO: Couldn't determine a range of the commits: \$TRAVIS_COMMIT_RANGE is empty."
		echo 'INFO: All checks will be performed'
	fi
	
	echo
	
	if [ "$CS_STATUS" != 'skip' ]; then
		mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning \
			>cs.log 2>&1 || CS_STATUS=fail
	fi
	print_status "$CS_STATUS" 'Run CheckStyle'
	
	if [ "$PMD_STATUS" != 'skip' ]; then
		mvn --batch-mode pmd:check \
			>pmd.log 2>&1 || PMD_STATUS=fail
	fi
	print_status "$PMD_STATUS" 'Run PMD'
	
	if [ "$CODENARC_STATUS" != 'skip' ]; then
		mvn --batch-mode codenarc:codenarc -Dcodenarc.maxPriority1Violations=0 -Dcodenarc.maxPriority2Violations=0 -Dcodenarc.maxPriority3Violations=0 \
			>codenarc.log 2>&1 || CODENARC_STATUS=fail
	fi
	print_status "$CODENARC_STATUS" 'Run CodeNarc'
	
	if [ "$LICENSE_STATUS" != 'skip' ]; then
		mvn --batch-mode license:check \
			>license.log 2>&1 || LICENSE_STATUS=fail
	fi
	print_status "$LICENSE_STATUS" 'Check license headers'
	
	if [ "$POM_STATUS" != 'skip' ]; then
		mvn --batch-mode sortpom:verify -Dsort.verifyFail=stop \
			>pom.log 2>&1 || POM_STATUS=fail
	fi
	print_status "$POM_STATUS" 'Check sorting of pom.xml'
	
	if [ "$BOOTLINT_STATUS" != 'skip' ]; then
		find src -type f -name '*.html' | xargs bootlint \
			>bootlint.log 2>&1 || BOOTLINT_STATUS=fail
	fi
	print_status "$BOOTLINT_STATUS" 'Run bootlint'
	
	if [ "$RFLINT_STATUS" != 'skip' ]; then
		rflint --error=all --ignore TooFewKeywordSteps --ignore TooManyTestSteps --ignore TooManyTestCases --configure LineTooLong:130 src/test/robotframework \
			>rflint.log 2>&1 || RFLINT_STATUS=fail
	fi
	print_status "$RFLINT_STATUS" 'Run robot framework lint'
	
	if [ "$JASMINE_STATUS" != 'skip' ]; then
		mvn --batch-mode jasmine:test \
			>jasmine.log 2>&1 || JASMINE_STATUS=fail
	fi
	print_status "$JASMINE_STATUS" 'Run JavaScript unit tests'
	
	if [ "$HTML_STATUS" != 'skip' ]; then
		# FIXME: add check for src/main/config/nginx/503.*html
		# TODO: remove ignoring of error about alt attribute after resolving #314
		# TODO: remove ignoring of error about document language when it will be resolved in upstream
		# TODO: remove ignoring of error about Picked up _JAVA_OPTIONS when it will be resolved in upstream
		html5validator \
			--root src/main/webapp/WEB-INF/views \
			--ignore-re 'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
				'Attribute “(th|sec|togglz):[a-z]+” is not serializable' \
				'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
				'An "img" element must have an "alt" attribute' \
				'The first child "option" element of a "select" element with a "required" attribute' \
				'This document appears to be written in (Danish|Lithuanian)' \
				'Picked up' \
			--show-warnings \
			>validator.log 2>&1 || HTML_STATUS=fail
	fi
	print_status "$HTML_STATUS" 'Run html5validator'
	
	if [ "$ENFORCER_STATUS" != 'skip' ]; then
		mvn --batch-mode enforcer:enforce \
			>enforcer.log 2>&1 || ENFORCER_STATUS=fail
	fi
	print_status "$ENFORCER_STATUS" 'Run maven-enforcer-plugin'
	
	if [ "$TEST_STATUS" != 'skip' ]; then
		mvn --batch-mode test -Denforcer.skip=true -Dmaven.resources.skip=true -DskipMinify=true -DdisableXmlReport=false \
			>test.log 2>&1 || TEST_STATUS=fail
	fi
	print_status "$TEST_STATUS" 'Run unit tests'
	
	if [ "$FINDBUGS_STATUS" != 'skip' ]; then
		# run after tests for getting compiled sources
		mvn --batch-mode findbugs:check \
			>findbugs.log 2>&1 || FINDBUGS_STATUS=fail
	fi
	print_status "$FINDBUGS_STATUS" 'Run FindBugs'
fi

mvn --batch-mode verify -Denforcer.skip=true -DskipUnitTests=true \
	>verify-raw.log 2>&1 || VERIFY_STATUS=fail
# Workaround for #538
"$(dirname "$0")/filter-out-htmlunit-messages.pl" <verify-raw.log >verify.log

print_status "$VERIFY_STATUS" 'Run integration tests'


if [ "$DANGER_STATUS" != 'skip' ]; then
	danger >danger.log 2>&1 || DANGER_STATUS=fail
fi
print_status "$DANGER_STATUS" 'Run danger'

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	[ "$CS_STATUS" = 'skip' ]       || print_log cs.log        'Run CheckStyle'
	[ "$PMD_STATUS" = 'skip' ]      || print_log pmd.log       'Run PMD'
	[ "$CODENARC_STATUS" = 'skip' ] || print_log codenarc.log  'Run CodeNarc'
	[ "$LICENSE_STATUS" = 'skip' ]  || print_log license.log   'Check license headers'
	[ "$POM_STATUS" = 'skip' ]      || print_log pom.log       'Check sorting of pom.xml'
	[ "$BOOTLINT_STATUS" = 'skip' ] || print_log bootlint.log  'Run bootlint'
	[ "$RFLINT_STATUS" = 'skip' ]   || print_log rflint.log    'Run robot framework lint'
	[ "$JASMINE_STATUS" = 'skip' ]  || print_log jasmine.log   'Run JavaScript unit tests'
	[ "$HTML_STATUS" = 'skip' ]     || print_log validator.log 'Run html5validator'
	[ "$ENFORCER_STATUS" = 'skip' ] || print_log enforcer.log  'Run maven-enforcer-plugin'
	[ "$TEST_STATUS" = 'skip' ]     || print_log test.log      'Run unit tests'
	[ "$FINDBUGS_STATUS" = 'skip' ] || print_log findbugs.log  'Run FindBugs'
fi

print_log verify.log   'Run integration tests'

if [ "$DANGER_STATUS" != 'skip' ]; then
	print_log danger.log 'Run danger'
fi

rm -f cs.log pmd.log codenarc.log license.log pom.log bootlint.log rflint.log jasmine.log validator.log enforcer.log test.log findbugs.log verify-raw.log verify.log danger.log

if echo "$CS_STATUS$PMD_STATUS$CODENARC_STATUS$LICENSE_STATUS$POM_STATUS$BOOTLINT_STATUS$RFLINT_STATUS$JASMINE_STATUS$HTML_STATUS$ENFORCER_STATUS$TEST_STATUS$FINDBUGS_STATUS$VERIFY_STATUS$DANGER_STATUS" | fgrep -qs 'fail'; then
	exit 1
fi
