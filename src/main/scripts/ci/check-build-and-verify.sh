#!/bin/sh

RUN_ONLY_INTEGRATION_TESTS=no
if [ "$1" = '--only-integration-tests' ]; then
	RUN_ONLY_INTEGRATION_TESTS=yes
fi

. "$(dirname "$0")/common.sh"

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	mvn --batch-mode checkstyle:check -Dcheckstyle.violationSeverity=warning >cs.log 2>&1 || touch CS_FAIL
	mvn --batch-mode pmd:check >pmd.log 2>&1 || touch PMD_FAIL
	mvn --batch-mode license:check >license.log 2>&1 || touch LICENSE_FAIL
	find src -type f -name '*.html' | xargs bootlint >bootlint.log 2>&1 || touch BOOTLINT_FAIL
	mvn --batch-mode jasmine:test >jasmine.log 2>&1 || touch JASMINE_FAIL
	# FIXME: add check for src/main/config/nginx/503.*html
	# TODO: remove ignoring of error about alt attribute after resolving #314
	html5validator \
		--root src/main/webapp/WEB-INF/views \
		--ignore-re 'Attribute “(th|sec|togglz|xmlns):[a-z]+” not allowed' \
			'Attribute “(th|sec|togglz):[a-z]+” is not serializable' \
			'Attribute with the local name “xmlns:[a-z]+” is not serializable' \
			'An "img" element must have an "alt" attribute' \
		--show-warnings \
		>validator.log 2>&1 || touch HTML_FAIL
fi

mvn --batch-mode verify -Dcucumber.options="--plugin pretty --monochrome" >verify.log 2>&1 || touch VERIFY_FAIL

echo
echo 'Build summary:'
echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_status CS_FAIL       'Run CheckStyle'
	print_status PMD_FAIL      'Run PMD'
	print_status LICENSE_FAIL  'Check license headers'
	print_status BOOTLINT_FAIL 'Run bootlint'
	print_status JASMINE_FAIL  'Run JavaScript unit tests'
	print_status HTML_FAIL     'Run html5validator'
fi

print_status VERIFY_FAIL   'Compile and run unit/integration tests'

echo

if [ "$RUN_ONLY_INTEGRATION_TESTS" = 'no' ]; then
	print_log cs.log        'Run CheckStyle'
	print_log pmd.log       'Run PMD'
	print_log license.log   'Check license headers'
	print_log bootlint.log  'Run bootlint'
	print_log jasmine.log   'Run JavaScript unit tests'
	print_log validator.log 'Run html5validator'
fi

print_log verify.log   'Compile and run unit/integration tests'

rm -f cs.log pmd.log license.log bootlint.log jasmine.log validator.log verify.log

if [ -n "$(ls *_FAIL 2>/dev/null)" ]; then
	rm -f CS_FAIL PMD_FAIL LICENSE_FAIL BOOTLINT_FAIL JASMINE_FAIL HTML_FAIL VERIFY_FAIL
	exit 1
fi
