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
	'ansible-lint')
		exec ansible-lint \
			-x 106 \
			"$ROOTDIR/infra/vagrant/provisioning/prod.yml" \
			"$ROOTDIR/infra/vagrant/provisioning/vagrant.yml" \
			"$ROOTDIR/infra/vagrant/provisioning/bootstrap.yml" \
			"$ROOTDIR/src/main/scripts/ci/ansible/deploy.yml"
		;;
	'check-license')
		exec "$MVN" \
			--batch-mode \
			license:check
		;;
	'checkstyle')
		exec "$MVN" \
			--batch-mode \
			checkstyle:check \
			-Dcheckstyle.violationSeverity=warning
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
	'pmd')
		exec "$MVN" \
			--batch-mode \
			pmd:check
		;;
	'shellcheck')
		# Shellcheck doesn't support recursive scanning: https://github.com/koalaman/shellcheck/issues/143
		# Also I don't want to invoke it many times (slower, more code for handling failures), so I prefer this way.
		# shellcheck disable=SC2207
		SHELL_FILES=( $(find "$ROOTDIR/src/main/scripts" -type f -name '*.sh') )
		exec shellcheck \
			--shell bash \
			--format gcc \
			"${SHELL_FILES[@]}"
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
		echo >&2 '- ansible-lint'
		echo >&2 '- check-license'
		echo >&2 '- checkstyle'
		echo >&2 '- enforcer'
		echo >&2 '- integration-tests'
		echo >&2 '- jest'
		echo >&2 '- pmd'
		echo >&2 '- shellcheck'
		echo >&2 '- spotbugs'
		echo >&2 '- unit-tests'
		exit 1
		;;
esac
