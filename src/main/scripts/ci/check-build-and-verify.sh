#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

print_status() {
	# $1 is empty if check has succeeded
	# $1 equals to 'fail' if check has failed
	# $1 equals to 'skip' if check has skipped
	local result="$1"
	local execution_time="$2"
	local msg="$3"
	
	local status='SUCCESS'
	local color=32
	
	if [ "$result" = 'fail' ]; then
		status='FAIL'
		color=31
	elif [ "$result" = 'skip' ]; then
		status='SKIP'
		color=33
	fi
	
	local time=
	if [ "$execution_time" -gt 0 ]; then
		local mins secs
		mins=$((execution_time / 60))
		secs=$((execution_time % 60))
		if [ "$mins" -eq 0 ]; then
			time="$(printf '%2ss' "$secs")"
		else
			time="$(printf '%2sm%2ds' "$mins" "$secs")"
		fi
	fi
	
	printf '* %-30s% 7s\t\t\033[1;%dm%-7s\033[0m\n' "$msg" "$time" "$color" "$status"
}

print_log() {
	local log_file="$1"
	local msg="$2"
	
	echo
	printf "=====> \033[1;33m%s\033[0m\n" "$msg"
	echo
	grep -Ev '^\[INFO\] Download(ing|ed)' "$log_file" || :
}

RUN_ONLY_INTEGRATION_TESTS=no
if [ "${1:-}" = '--only-integration-tests' ]; then
	RUN_ONLY_INTEGRATION_TESTS=yes
fi

SHELLCHECK_STATUS=
JEST_STATUS=
HTML_STATUS=
ENFORCER_STATUS=
TEST_STATUS=
CODENARC_STATUS=
SPOTBUGS_STATUS=
VERIFY_STATUS=
ANSIBLE_LINT_STATUS=

DANGER_STATUS=skip
if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' ] && [ "${TRAVIS_PULL_REQUEST:-false}" != 'false' ]; then
	DANGER_STATUS=
fi

SHELLCHECK_TIME=0
JEST_TIME=0
HTML_TIME=0
ENFORCER_TIME=0
TEST_TIME=0
CODENARC_TIME=0
SPOTBUGS_TIME=0
VERIFY_TIME=0
ANSIBLE_LINT_TIME=0
DANGER_TIME=0

CURDIR="$(dirname "$0")"
EXEC_CMD="$CURDIR/../execute-command.sh"

echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	
	# TRAVIS_COMMIT_RANGE: The range of commits that were included in the push or
	# pull request. (Note that this is empty for builds triggered by the initial
	# commit of a new branch.)
	if [ -n "${TRAVIS_COMMIT_RANGE:-}" ]; then
		echo "INFO: Range of the commits to be checked: $TRAVIS_COMMIT_RANGE"
		echo 'INFO: List of the files modified by this commits range:'
		git --no-pager diff --name-only "$TRAVIS_COMMIT_RANGE" -- | sed 's|^|      |' || :
		
		MODIFIED_FILES="$(git --no-pager diff --name-only "$TRAVIS_COMMIT_RANGE" -- 2>/dev/null || :)"
		
		if [ -n "$MODIFIED_FILES" ]; then
			AFFECTS_POM_XML="$(echo "$MODIFIED_FILES"      | grep -Fxq 'pom.xml' || echo 'no')"
			AFFECTS_TRAVIS_CFG="$(echo "$MODIFIED_FILES"   | grep -Fxq '.travis.yml' || echo 'no')"
			AFFECTS_SPOTBUGS_CFG="$(echo "$MODIFIED_FILES"  |  grep -q 'spotbugs-filter\.xml$' || echo 'no')"
			AFFECTS_JS_FILES="$(echo "$MODIFIED_FILES"      |  grep -q '\.js$' || echo 'no')"
			AFFECTS_HTML_FILES="$(echo "$MODIFIED_FILES"    |  grep -q '\.html$' || echo 'no')"
			AFFECTS_JAVA_FILES="$(echo "$MODIFIED_FILES"    |  grep -q '\.java$' || echo 'no')"
			AFFECTS_SHELL_FILES="$(echo "$MODIFIED_FILES"   |  grep -q '\.sh$' || echo 'no')"
			AFFECTS_GROOVY_FILES="$(echo "$MODIFIED_FILES"  |  grep -q '\.groovy$' || echo 'no')"
			AFFECTS_PLAYBOOKS="$(echo "$MODIFIED_FILES"      |  grep -Eq '(vagrant|prod|deploy|bootstrap|/roles/.+)\.yml$' || echo 'no')"
			
			if [ "$AFFECTS_POM_XML" = 'no' ]; then
				ENFORCER_STATUS=skip
				
				if [ "$AFFECTS_JAVA_FILES" = 'no' ]; then
					[ "$AFFECTS_SPOTBUGS_CFG" != 'no' ] || SPOTBUGS_STATUS=skip
					
					if [ "$AFFECTS_GROOVY_FILES" = 'no' ]; then
						TEST_STATUS=skip
					fi
				fi
				
				[ "$AFFECTS_GROOVY_FILES" != 'no' ] || CODENARC_STATUS=skip
				[ "$AFFECTS_JS_FILES" != 'no' ] || JEST_STATUS=skip
			fi
			
			if [ "$AFFECTS_TRAVIS_CFG" = 'no' ]; then
				if [ "$AFFECTS_HTML_FILES" = 'no' ]; then
					HTML_STATUS=skip
				fi
				[ "$AFFECTS_SHELL_FILES" != 'no' ] || SHELLCHECK_STATUS=skip

				if [ "$AFFECTS_PLAYBOOKS" = 'no' ]; then
					ANSIBLE_LINT_STATUS=skip
				fi
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
	
	if [ "$SHELLCHECK_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		"$EXEC_CMD" shellcheck >shellcheck.log 2>&1 || SHELLCHECK_STATUS=fail
		SHELLCHECK_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$SHELLCHECK_STATUS" "$SHELLCHECK_TIME" 'Run shellcheck'
	
	if [ "$JEST_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		"$EXEC_CMD" jest >jest.log 2>&1 || JEST_STATUS=fail
		JEST_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$JEST_STATUS" "$JEST_TIME" 'Run JavaScript unit tests'
	
	if [ "$HTML_STATUS" != 'skip' ]; then
		"$EXEC_CMD" html5validator >validator.log 2>&1 || HTML_STATUS=fail
		HTML_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$HTML_STATUS" "$HTML_TIME" 'Run html5validator'
	
	if [ "$ENFORCER_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		"$EXEC_CMD" enforcer >enforcer.log 2>&1 || ENFORCER_STATUS=fail
		ENFORCER_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$ENFORCER_STATUS" "$ENFORCER_TIME" 'Run maven-enforcer-plugin'
	
	if [ "$TEST_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		"$EXEC_CMD" unit-tests >test.log 2>&1 || TEST_STATUS=fail
		TEST_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$TEST_STATUS" "$TEST_TIME" 'Run unit tests'
	
	if [ "$CODENARC_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		# run after tests for getting compiled sources
		"$EXEC_CMD" codenarc >codenarc.log 2>&1 || CODENARC_STATUS=fail
		CODENARC_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$CODENARC_STATUS" "$CODENARC_TIME" 'Run CodeNarc'
	
	if [ "$SPOTBUGS_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		# run after tests for getting compiled sources
		"$EXEC_CMD" spotbugs >spotbugs.log 2>&1 || SPOTBUGS_STATUS=fail
		SPOTBUGS_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$SPOTBUGS_STATUS" "$SPOTBUGS_TIME" 'Run SpotBugs'

	if [ "$ANSIBLE_LINT_STATUS" != 'skip' ]; then
		START_TIME=$SECONDS
		"$EXEC_CMD" ansible-lint >ansible_lint.log 2>&1 || ANSIBLE_LINT_STATUS=fail
		ANSIBLE_LINT_TIME=$((SECONDS - START_TIME))
	fi
	print_status "$ANSIBLE_LINT_STATUS" $ANSIBLE_LINT_TIME 'Run Ansible Lint'
fi

START_TIME=$SECONDS
"$EXEC_CMD" integration-tests >verify.log 2>&1 || VERIFY_STATUS=fail
VERIFY_TIME=$((SECONDS - START_TIME))

print_status "$VERIFY_STATUS" "$VERIFY_TIME" 'Run integration tests'


if [ "$DANGER_STATUS" != 'skip' ]; then
	START_TIME=$SECONDS
	"$EXEC_CMD" danger >danger.log 2>&1 || DANGER_STATUS=fail
	DANGER_TIME=$((SECONDS - START_TIME))
fi
print_status "$DANGER_STATUS" "$DANGER_TIME" 'Run danger'

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	[ "$SHELLCHECK_STATUS" = 'skip' ]     || print_log shellcheck.log     'Run shellcheck'
	[ "$JEST_STATUS" = 'skip' ]           || print_log jest.log           'Run JavaScript unit tests'
	[ "$HTML_STATUS" = 'skip' ]           || print_log validator.log      'Run html5validator'
	[ "$ENFORCER_STATUS" = 'skip' ]       || print_log enforcer.log       'Run maven-enforcer-plugin'
	[ "$TEST_STATUS" = 'skip' ]           || print_log test.log           'Run unit tests'
	[ "$CODENARC_STATUS" = 'skip' ]       || print_log codenarc.log       'Run CodeNarc'
	[ "$SPOTBUGS_STATUS" = 'skip' ]       || print_log spotbugs.log       'Run SpotBugs'
	[ "$ANSIBLE_LINT_STATUS" = 'skip' ]   || print_log ansible_lint.log   'Run Ansible Lint'
fi

print_log verify.log   'Run integration tests'

if [ "$DANGER_STATUS" != 'skip' ]; then
	print_log danger.log 'Run danger'
fi

rm -f shellcheck.log jest.log validator.log enforcer.log test.log codenarc.log spotbugs.log verify.log danger.log ansible_lint.log

if echo "$SHELLCHECK_STATUS$JEST_STATUS$HTML_STATUS$ENFORCER_STATUS$TEST_STATUS$CODENARC_STATUS$SPOTBUGS_STATUS$VERIFY_STATUS$DANGER_STATUS$ANSIBLE_LINT_STATUS" | grep -Fqs 'fail'; then
	exit 1
fi
