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
	'check-pom')
		exec "$MVN" \
			--batch-mode \
			sortpom:verify \
			-Dsort.verifyFail=stop
		;;
	'checkstyle')
		exec "$MVN" \
			--batch-mode \
			checkstyle:check \
			-Dcheckstyle.violationSeverity=warning
		;;
	'codenarc')
		exec "$MVN" \
			--batch-mode \
			codenarc:codenarc \
			-Dcodenarc.maxPriority1Violations=0 \
			-Dcodenarc.maxPriority2Violations=0 \
			-Dcodenarc.maxPriority3Violations=0
		;;
	'enforcer')
		exec "$MVN" \
			--batch-mode \
			enforcer:enforce
		;;
	'html5validator')
		# FIXME: remove ignoring of error about alt attribute after resolving #314
		# @todo #109 Check src/main/config/nginx/503.*html by html5validator
		# @todo #695 /series/import/request/{id}: use divs instead of table for elements aligning
		exec html5validator \
			--root "$ROOTDIR/src/main/webapp/WEB-INF/views" \
			--no-langdetect \
			--ignore-re \
				'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
				'Attribute “th:[-a-z]+” not allowed on element "body" at this point' \
				'Attribute “(th|sec|togglz):[-a-z]+” is not serializable' \
				'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
			--ignore \
				'An "img" element must have an "alt" attribute' \
				'Element "option" without attribute "label" must not be empty' \
				'The "width" attribute on the "td" element is obsolete' \
				'Attribute "loading" not allowed on element "img" at this point' \
			--show-warnings
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
	'rflint')
		exec rflint \
			--error=all \
			--ignore TooFewKeywordSteps \
			--configure LineTooLong:130 \
			"$ROOTDIR/src/test/robotframework"
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
		echo >&2 '- check-pom'
		echo >&2 '- checkstyle'
		echo >&2 '- codenarc'
		echo >&2 '- enforcer'
		echo >&2 '- html5validator'
		echo >&2 '- integration-tests'
		echo >&2 '- jest'
		echo >&2 '- pmd'
		echo >&2 '- rflint'
		echo >&2 '- shellcheck'
		echo >&2 '- spotbugs'
		echo >&2 '- unit-tests'
		exit 1
		;;
esac
