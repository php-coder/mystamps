#!/bin/bash

# Treat unset variables and parameters as an error when performing parameter expansion
set -o nounset

# Exit immediately if command returns a non-zero status
set -o errexit

# Return value of a pipeline is the value of the last command to exit with a non-zero status
set -o pipefail

ROOTDIR="$(dirname "$0")/../../.."

MVN=mvn

case ${1:-} in
	'check-license')
		exec "$MVN" \
			--batch-mode \
			license:check
		;;
	'enforcer')
		exec "$MVN" \
			--batch-mode \
			enforcer:enforce
		;;
	'integration-tests')
		exec "$MVN" \
			--batch-mode \
			--activate-profiles frontend,native2ascii \
			verify \
			-Denforcer.skip=true \
			-DskipUnitTests=true
		;;
	'jest')
		exec "$MVN" \
			--batch-mode \
			--activate-profiles frontend \
			frontend:install-node-and-npm \
			frontend:npm \
			-Dfrontend.npm.arguments='install-ci-test'
		;;
	'spotbugs')
		exec "$MVN" \
			--batch-mode \
			spotbugs:check
		;;
	'unit-tests')
		exec "$MVN" \
			--batch-mode \
			test \
			-Denforcer.skip=true \
			-DskipMinify=true \
			-DdisableXmlReport=false \
			-Dskip.npm \
			-Dskip.installnodenpm
		;;
	*)
		echo >&2 "Usage: $0 <command>"
		echo >&2
		echo >&2 "Where <command> is one of:"
		echo >&2 '- check-license'
		echo >&2 '- enforcer'
		echo >&2 '- integration-tests'
		echo >&2 '- jest'
		echo >&2 '- spotbugs'
		echo >&2 '- unit-tests'
		exit 1
		;;
esac
