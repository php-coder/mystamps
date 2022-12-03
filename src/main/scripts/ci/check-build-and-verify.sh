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

VERIFY_STATUS=

DANGER_STATUS=skip
if [ "${SPRING_PROFILES_ACTIVE:-}" = 'travis' ] && [ "${TRAVIS_PULL_REQUEST:-false}" != 'false' ]; then
	DANGER_STATUS=
fi

VERIFY_TIME=0
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

print_log verify.log   'Run integration tests'

if [ "$DANGER_STATUS" != 'skip' ]; then
	print_log danger.log 'Run danger'
fi

rm -f verify.log danger.log

if echo "$VERIFY_STATUS$DANGER_STATUS" | grep -Fqs 'fail'; then
	exit 1
fi
